package il.ac.huji.todolist;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


public class TweetToTaskActivity extends Activity {

	private int currTweet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Get the tweet to ask the message about and its text.
		Bundle extras = getIntent().getExtras();
		currTweet = extras.getInt("currTweet");
		final ArrayList<Tweet> tweets = TwitterRetriever.tweets;

		//Check if we reached the end of the list, if so return.
		if (currTweet >= tweets.size()) {
			finish();
			return;
		}

		final String currTweetText = tweets.get(currTweet).text;
		
		final Activity thisActivity = this;

		setContentView(R.layout.tweet_to_task);
		setTitle("A New Tweet Found! (" + (currTweet+1) + "/" + tweets.size() + ")");
		((TextView)findViewById(R.id.tweetText)).setText("\"" + tweets.get(currTweet).text + "\"\n\nCreate a new task from it?");

		final Activity activity = this;

		findViewById(R.id.yes).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(activity, AddNewTodoItemActivity.class);
				intent.putExtra("text", currTweetText);
				activity.startActivityForResult(intent, 1337);
			}
		});


		findViewById(R.id.no).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startNextActivity();
				finish();
			}
		});


		findViewById(R.id.yes2all).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new TweetAdder(thisActivity, currTweet, tweets).execute();
				finish();
			}
		});


		findViewById(R.id.no2all).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

	}


	private void startNextActivity() {
		Intent intent = new Intent(this, TweetToTaskActivity.class);
		intent.putExtra("currTweet", currTweet + 1);
		this.startActivityForResult(intent, 1337);
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1337 && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			String title = (String)extras.get("title");
			Date date = (Date)(extras.get("dueDate"));
			Task t = new Task(0, title, date);
			TodoListManagerActivity.todoDal.insert(t);
		}
		startNextActivity();
		finish();
	}

}

