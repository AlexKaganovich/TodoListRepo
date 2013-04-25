package il.ac.huji.todolist;

import java.util.Date;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class Task {
	
	final String title;
	final Date date;
	final ImageView thumb = null;
	Bitmap img;
	long id;
	
	Task(long id, String s, Date d) {
		//Remove quotes from string to prevent SQL injection and various bugs.
		title = s.replaceAll("[\'|\"]+", "");
		date = d;
		this.id = id;
	}

	/**
	 * Generate the date string from the date object.
	 * @return
	 */
	@SuppressWarnings("deprecation")
	String getDateStr() {
		return date == null ? "No due date" :
			addZero(date.getDate()) + "/" + addZero(date.getMonth() + 1) + "/" + (date.getYear() + 1900);
	}
	
	private static String addZero(int num) {
		return (num < 10 ? "0" : "") + num; 
	}
	
	/**
	 * Check if the date has already passed.
	 * @return
	 */
	static boolean isDue(Date date) {
		return date == null ? false : new Date().after(date);
	}

}
