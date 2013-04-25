package il.ac.huji.todolist;

import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;
import android.app.*;
import android.content.*;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;


/**
 * This class retrieves messages from Twitter with the given tag.
 * @author Alex
 *
 */
public class TwitterRetriever extends AsyncTask<Void, Integer, ArrayList<Tweet>> {

	private static final int MAX_TWEETS = 100;

	static ArrayList<Tweet> tweets = null;

	private String tag;
	private ProgressDialog progress;


	TwitterRetriever(String tag) {
		this.tag = tag;
	}


	protected void onPreExecute() {
		progress = new ProgressDialog(TodoListManagerActivity.mainActivity);
		progress.setTitle("");
		progress.setMessage("Receiving tweets with \"#" + tag + "\"...");
		progress.setCancelable(false);
		progress.show();
	}


	@Override
	protected ArrayList<Tweet> doInBackground(Void... params) {

		ArrayList<Tweet> tweetList = new ArrayList<Tweet>();

		//Create the URL to search on.
		URL url;
		try {
			url = new URL("http://search.twitter.com/search.json?q=%23" + tag + "&rpp=" + MAX_TWEETS);
		} catch (MalformedURLException e) {
			return null;
		}

		//Create the stream to read the results.
		InputStream stream = null;
		try {
			stream = ((HttpURLConnection)url.openConnection()).getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		//Get the result array from the stream.
		JSONArray results = getResults(stream);
		if (results == null)
			return null;

		//Traverse the array of results and construct Tweet objects from each result.
		for (int i = 0; i < results.length(); i++) {
			try {
				JSONObject result = results.getJSONObject(i);
				tweetList.add(new Tweet(result.getLong("id"), result.getString("text")));
			} catch (JSONException e) {}
		}

		return tweetList;
	}


	/**
	 * Get the tweet results from a stream, in a JSONArray.
	 * @param stream the stream to get the data from.
	 * @return the array of results.
	 */
	private JSONArray getResults(InputStream stream) {

		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		JSONArray results = null;

		//Concatenate all the read lines into one string.
		String str = "";
		try {

			//Read all the lines, create the JSONObject and get the JSONArray of the results.
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				str += line + "\n";
			}
			results = new JSONObject(str).getJSONArray("results");

		} catch (Exception e) {}
		try {
			br.close();
		} catch (IOException e1) {}
		try {
			stream.close();
		} catch (IOException e1) {}

		return results;
	}



	@Override
	protected void onPostExecute(ArrayList<Tweet> tweets) {

		progress.dismiss();

		//Check that getting the results ended OK.
		if ((TwitterRetriever.tweets = tweets) == null) {
			displayAlert("Error getting new tweets.");
			return;
		}

		//Get the last seen tweet ID.
		SharedPreferences pref = TodoListManagerActivity.mainActivity.getSharedPreferences(TodoListManagerActivity.PREF_NAME, 0);
		long lastID = pref.getLong("#" + tag, 0);
		
		//Remove seen tweets.
		for (int i = tweets.size() - 1; i >= 0; i--) {
			if (tweets.get(i).id <= lastID)
				tweets.remove(i);
		}

		//Check if any tweets remained.
		if (tweets.isEmpty()) {
			displayAlert("No new tweets found.");
			return;
		}
		
		//Save the last tweet ID.
		SharedPreferences.Editor prefEditor = pref.edit();
		prefEditor.putLong("#" + tag, tweets.get(0).id);
		prefEditor.commit();

		//Ask the user to create tasks from the tweets.
		Intent intent = new Intent(TodoListManagerActivity.mainActivity, TweetToTaskActivity.class);
		intent.putExtra("currTweet", 0);
		TodoListManagerActivity.mainActivity.startActivity(intent);
	}
	


	private void displayAlert(String text) {
		final AlertDialog.Builder alert  = new AlertDialog.Builder(TodoListManagerActivity.mainActivity);
		alert.setMessage(text);
		alert.setTitle("");
		alert.setPositiveButton("OK", new OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {}
		});
		alert.setCancelable(true);
		alert.create().show();
	}

}
