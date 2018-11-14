package com.example.pc.jbossoutreachapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

public class contributors extends AppCompatActivity {


    private String TAG = contributors.class.getSimpleName();
    ArrayList<HashMap<String, String>> names;
    ListView lv;
    private ProgressDialog progressDialog;
    static String url;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contributors);
        names = new ArrayList<>();
        lv = findViewById(R.id.list2);

        Bundle bundle = getIntent().getExtras();
        System.out.print(url);
        url = bundle.getString("url");

        new getcontrib().execute();
    }


    private class getcontrib extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(contributors.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            HttpHandler hd = new HttpHandler();
            String Json_result = hd.makeServiceCall(url);
            Log.e(TAG, "Response from url: " + Json_result);

            if (Json_result != null) {
                try {
                    JSONArray array = new JSONArray(Json_result);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        String contributor_name = obj.getString("login");

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("name", contributor_name);


                        names.add(hashMap);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json Parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            ListAdapter adapter = new SimpleAdapter(
                    contributors.this, names, R.layout.contributor_list_items, new String[]
                    {"name"}, new int[]{R.id.ContributorsName});
            lv.setAdapter(adapter);
            ((SimpleAdapter) adapter).notifyDataSetChanged();
        }

    }

}
