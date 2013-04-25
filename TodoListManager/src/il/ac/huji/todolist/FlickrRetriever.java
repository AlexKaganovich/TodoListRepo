package il.ac.huji.todolist;

import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;
import android.app.*;
import android.content.*;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;


/**
 * This class searches Flickr for images with the given name.
 * @author Alex
 *
 */
public class FlickrRetriever extends AsyncTask<Void, Integer, ArrayList<Bitmap>> {

	private static final int MAX_IMAGES = 20;
	
	private ProgressDialog progress;
	private String term;
	private ImageAdapter imAdapter;


	@SuppressWarnings("deprecation")
	FlickrRetriever(String searchTerm, ImageAdapter imAdapter) {
		this.term = URLEncoder.encode(searchTerm);
		this.imAdapter = imAdapter;
	}


	protected void onPreExecute() {
		progress = new ProgressDialog(TodoListManagerActivity.mainActivity);
		progress.setTitle("");
		progress.setMessage("Searching Flickr for \"" + term + "\"...");
		progress.setCancelable(false);
		progress.show();
	}


	@Override
	protected ArrayList<Bitmap> doInBackground(Void... params) {

		ArrayList<Bitmap> imgList = new ArrayList<Bitmap>();

		//Create the URL to search on.
		URL url;
		try {
			String flickrKey = TodoListManagerActivity.mainActivity.getResources().getString(R.string.flickrKey);
			url = new URL("http://www.flickr.com/services/rest/?method=flickr.photos.search&format=json&api_key=" +
					flickrKey + "&text=" + term + "&per_page=" + MAX_IMAGES);
		} catch (MalformedURLException e) {
			return null;
		}

		//Create the stream to read the results.
		InputStream stream = null;
		try {
			stream = ((HttpURLConnection)url.openConnection()).getInputStream();
		} catch (IOException e) {
			return null;
		}

		//Get the result array from the stream.
		JSONArray results = getResults(stream);
		if (results == null)
			return null;

		//Traverse the array of results (addresses of photos) and download the photos.
		for (int i = 0; i < results.length(); i++) {
			try {
				JSONObject result = results.getJSONObject(i);
				
				URL downloadURL = new URL("http://farm" + result.getInt("farm") + ".staticflickr.com/" +
						result.getString("server") + "/" + result.getString("id") + "_" + result.getString("secret") + "_m.jpg");
				
				Bitmap image = downloadImage(downloadURL);
				if (image != null) {
					image = Bitmap.createScaledBitmap(image, 70, 70, true); //Resize the image.
					imgList.add(image);
				}

			} catch (Exception e) {}
		}
		
		return imgList;
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
			
			//For some reason, the string is wrapped with "jsonFlickrApi( ... )", we'll have to remove the wrap.
			str = str.substring(14, str.length() - 1);
			
			results = new JSONObject(str).getJSONObject("photos").getJSONArray("photo");

		} catch (Exception e) {}
		try {
			br.close();
		} catch (IOException e1) {}
		try {
			stream.close();
		} catch (IOException e1) {}

		return results;
	}
	
	
	/**
	 * Download the image from the given URL.
	 * @param url
	 * @return
	 */
	private Bitmap downloadImage(URL url) {
		
		Bitmap bitmap = null;
		InputStream in = null;
		
		try {
			in = url.openStream();
			bitmap = BitmapFactory.decodeStream(in);
			
		} catch (Exception e) {}
		try {
			in.close();
		} catch (IOException e) {}
		
		return bitmap;
	}



	@Override
	protected void onPostExecute(ArrayList<Bitmap> imgList) {
		
		progress.dismiss();

		//Check that getting the results ended OK.
		if (imgList == null) {
			displayAlert("Error getting images.");
			return;
		}

		//Check if list isn't empty.
		if (imgList.isEmpty()) {
			displayAlert("No images found.");
			return;
		}

		//TODO add images to grid
		imAdapter.imgList = imgList;
		imAdapter.notifyDataSetChanged();

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
