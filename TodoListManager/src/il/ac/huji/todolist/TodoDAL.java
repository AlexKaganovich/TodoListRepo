package il.ac.huji.todolist;

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


public class TodoDAL {

	//The items in the list.
	private ArrayList<ITodoItem> items = new ArrayList<ITodoItem>();
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
		
		insert(new Task("a", null));
		
		//Fill the list when first starting the application.
		Cursor cursor =  db.query("todo", new String[] {"_id", "title", "due"},
				null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				long date = cursor.getLong(2);
				Task t = new Task(cursor.getString(1), date == 0 ? null : new Date(date));
				items.add(t);
			} while (cursor.moveToNext());
			listAdapter.notifyDataSetChanged();
		}

		//Create the parse user.
		//ParseUser.enableAutomaticUser();
		//ParseACL.setDefaultACL(new ParseACL(), true);
	}

	/**
	 * Insert a new item.
	 * @param item
	 * @return true if addition was successful.
	 */
	public boolean insert(ITodoItem item) {

		//Add the item to the list.
		listAdapter.add(item);
		listAdapter.notifyDataSetChanged();

		//Add the item to the database.
		ContentValues task = new ContentValues();
		task.put("title", item.getTitle());
		task.put("due", item.getDueDate() == null ? null : item.getDueDate().getTime());
		db.insert("todo", null, task);

		//Add the item to the parse.
		ParseObject parse = new ParseObject("todo");
		parse.put("title", item.getTitle());
		if (item.getDueDate() != null)
			parse.put("due", item.getDueDate().getTime());
		//parse.put("user", ParseUser.getCurrentUser());
		//parse.setACL(new ParseACL(ParseUser.getCurrentUser()));
		parse.saveInBackground();

		return true;
	}

	/**
	 * Update an item.
	 * @param item
	 * @return true if update was successful.
	 */
	public boolean update(ITodoItem item) {

		for (int i = 0; i < items.size(); i++)
			if (items.get(i).getTitle().equals(item.getTitle())) {

				//Update the item in the list.
				items.set(i, item);
				listAdapter.notifyDataSetChanged();

				//Update the item in the database.
				ContentValues task = new ContentValues();
				task.put("title", item.getTitle());
				task.put("due", item.getDueDate() == null ? null : item.getDueDate().getTime());
				db.update("todo", task, "title like '" + item.getTitle() + "'", null);

				//Update the due date in the parse.
				final Date due = item.getDueDate();
				ParseQuery query = new ParseQuery("todo");
				query.whereEqualTo("title", item.getTitle());
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
	public boolean delete(ITodoItem item) {

		for (ITodoItem it: items) {
			if (it.getTitle().equals(item.getTitle())) {
				
				//Remove the item from the list.
				items.remove(it);
				listAdapter.notifyDataSetChanged();

				//Remove the item from the database.
				db.delete("todo", "title like '" + item.getTitle() + "'", null);

				//Remove the item from the parse.
				ParseQuery query = new ParseQuery("todo");
				query.whereEqualTo("title", item.getTitle());
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
	public List<ITodoItem> all() {
		return items;
	}

}
