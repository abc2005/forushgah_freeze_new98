package com.persiandesigners.freeze;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.persiandesigners.freeze.Util.Html;
import com.persiandesigners.freeze.Util.Lv_adapter;
import com.persiandesigners.freeze.Util.MyToast;
import com.persiandesigners.freeze.Util.OnTaskFinished;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


public class MainActivity extends Activity implements SurfaceHolder.Callback {
    private MediaPlayer mp = null;
    SurfaceView mSurfaceView = null;

    boolean loadCoompleted = false;
    int secounds = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Func.isInternet(this))
            new getXml().execute();
        else {
            SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
            SharedPreferences.Editor pref = settings.edit();
            pref.putString("homedata", "");
            pref.commit();
            loadCoompleted = true;
        }

        if (Func.getUid(this).equals("0") && getResources().getBoolean(R.bool.registerOnFpage)) {
            Intent mInHome = new Intent(MainActivity.this, Login.class);
            MainActivity.this.startActivity(mInHome);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            MainActivity.this.finish();
        } else {

            ///agar anim bud
            if (getResources().getBoolean(R.bool.display_gif_as_fpage)) {
                gif();
                ImageView fpage = (ImageView) findViewById(R.id.fpageimg);
                fpage.setVisibility(View.GONE);
            } else {//agar aks bud fpage
                final Handler handler = new Handler();
                final boolean[] mStopHandler = {false};
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        secounds++;
                        if (loadCoompleted && secounds >= 3) {
                            Intent mInHome = new Intent(MainActivity.this, Home.class);
                            MainActivity.this.startActivity(mInHome);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            MainActivity.this.finish();
                            mStopHandler[0] = true;
                        }
                        if (!mStopHandler[0]) {
                            handler.postDelayed(this, 1000);
                        }
                    }
                };
                handler.post(runnable);
            }

            SharedPreferences settings = getSharedPreferences("shinaweb4", MODE_PRIVATE);
            if (settings.getBoolean("first", true)) {
                ShortcutIcon();
                SharedPreferences.Editor pref = settings.edit();
                pref.putBoolean("first", false);
                pref.commit();
            }
        }
    }

    private void gif() {
        mp = new MediaPlayer();
        mSurfaceView = (SurfaceView) findViewById(R.id.surface);
        mSurfaceView.setVisibility(View.VISIBLE);
        mSurfaceView.getHolder().addCallback(this);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.v("this","animEndReach");
                final Handler handler = new Handler();
                final boolean[] mStopHandler = {false};
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {

                        if (loadCoompleted) {
                            Intent mInHome = new Intent(MainActivity.this, Home.class);
                            MainActivity.this.startActivity(mInHome);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            MainActivity.this.finish();
                            mStopHandler[0] = true;
                        }
                        if (!mStopHandler[0]) {
                            handler.postDelayed(this, 1000);
                        }
                    }
                };
                handler.post(runnable);
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            Uri video = null;
//            video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.loading);
            mp.setDataSource(this, video);
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Get the dimensions of the video
        int videoWidth = mp.getVideoWidth();
        int videoHeight = mp.getVideoHeight();

        //Get the width of the screen
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();

        //Get the SurfaceView layout parameters
        android.view.ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();

        //Set the width of the SurfaceView to the width of the screen
        lp.width = screenWidth;

        //Set the height of the SurfaceView to match the aspect ratio of the video
        //be sure to cast these as floats otherwise the calculation will likely be 0
        lp.height = (int) (((float) videoHeight / (float) videoWidth) * (float) screenWidth);

        //Commit the layout parameters
        mSurfaceView.setLayoutParams(lp);

        //Start video
        mp.setDisplay(holder);
        mp.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    private void ShortcutIcon() {

        try {
            Intent shortcutIntent = new Intent(getApplicationContext(), MainActivity.class);
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            shortcutIntent.setAction(Intent.ACTION_MAIN);

            Intent addIntent = new Intent();
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
            addIntent.putExtra("duplicate", false);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher));
            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            getApplicationContext().sendBroadcast(addIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public class getXml extends AsyncTask<Void, String, String> {
        Boolean goterror = false,ostanShahrestanSellers=false;

        @Override
        protected String doInBackground(Void... params) {
            try {
                Document doc = null;
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                if (Func.isInternet(MainActivity.this)) {
                    String urls= getString(R.string.url) + "/getHomePage2.php?uid="
                            + Func.getUid(MainActivity.this) + "&w=" + Func.getScreenWidth(MainActivity.this)
                            +"&firstCat="+Func.getCityId(MainActivity.this);;
                    URL url = new URL(urls);
                    Log.v("tihs",urls);
                    URLConnection conn = url.openConnection();
                    doc = builder.parse(conn.getInputStream());

                    StringWriter sw = new StringWriter();
                    TransformerFactory tf = TransformerFactory.newInstance();
                    Transformer transformer = tf.newTransformer();
                    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
                    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

                    transformer.transform(new DOMSource(doc), new StreamResult(sw));
                    Func.insertOffline(MainActivity.this, sw.toString(), "FistActivity", 0);

                    SharedPreferences settings =getSharedPreferences("settings", MODE_PRIVATE);
                    SharedPreferences.Editor pref = settings.edit();
                    pref.putString("homedata", sw.toString());
                    if(sw.toString().contains("choose_ostan_beginning=true")){
                        pref.putBoolean(getString(R.string.ostanShahrestanSellers),true);
                        ostanShahrestanSellers=true;
                    }else
                        pref.putBoolean(getString(R.string.ostanShahrestanSellers),false);
                    pref.commit();
                }
            } catch (Exception e) {
                e.printStackTrace();
                goterror = true;
                Log.v("this", e.getMessage() + "eror");
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            loadCoompleted=true;

            if(ostanShahrestanSellers && Func.getCityId(MainActivity.this).equals("0")){
                getOstanShahrestan();
            }else
                loadCoompleted=true;
        }
    }

    private void getOstanShahrestan() {
        long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
        new Html(new OnTaskFinished() {
            @Override
            public void onFeedRetrieved(String body) {
                Log.v("this", body);
                if (body.equals("errordade")) {
                    MyToast.makeText(getApplicationContext(), "اتصال اینترنت را بررسی کنید");
                }else{
                    String citieS[]= null;
                    String citieId[] = null;
                    try {
                        JSONObject json=new JSONObject(body);
                        JSONArray cities=json.optJSONArray("cities");
                        citieS=new String[cities.length()];
                        citieId=new String[cities.length()];
                        for (int i=0; i<cities.length();i++){
                            JSONObject row=cities.optJSONObject(i);
                            citieS[i]=row.optString("name");
                            citieId[i]=row.optString("id");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getCitiies(citieS,citieId);
                }
            }
        }, true, this, "").execute(getString(R.string.url) + "/getCitiesMain.php?n=" + number);
    }

    private void getCitiies(final String[] citieS, final String[] citieId) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.ostan_shahrestan_ln, null);
        TextView tv = (TextView) convertView.findViewById(R.id.ostan_shahrestan);
        tv.setTypeface(Func.getTypeface(this));
        tv.setText("انتخاب شهر");

        alertDialog.setView(convertView);
        ListView lv = (ListView) convertView.findViewById(R.id.lv);

        Lv_adapter adapter = new Lv_adapter(this, citieS);
        lv.setAdapter(adapter);

        final AlertDialog alert = alertDialog.create();
        alert.show();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Func.setCity(citieS[position],citieId[position], MainActivity.this);
                new getXml().execute();
                alert.dismiss();
            }
        });
    }

}
