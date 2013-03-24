package il.ac.huji.todolist;

import java.util.List;
import android.content.Context;
import android.graphics.Color;
<<<<<<< HEAD
import android.view.LayoutInflater;
=======
>>>>>>> 39ec1c0cf691d8cff2eb1b0e81c6169448943aa0
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

<<<<<<< HEAD
public class SpecialAdapter extends ArrayAdapter<Task> {

	public SpecialAdapter(Context context, int textViewResourceId, List<Task> objects) {
		super(context, textViewResourceId, objects);
	}

	/**
	 * Set the row text colors to red/black according to date.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Task task = getItem(position);
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.list_row, null);
		
		TextView title = (TextView)view.findViewById(R.id.txtTodoTitle),
				date = (TextView)view.findViewById(R.id.txtTodoDueDate);
		
		title.setText(task.getTitle());
		date.setText(task.getDateStr());
		
		boolean datePassed = task.isDue();
		title.setTextColor(datePassed ? Color.RED : Color.BLACK);
		date.setTextColor(datePassed ? Color.RED : Color.BLACK);

		return view;
	}
=======
public class SpecialAdapter extends ArrayAdapter<String> {
     
    public SpecialAdapter(Context context, int textViewResourceId, List<String> objects) {
        super(context, textViewResourceId, objects);
    }
 
    /**
     * Set the row text colors to red/blue alternately.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View view = super.getView(position, convertView, parent);
      ((TextView)view).setTextColor(position % 2 == 0 ? Color.RED : Color.BLUE);
      return view;
    }
>>>>>>> 39ec1c0cf691d8cff2eb1b0e81c6169448943aa0

}