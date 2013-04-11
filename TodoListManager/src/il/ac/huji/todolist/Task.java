package il.ac.huji.todolist;

import java.util.Date;

public class Task implements ITodoItem {
	
	private final String title;
	private final Date date;
	
	Task(String s, Date d) {
		title = s;
		date = d;
	}
	
	public String toString() {
		return title + " " + date;
	}
	
	public String getTitle() {
		return title;
	}
	
	/**
	 * Generate the date string from the date object.
	 * @return
	 */
	@SuppressWarnings("deprecation")
	static String getDateStr(Date date) {
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

	@Override
	public Date getDueDate() {
		return date;
	}

}
