package il.ac.huji.todolist;

import java.util.Date;

public class Task {
	
	private final String title;
	private final Date date;
	
	Task(String s, Date d) {
		title = s;
		date = d;
	}
	
	String getTitle() {
		return title;
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
	
	private String addZero(int num) {
		return (num < 10 ? "0" : "") + num; 
	}
	
	
	/**
	 * Check if the date has already passed.
	 * @return
	 */
	boolean isDue() {
		return date == null ? false : new Date().after(date);
	}

}
