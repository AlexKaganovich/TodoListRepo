package il.ac.huji.todolist;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;


public class TodoListManagerActivity extends Activity {
	
	private ListView listView;
	private SpecialAdapter listAdapter;
	private ArrayList<String> items = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list_manager);

		listView = (ListView) findViewById(R.id.lstTodoItems);
		listAdapter = new SpecialAdapter(this, android.R.layout.simple_list_item_1, items);
		listView.setAdapter(listAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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
	}

}
