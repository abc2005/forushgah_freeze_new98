package com.persiandesigners.freeze;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.persiandesigners.freeze.Util.CustomProgressDialog;
import com.persiandesigners.freeze.Util.Html;
import com.persiandesigners.freeze.Util.MyToast;
import com.persiandesigners.freeze.Util.OnTaskFinished;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Cats extends Fragment {
    Toolbar toolbar;
    Typeface typeface2;
    CustomProgressDialog mCustomProgressDialog;
    private List<CatItems> catItems;
    private CatsAdapter adapter;
    RecyclerView recycle;

    GridView grid_cat;
    ListAdapterCat ladap_cat;
    String title;
    LinearLayout hide;

    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.cats, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        declare();

        getCats("0");
        actionbar();
    }


    private void getCats(String subcat) {
        mCustomProgressDialog.show("");
        new Html(new OnTaskFinished() {
            @Override
            public void onFeedRetrieved(String body) {
                Log.v("this", body);
                if (body.equals("errordade")) {
                    MyToast.makeText(getActivity(), "اشکالی پیش آمده است");
                }else{
                    hide.setVisibility(View.GONE);
                    final ArrayList<HashMap<String, String>> dataC = new ArrayList<HashMap<String, String>>();
                    try {
                        JSONObject jsonObj = new JSONObject(body);
                        JSONArray contacts = jsonObj.getJSONArray("contacts");
                        for (int i = 0; i < contacts.length(); i++) {
                            JSONObject c = contacts.getJSONObject(i);
                            HashMap<String, String> contact = new HashMap<String, String>();
                            contact.put("id", new String(c.getString("id")));
                            contact.put("img", new String(c.getString("thumb")));
                            contact.put("name", new String(c.getString("name")));
                            contact.put("hasSubCats", new String(c.getString("hsc")));
                            dataC.add(contact);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (ladap_cat == null) {
                        ladap_cat = new ListAdapterCat(getActivity(), dataC, typeface2);
                        grid_cat.setAdapter(ladap_cat);

                        if(dataC.size()==0){
//                            Intent in = new Intent(getActivity(), Subcats.class);
//                            in.putExtra("catId",bl.getString("catId"));
//                            in.putExtra("onvan",bl.getString("onvan"));
//                            startActivity(in);
//
                        }else {
                            mCustomProgressDialog.dismiss("");
                            grid_cat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent in = new Intent(getActivity(), Subcats.class);
                                    in.putExtra("catId", dataC.get(position).get("id"));
                                    in.putExtra("onvan", dataC.get(position).get("name"));
                                    startActivity(in);
                                }
                            });
                            LinearLayout lnsabadbottom = (LinearLayout) v.findViewById(R.id.lnsabadbottom);
                            lnsabadbottom.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }, false, getActivity(), "").execute(getString(R.string.url) + "/getAllCats.php?subcat="+subcat);
    }

    public class getXml extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (Func.isInternet(getActivity())) {
                StringBuilder response = new StringBuilder();
                try {
                    String search = "0";

                    URL url = new URL(getString(R.string.url) + "getCats.php");
                    Log.v("this", getString(R.string.url) + "getCats.php");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    Uri.Builder builder = new Uri.Builder()
                            .appendQueryParameter("search", search);
                    ;
                    String query = builder.build().getEncodedQuery();

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(query);
                    writer.flush();
                    writer.close();
                    os.close();
                    conn.connect();

                    int statusCode = conn.getResponseCode();
                    if (statusCode == 200) {
                        BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                        String line;
                        while ((line = r.readLine()) != null) {
                            response.append(line);
                        }
                    }
                } catch (Exception e) {
                    Log.d("this", e.getLocalizedMessage());
                }
                 return response.toString();
            } else {
                return Func.getOffline(getActivity(), "Cats", 0);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            mCustomProgressDialog.dismiss("");

//            adapter = new CatsAdapter(getActivity(),
//                    Func.parseResultCats(result.replaceAll(getString(R.string.qoteJson), "]").replaceAll(",,", ","), ""));
            recycle.setAdapter(adapter);

            grid_cat.setVisibility(View.GONE);
        }
    }

    private void declare() {
        hide=(LinearLayout) v.findViewById(R.id.hideit);

        mCustomProgressDialog = new CustomProgressDialog(getActivity());
        typeface2 =Func.getTypeface(getActivity());
        recycle = (RecyclerView) v.findViewById(R.id.RecyclerView);
        LinearLayoutManager linearCats = new LinearLayoutManager(getActivity());
        recycle.setLayoutManager(linearCats);
        grid_cat = (GridView) v.findViewById(R.id.grid_cat);
    }

    private void actionbar() {

        Func action = new Func(getActivity());
        action.MakeActionBar("لیست خدمات شرکت بابل استور");

        Func.checkSabad(getActivity());
        ImageView img_sabad = (ImageView)v.findViewById(R.id.img_sabad);
        img_sabad.setVisibility(View.GONE);
        TextView tvnumsabad = (TextView) v.findViewById(R.id.text_numkharid);
        tvnumsabad.setVisibility(View.GONE);

        Func.checkSabad(getActivity());
        v.findViewById(R.id.back).setVisibility(View.INVISIBLE);
        v.findViewById(R.id.imgsearch).setVisibility(View.INVISIBLE);
    }

}
