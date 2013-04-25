package il.ac.huji.todolist;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class TodoDAL {

	//The items in the list.
	private ArrayList<Task> items = new ArrayList<Task>();
	SpecialAdapter listAdapter;

	//The database.
	private SQLiteDatabase db;

	/**
	 * Constructor.
	 * @param c
	 */
	public TodoDAL(Context c) {

		//Create the database.
		DBHelper helper = new DBHelper(c);
		listAdapter = new SpecialAdapter(c, android.R.layout.simple_list_item_1, items);
		db = helper.getWritableDatabase();
		helper.onCreate(db);

		//Fill the list when first starting the application.
		Cursor cursor =  db.query("todo", new String[] {"_id", "title", "due"},
				null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				long dueDate;
				try {
					dueDate = cursor.getLong(2);
				} catch (Exception e) {
					dueDate = 0;
				}

				long taskID = cursor.getLong(0);
				Task t = new Task(taskID, cursor.getString(1), dueDate == 0 ? null : new Date(dueDate));
				items.add(t);

				//Get the task image
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				File file = new File(TodoListManagerActivity.getDir(), "taskImg" + taskID);
				t.img = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

			} while (cursor.moveToNext());
			listAdapter.notifyDataSetChanged();
		}
	}
	

	/**
	 * Insert a new item.
	 * @param item
	 * @return true if addition was successful.
	 */
	public boolean insert(Task item) {

		//Check that item with a similar title doesn't already exist.
		if (db.query("todo", new String[] {"_id", "title", "due"},
				"title = '" + item.title + "'", null, null, null, null).moveToFirst())
			return false;

		//Add the item to the list.
		listAdapter.add(item);
		listAdapter.notifyDataSetChanged();

		//Add the item to the database.
		ContentValues task = new ContentValues();
		task.put("title", item.title);
		if (item.date != null)
			task.put("due", item.date == null ? null : item.date.getTime());
		item.id = db.insert("todo", null, task);

		//Add the item to the parse.
		ParseObject parse = new ParseObject("todo");
		parse.put("title", item.title);
		if (item.date != null)
			parse.put("due", item.date.getTime());
		parse.saveInBackground();

		return true;
	}

	/**
	 * Update an item.
	 * @param item
	 * @return true if update was successful.
	 */
	public boolean update(Task item) {

		for (int i = 0; i < items.size(); i++)
			if (items.get(i).title.equals(item.title)) {

				//Update the item in the list.
				items.set(i, item);
				listAdapter.notifyDataSetChanged();

				//Update the item in the database.
				ContentValues task = new ContentValues();
				task.put("title", item.title);
				task.put("due", item.date == null ? null : item.date.getTime());
				db.update("todo", task, "title like '" + item.title + "'", null);

				//Update the due date in the parse.
				final Date due = item.date;
				ParseQuery query = new ParseQuery("todo");
				query.whereEqualTo("title", item.title);
				query.findInBackground(new FindCallback() {
					public void done(List<ParseObject> objects, ParseException e) {
						if (e == null && !objects.isEmpty()) {
							ParseObject obj = objects.get(0);
							if (due != null)
								obj.put("due", due.getTime());
							else
								obj.remove("due");
							obj.saveInBackground();
						}
					}
				});

				return true;
			}

		return false;
	}

	/**
	 * Delete an item.
	 * @param item
	 * @return true if deletion was successful.
	 */
	public boolean delete(Task item) {

		for (Task it: items) {
			if (it.title.equals(item.title)) {

				//Remove the item from the list.
				items.remove(it);
				listAdapter.notifyDataSetChanged();

				//Remove the item from the database.
				db.delete("todo", "title like '" + item.title + "'", null);
				
				//Delete task image from the device.
				if (item.img != null) {
					File file = new File(TodoListManagerActivity.getDir(), "taskImg" + item.id);
					file.delete();
				}

				//Remove the item from the parse.
				ParseQuery query = new ParseQuery("todo");
				query.whereEqualTo("title", item.title);
				query.findInBackground(new FindCallback() {
					public void done(List<ParseObject> objects, ParseException e) {
						if (e == null && !objects.isEmpty()) {
							ParseObject obj = objects.get(0);
							obj.deleteInBackground();
						}
					}
				});

				return true;
			}
		}

		return false;
	}

	/**
	 * Get all items in the list.
	 * @return all items in the list.
	 */
	public List<Task> all() {
		return items;
	}


}
