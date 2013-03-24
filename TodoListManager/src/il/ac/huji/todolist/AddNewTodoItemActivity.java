package il.ac.huji.todolist;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.DatePicker;


public class AddNewTodoItemActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_new_todo_item_activity);
		setTitle("Add New Item");

		
		findViewById(R.id.btnOK).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				String itemName = ((EditText)findViewById(R.id.edtNewItem)).getText().toString();
				
				if (itemName == null || "".equals(itemName)) {
					setResult(RESULT_CANCELED);
					finish();
				}
				
				else {
					Intent resultIntent = new Intent();
					resultIntent.putExtra("title", itemName);
					DatePicker dp = (DatePicker)findViewById(R.id.datePicker);
					
					@SuppressWarnings("deprecation")
					Date date = new Date(dp.getYear() - 1900,
							dp.getMonth(),
							dp.getDayOfMonth());
					resultIntent.putExtra("dueDate", date);
					
					setResult(RESULT_OK, resultIntent);
					finish();
				}
			}
		});

		
		findViewById(R.id.btnCancel).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
	}

}
