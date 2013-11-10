package com.columbusagain.camark;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {
	TextView view1;
	ProgressBar progress1;
	List<String> groupList;
	SparseArray<Groupmarks> testwisesparsearray;
	Map<String, Map<String, String>> subjectmarks;
	List<String> index;
	ExpandableListView expListView;
	ExpandableListAdapter explistadapter;
	Activity activity;
	Spinner spinner;
	SparseArray<Groupmarks> subjectwisesparsearray;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		progress1 = (ProgressBar) findViewById(R.id.progressBar1);
		expListView = (ExpandableListView) findViewById(R.id.mark_list);
		Log.d("camark", "start");
		new FetchJson()
				.execute("http://citibytes.columbusagain.com/camark/striptable.php");
		Log.d("camark", "stop");
		activity = this;
		spinner = (Spinner) findViewById(R.id.spinner1);
		List<String> spinner_entries = new ArrayList<String>();
		spinner_entries.add("Subject wise");
		spinner_entries.add("Test wise");
		ArrayAdapter<String> spinneradapter = new ArrayAdapter<String>(
				activity, android.R.layout.simple_spinner_item, spinner_entries);
		spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinneradapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				// TODO Auto-generated method stub
				String choice = parent.getItemAtPosition(pos).toString();
				if (choice == "Test wise") {
					Log.d("DEBUG", "hello");
					explistadapter = new ExpandableListAdapter(activity,
							testwisesparsearray);
					expListView.setAdapter(explistadapter);
				}
				else
				{
					if(subjectwisesparsearray!=null)
					{
					explistadapter = new ExpandableListAdapter(activity,
							subjectwisesparsearray);
					expListView.setAdapter(explistadapter);
					}
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	class FetchJson extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			progress1.setVisibility(View.VISIBLE);
			URL url;
			HttpURLConnection urlconnection = null;
			InputStream in = null;
			String result = null;
			JSONArray json;
			index = new ArrayList<String>();
			groupList = new ArrayList<String>();

			subjectmarks = new HashMap<String, Map<String, String>>();

			try {
				url = new URL(params[0]);
				urlconnection = (HttpURLConnection) url.openConnection();
				in = new BufferedInputStream(urlconnection.getInputStream());
				urlconnection.connect();
				Log.d("camark", "connection created");

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				in.close();
				result = sb.toString();
				reader.close();
				in.close();
				urlconnection.disconnect();
				JSONArray array = new JSONArray(result);
				result = array.getString(0);

				JSONArray jsontable = array.getJSONArray(0);
				JSONArray jsonrow;
				Map<String, String> subjectmarktemp;
				for (int i = 0; i < jsontable.length(); i++) {
					jsonrow = jsontable.getJSONArray(i);
					for (int j = 0; j < jsonrow.length(); j++) {
						if (i == 0) {
							index.add(jsonrow.getString(j)); // collect the
																// index alone
						} else if (i > 0 && j == 0) // collect the course names
													// alone
						{
							groupList.add(jsonrow.getString(j));
						}
					}
				}
				for (int i = 1; i < jsontable.length(); i++) {
					jsonrow = jsontable.getJSONArray(i);
					subjectmarktemp = new HashMap<String, String>();
					for (int j = 1; j < jsonrow.length(); j++) {
						subjectmarktemp.put(index.get(j), jsonrow.getString(j));
					}
					subjectmarks.put(groupList.get(i - 1), subjectmarktemp);
				}

				Groupmarks testwisemarks;
				JSONArray indexjsonarray = new JSONArray();
				indexjsonarray = jsontable.getJSONArray(0);
				Marks marksobject = new Marks();
				testwisesparsearray = new SparseArray<Groupmarks>();
				for (int i = 1; i < indexjsonarray.length(); i++) {
					testwisemarks = new Groupmarks(indexjsonarray.getString(i));
					Log.d("DEBUG", indexjsonarray.getString(i));
					marksobject = new Marks();
					Map<String, String> map = new HashMap<String, String>();
					for (int j = 1; j < jsontable.length(); j++) {
						JSONArray jsonarray = new JSONArray();
						jsonarray = jsontable.getJSONArray(j);
						marksobject.child.put(jsonarray.getString(0),
								jsonarray.getString(i));
					}

					testwisemarks.marks.add(marksobject);
					testwisesparsearray.append(i - 1, testwisemarks);
				}

				subjectwisesparsearray = new SparseArray<Groupmarks>();
				Marks temp;
				Map<String, String> tempmap = new HashMap<String, String>();
				Groupmarks groupMarks = null;
				int i = 0;
				for (Entry entry : subjectmarks.entrySet()) {
					groupMarks = new Groupmarks((String) entry.getKey());
					temp = new Marks();
					tempmap = subjectmarks.get(entry.getKey());
					for (Entry subentry : tempmap.entrySet()) {
						String key = subentry.getKey().toString();
						String value = subentry.getValue().toString();
						temp.child.put(key, value);
					}
					groupMarks.marks.add(temp);
					subjectwisesparsearray.append(i, groupMarks);
					i++;
				}

				// Log.d("DEBUG",subjectmarks.toString());

				explistadapter = new ExpandableListAdapter(activity, subjectwisesparsearray);

			} catch (Exception e) {
				return null;
			}
			return result;

		}

		@Override
		protected void onPostExecute(String result) {
			// view1.setText(result);
			expListView.setAdapter(explistadapter);
			expListView.setOnChildClickListener(new OnChildClickListener() {

				@Override
				public boolean onChildClick(ExpandableListView parent, View v,
						int groupPosition, int childPosition, long id) {

					// TODO Auto-generated method stub
					return false;
				}
			});
			progress1.setVisibility(View.INVISIBLE);
		}
	}
}