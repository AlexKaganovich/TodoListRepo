package il.ac.huji.todolist;

import java.util.ArrayList;
<<<<<<< HEAD
import java.util.Date;
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
=======

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
>>>>>>> 39ec1c0cf691d8cff2eb1b0e81c6169448943aa0
import android.widget.ListView;


public class TodoListManagerActivity extends Activity {
<<<<<<< HEAD

	private ListView listView;
	private SpecialAdapter listAdapter;
	private ArrayList<Task> items = new ArrayList<Task>();
=======
	
	private ListView listView;
	private SpecialAdapter listAdapter;
	private ArrayList<String> items = new ArrayList<String>();
>>>>>>> 39ec1c0cf691d8cff2eb1b0e81c6169448943aa0

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list_manager);

		listView = (ListView) findViewById(R.id.lstTodoItems);
		listAdapter = new SpecialAdapter(this, android.R.layout.simple_list_item_1, items);
		listView.setAdapter(listAdapter);
<<<<<<< HEAD

		registerForContextMenu(listView);
=======
>>>>>>> 39ec1c0cf691d8cff2eb1b0e81c6169448943aa0
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
<<<<<<< HEAD
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1337 && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			String title = (String)extras.get("title");
			Date date = (Date)(extras.get("dueDate"));
			listAdapter.add(new Task(title, date));
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

		String title = items.get(info2.position).getTitle();
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
			items.remove(info.position);
			listAdapter.notifyDataSetChanged();
			break;
		}
		case (R.id.menuItemCall): {
			String number = items.get(info.position).getTitle().split(" +")[1];
			Intent intent = new Intent(Intent.ACTION_DIAL);
			intent.setData(Uri.parse("tel:" + number));
			startActivity(intent);
			break;
		}
		}
		return true;
=======
		// Inflate the menu; this adds items to the action bar if it is present.
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
        case R.id.menuItemAdd:
        	items.add(((EditText)findViewById(R.id.edtNewItem)).getText().toString());
        	listAdapter.notifyDataSetChanged();
        	return true;
            
        //Delete item
        case R.id.menuItemDelete:
        	Object sel = listView.getSelectedItem();
        	if (sel != null) {
        		listAdapter.remove((String)sel);
        		listAdapter.notifyDataSetChanged();
        	}
        }
        
        return super.onOptionsItemSelected(item);
>>>>>>> 39ec1c0cf691d8cff2eb1b0e81c6169448943aa0
	}

}
