package il.ac.huji.todolist;

import java.util.Date;
import com.parse.Parse;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;


public class TodoListManagerActivity extends Activity {

	private ListView listView;
	private TodoDAL todoDal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list_manager);
		
		Parse.initialize(this, getResources().getString(R.string.parseApplication),
				getResources().getString(R.string.clientKey));

		todoDal = new TodoDAL(this);

		listView = (ListView) findViewById(R.id.lstTodoItems);
		listView.setAdapter(todoDal.listAdapter);

		registerForContextMenu(listView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1337 && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			String title = (String)extras.get("title");
			Date date = (Date)(extras.get("dueDate"));
			Task t = new Task(title, date);
			todoDal.insert(t);
		}
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
		case R.id.menuItemAdd:
			Intent intent = new Intent(this, AddNewTodoItemActivity.class);
			startActivityForResult(intent, 1337);
			break;
		}

		return true;
	}



	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo info) {
		super.onCreateContextMenu(menu, v, info);
		getMenuInflater().inflate(R.menu.context_menu, menu);
		AdapterContextMenuInfo info2 = (AdapterContextMenuInfo)info;

		String title = todoDal.all().get(info2.position).getTitle();
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
		case (R.id.menuItemCall): {
			String number = todoDal.all().get(info.position).getTitle().split(" +")[1];
			Intent intent = new Intent(Intent.ACTION_DIAL);
			intent.setData(Uri.parse("tel:" + number));
			startActivity(intent);
			break;
		}
		}
		return true;
	}

}
