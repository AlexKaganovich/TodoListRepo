package il.ac.huji.todolist;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class CustomizeActivity extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

}
