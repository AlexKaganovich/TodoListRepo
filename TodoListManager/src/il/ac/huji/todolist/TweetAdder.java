package il.ac.huji.todolist;

import java.util.*;
import android.app.*;
import android.os.AsyncTask;


/**
 * This class adds all tweets to the list of tasks if user clicks "Yes to All".
 * @author Alex
 *
 */
public class TweetAdder extends AsyncTask<Void, Integer, Void> {

	private Activity activity;
	private int currTweet;
	ArrayList<Tweet> tweets;
	private ProgressDialog progress;


	TweetAdder(Activity activity, int currTweet, ArrayList<Tweet> tweets) {
		this.currTweet = currTweet;
		this.tweets = tweets;
		this.activity = activity;
	}


	protected void onPreExecute() {
		progress = new ProgressDialog(TodoListManagerActivity.mainActivity);
		progress.setTitle("");
		progress.setMessage("Adding new tasks...");
		progress.setCancelable(false);
		progress.show();
		publishProgress(currTweet+1);
	}


	@Override
	protected Void doInBackground(Void... params) {
		for (int i = currTweet; i < tweets.size(); i++) {
			final Task t = new Task(0, tweets.get(i).text, null);
			activity.runOnUiThread(new Runnable() {
				public void run() {
					TodoListManagerActivity.todoDal.insert(t);
				}
			});
		}
		return null;
	}



	@Override
	protected void onPostExecute(Void params) {
		progress.dismiss();
	}

}
