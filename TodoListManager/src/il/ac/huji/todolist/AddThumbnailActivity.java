package il.ac.huji.todolist;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import com.parse.ParseFile;
import com.parse.ParseObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;


public class AddThumbnailActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);		
		setContentView(R.layout.add_thumbnail_activity);
		setTitle("Add Thumbnail");

		int taskNum = getIntent().getIntExtra("taskNum", -1);
		final Task task = TodoListManagerActivity.todoDal.all().get(taskNum);

		GridView grid = (GridView) findViewById(R.id.grid);
		final ImageAdapter imAdapter = new ImageAdapter(this);
		grid.setAdapter(imAdapter);

		grid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {

				task.img = imAdapter.imgList.get(pos);
				TodoListManagerActivity.todoDal.listAdapter.notifyDataSetChanged();
				
				String imgName = "taskImg" + task.id;
				
				//Save image to the device storage.
				FileOutputStream out = null;
				try {
					File file = new File(TodoListManagerActivity.getDir(), imgName);
					out = new FileOutputStream(file);
					task.img.compress(Bitmap.CompressFormat.PNG, 100, out);
				} catch (Exception e) {}
				try {
					out.close();
				} catch (Exception e) {}
				
				//Save image to the Parse.
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				task.img.compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte[] imgBytes = stream.toByteArray();
				ParseFile file = new ParseFile(imgName, imgBytes);
				file.saveInBackground();
				
				finish();
			}
		});

		findViewById(R.id.btnSearch).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String searchTerm = ((EditText)findViewById(R.id.searchTerm)).getText().toString();
				if (searchTerm != null && searchTerm.length() > 0) {
					new FlickrRetriever(searchTerm, imAdapter).execute();
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
