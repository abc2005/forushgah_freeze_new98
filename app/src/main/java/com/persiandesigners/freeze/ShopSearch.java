package com.persiandesigners.freeze;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by navid on 9/8/2015.
 */
public class ShopSearch extends AsyncTask<String, String, String> {
    JSONObject json;
    AutoCompleteTextView etSearch;
    Activity act;
    String catId;


    public ShopSearch(AutoCompleteTextView et, Activity ac, String catId) {
        super();
        etSearch = et;
        act = ac;
        this.catId=catId;
    }

    protected String doInBackground(String... args) {
        String jsonStr = null;
        String urls = act.getString(R.string.url) + "/getPredictsShopName.php?catId="+catId;

        try {
            URL url = new URL(urls);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(20000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("w", etSearch.getText().toString());
            String query = builder.build().getEncodedQuery();
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            conn.connect();
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                jsonStr="";
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    jsonStr += line;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (jsonStr != null) {
            jsonStr=jsonStr.replaceAll("null","");
            Log.v("this", jsonStr);
            try {
                this.json = new JSONObject(jsonStr);
            } catch (JSONException var5) {
                Log.v("this",var5.getMessage());
            }
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    protected void onPostExecute(String file_url) {
        int sdk = Build.VERSION.SDK_INT;
        if (json != null) {
            try {

                JSONArray jArray = json.getJSONArray("contacts");
                final String[] Predics = new String[jArray.length()];
                final String[] Ides = new String[jArray.length()];
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject tempObj = jArray.getJSONObject(i);
//                    Predics[i] = new String(tempObj.getString("name").getBytes("ISO-8859-1"), "UTF-8");
                    Predics[i] = tempObj.getString("name");
                    Ides[i] = tempObj.getString("id");
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(act, R.layout.predict, Predics);
                if (sdk >= 13) {
                    Point pointSize = new Point();
                    act.getWindowManager().getDefaultDisplay().getSize(pointSize);
                    etSearch.setDropDownWidth(pointSize.x);
                }
                adapter.notifyDataSetChanged();
                etSearch.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                Log.v("this", "size " + adapter.getCount());
                etSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                        Intent in = new Intent(act, Subcats.class);
                        in.putExtra("catId", Ides[position]);
                        in.putExtra("onvan", Predics[position]);
                        act.startActivity(in);

                        etSearch.setText("");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Log.v("this", e.getMessage());
            }
        }

    }
}
