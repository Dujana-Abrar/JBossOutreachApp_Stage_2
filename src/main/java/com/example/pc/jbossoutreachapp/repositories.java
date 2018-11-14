package com.example.pc.jbossoutreachapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

public class repositories extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog ProgDialog;
    private ListView listview;

    private static String url = "https://api.github.com/orgs/JBossOutreach/repos";

    String url1;

    ArrayList<HashMap<String,String>> RepoDetails;


    public void link(View view)
    {
        TextView text = findViewById(R.id.Repolink);
        String url = text.getText().toString();

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);

    }



    public void Contributors(View view)
    {
        Intent intent = new Intent(this, contributors.class);
        Bundle bundle = new Bundle();
        bundle.putString("url", url1);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repositories);

        RepoDetails = new ArrayList<>();

        listview = this.findViewById(R.id.list);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = view.findViewById(R.id.RepositoryName);
                String text = textView.getText().toString();
                Log.e("nameeeeee", "Name is  == "+text);
                url1 = "https://api.github.com/repos/JBossOutreach/"+text+"/contributors";
            }
        });
        new GetContacts().execute();

    }


    private class GetContacts extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ProgDialog = new ProgressDialog(repositories.this);
            ProgDialog.setMessage("Please wait...");
            ProgDialog.setCancelable(false);
            ProgDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            HttpHandler sh = new HttpHandler();

            String Json_String = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + Json_String);

            if(Json_String != null)
            {
                try
                {
                  JSONArray array = new JSONArray(Json_String);
                    for(int i = 0; i < array.length(); i++)
                    {
                        JSONObject ob = array.getJSONObject(i);

                        String name = ob.getString("name");

                        JSONObject owner = ob.getJSONObject("owner");
                        String link = owner.getString("html_url");

                        HashMap<String, String> contact = new HashMap<>();

                        contact.put("name", name);
                        contact.put("link", link+"/"+name);

                        RepoDetails.add(contact);
                    }
                }
                catch(final JSONException e)
                {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json Parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();                        }
                    });
                }
            }

            else
            {
                Log.e(TAG, "Couldn't get Json from server");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server ", Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(ProgDialog.isShowing())
            {
                ProgDialog.dismiss();
            }

            ListAdapter adapter = new SimpleAdapter(
                    repositories.this, RepoDetails, R.layout.repo_list_item, new String[]
                    {"name", "link"}, new int[]{R.id.RepositoryName, R.id.Repolink});

            listview.setAdapter(adapter);
        }

    }

}
