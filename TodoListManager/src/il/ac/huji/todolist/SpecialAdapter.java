package il.ac.huji.todolist;

import java.util.List;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SpecialAdapter extends ArrayAdapter<ITodoItem> {

	public SpecialAdapter(Context context, int textViewResourceId, List<ITodoItem> objects) {
		super(context, textViewResourceId, objects);
	}

	/**
	 * Set the row text colors to red/black according to date.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ITodoItem task = getItem(position);
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.list_row, null);
		
		TextView title = (TextView)view.findViewById(R.id.txtTodoTitle),
				date = (TextView)view.findViewById(R.id.txtTodoDueDate);
		
		title.setText(task.getTitle());
		date.setText(Task.getDateStr(task.getDueDate()));
		
		boolean datePassed = Task.isDue(task.getDueDate());
		title.setTextColor(datePassed ? Color.RED : Color.BLACK);
		date.setTextColor(datePassed ? Color.RED : Color.BLACK);

		return view;
	}

}