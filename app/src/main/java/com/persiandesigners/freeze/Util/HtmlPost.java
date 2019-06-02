package com.persiandesigners.freeze.Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by navid on 11/2/2015.
 */
public class HtmlPost extends AsyncTask<String, Void, String> {
    String HTML_response = "",pdText,query;
    ProgressDialog pDialog ;
    Boolean progressbar,err=false;
    Activity act;
    OnTaskFinished onOurTaskFinished;


    public HtmlPost(OnTaskFinished onTaskFinished, Boolean progressDialog,
                    Activity ct,String pgText, String PostingQueries) {
        onOurTaskFinished = onTaskFinished;
        pdText=pgText;
        act=ct;
        progressbar=progressDialog;
        query=PostingQueries;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            if(progressbar){
                pDialog = new ProgressDialog(act);
                if(pdText.length()==0)
                    pDialog.setMessage("در حال ارسال اطلاعات");
                else
                    pDialog.setMessage(pdText);
                pDialog.setCancelable(true);
                pDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]); // enter your url here which to download
            Log.v("this",urls[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(20000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            conn.connect();
            int responseCode=conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    HTML_response+=line;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            err=true;
        }
        return HTML_response;
    }

    @Override
    protected void onPostExecute(String feed) {
        try {
            if(progressbar){
                if (pDialog.isShowing() && pDialog != null) {
                    pDialog.dismiss();
                    pDialog=null;
                }

            }
        } catch (Exception e) {

        }
        if(err){
            onOurTaskFinished.onFeedRetrieved("errordade");
        }else{
            onOurTaskFinished.onFeedRetrieved(feed);
        }

    }
}


