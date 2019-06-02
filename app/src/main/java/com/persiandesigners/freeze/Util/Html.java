package com.persiandesigners.freeze.Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by navid on 11/2/2015.
 */
public class Html extends AsyncTask<String, Void, String> {
    String HTML_response = "",pdText;
    ProgressDialog pDialog ;
    Boolean progressbar,err=false;
    Activity act;

    OnTaskFinished onOurTaskFinished;


    public Html(OnTaskFinished onTaskFinished,Boolean progressDialog ,Activity ct,String pgText) {
        onOurTaskFinished = onTaskFinished;
        pdText=pgText;
        act=ct;
        progressbar=progressDialog;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(progressbar){
            pDialog = new ProgressDialog(act);
            if(pdText.length()==0)
                pDialog.setMessage("در حال دریافت اطلاعات..");
            else
                pDialog.setMessage(pdText);
            pDialog.setCancelable(true);
            pDialog.show();
        }
    }

    @Override
    protected String doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]); // enter your url here which to download
            Log.v("this",urls[0]);
            URLConnection conn = url.openConnection();

            // open the stream and put it into BufferedReader
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String inputLine;

            while ((inputLine = br.readLine()) != null) {
                // System.out.println(inputLine);
                HTML_response += inputLine;
            }
            br.close();
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


