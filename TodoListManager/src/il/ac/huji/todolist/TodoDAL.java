package il.ac.huji.todolist;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class TodoDAL {

	private ArrayList<ITodoItem> items = new ArrayList<ITodoItem>();
	SpecialAdapter listAdapter;

	private DBHelper helper;
	private SQLiteDatabase db;

	public TodoDAL(Context c) {
		helper = new DBHelper(c);
		listAdapter = new SpecialAdapter(c, android.R.layout.simple_list_item_1, items);
		db = helper.getWritableDatabase();

		//Fill the list when first starting the application.
		Cursor cursor =  db.query("todo", new String[] {"_id", "title", "due"},
				"title = '' or title != ''", null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				long date = cursor.getLong(2);
				Task t = new Task(cursor.getString(1), date == 0 ? null : new Date(date));
				items.add(t);
			} while (cursor.moveToNext());
			listAdapter.notifyDataSetChanged();
		}

		/*ParseUser.enableAutomaticUser();
		ParseACL.setDefaultACL(new ParseACL(), true);*/
	}

	public boolean insert(ITodoItem item) {
		listAdapter.add(item);
		ContentValues task = new ContentValues();
		task.put("title", item.getTitle());
		task.put("due", item.getDueDate().getTime());
		db.insert("todo", null, task);
		listAdapter.notifyDataSetChanged();

		//Store to the Parse.
		ParseObject parse = new ParseObject("todo");
		parse.put("title", item.getTitle());
		if (item.getDueDate() != null)
			parse.put("due", item.getDueDate().getTime());
		//parse.put("user", ParseUser.getCurrentUser());
		//parse.setACL(new ParseACL(ParseUser.getCurrentUser()));
		parse.saveInBackground();

		return true;
	}

	public boolean update(ITodoItem item) {

		for (int i = 0; i < items.size(); i++)
			if (items.get(i).getTitle().equals(item.getTitle())) {

				items.set(i, item);

				ContentValues task = new ContentValues();
				task.put("title", item.getTitle());
				task.put("due", item.getDueDate() == null ? null : item.getDueDate().getTime());
				db.update("todo", task, "title like '" + item.getTitle() + "'", null);
				listAdapter.notifyDataSetChanged();

				final Date due = item.getDueDate();
				ParseQuery query = new ParseQuery("todo");
				query.whereStartsWith("title", "");
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

	public boolean delete(ITodoItem item) {
		
		if (!items.remove(item))
			return false;

		db.delete("todo", "title like '" + item.getTitle() + "'", null);
		listAdapter.notifyDataSetChanged();

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

	public List<ITodoItem> all() {
		return items;
	}

}
