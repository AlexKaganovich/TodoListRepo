package il.ac.huji.todolist;

import java.util.List;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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

}