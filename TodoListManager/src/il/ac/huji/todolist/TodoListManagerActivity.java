package il.ac.huji.todolist;

import java.util.Date;

import com.parse.Parse;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;


public class TodoListManagerActivity extends Activity {
	
	static final String PREF_NAME = "TodoListPref";
	static final String TAG = "tag";

	private ListView listView;
	static TodoDAL todoDal;
	static Activity mainActivity;
	static String tag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list_manager);
		mainActivity = this;

		Parse.initialize(this, getResources().getString(R.string.parseApplication),
				getResources().getString(R.string.clientKey));

		todoDal = new TodoDAL(this);

		listView = (ListView) findViewById(R.id.lstTodoItems);
		listView.setAdapter(todoDal.listAdapter);
		registerForContextMenu(listView);
		
		//Get the tag. If no tag is defined, get "todoapp".
		SharedPreferences pref = TodoListManagerActivity.mainActivity.getSharedPreferences(PREF_NAME, 0);
		tag = pref.getString(TAG, "todoapp");

		new TwitterRetriever(tag).execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}


	/**
	 * Event Handling for Individual menu item selected
	 * Identify single menu item by it's id
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {

		//Add item
		case R.id.menuItemAdd: {
			Intent intent = new Intent(this, AddNewTodoItemActivity.class);
			startActivityForResult(intent, 1337);
			break;
		}

		//Customize
		case R.id.menuCustom: {
			Intent intent = new Intent(this, CustomizeActivity.class);
			startActivityForResult(intent, 1337);
			break;
		}
		}

		return true;
	}



	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo info) {
		super.onCreateContextMenu(menu, v, info);
		getMenuInflater().inflate(R.menu.context_menu, menu);
		AdapterContextMenuInfo info2 = (AdapterContextMenuInfo)info;

		String title = todoDal.all().get(info2.position).title;
		menu.setHeaderTitle(title);

		if (title.startsWith("Call "))
			menu.getItem(1).setTitle(title);
		else
			menu.removeItem(R.id.menuItemCall);
	}


	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		switch (item.getItemId()) {
		case (R.id.menuItemDelete): {
			todoDal.delete(todoDal.all().get(info.position));
			break;
		}
		case (R.id.menuThumb): {
			Intent intent = new Intent(this, AddThumbnailActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.putExtra("taskNum", info.position);
			startActivityForResult(intent, 1337);
			break;
		}
		case (R.id.menuItemCall): {
			String number = todoDal.all().get(info.position).title.split(" +")[1];
			Intent intent = new Intent(Intent.ACTION_DIAL);
			intent.setData(Uri.parse("tel:" + number));
			startActivity(intent);
			break;
		}
		}
		return true;
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1337 && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			String title = (String)extras.get("title");
			Date date = (Date)(extras.get("dueDate"));
			Task t = new Task(0, title, date);
			TodoListManagerActivity.todoDal.insert(t);
		}
	}
	
	
	static String getDir() {
		return mainActivity.getFilesDir().getPath().toString();
	}

}
