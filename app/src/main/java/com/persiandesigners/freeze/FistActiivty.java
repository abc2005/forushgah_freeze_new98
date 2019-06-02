package com.persiandesigners.freeze;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessaging;
import com.persiandesigners.freeze.Util.Alert;
import com.persiandesigners.freeze.Util.CustomProgressDialog;
import com.persiandesigners.freeze.Util.DatabaseHandler;
import com.persiandesigners.freeze.Util.Html;
import com.persiandesigners.freeze.Util.MyAdapterCallBack;
import com.persiandesigners.freeze.Util.MyToast;
import com.persiandesigners.freeze.Util.OnTaskFinished;
import com.persiandesigners.freeze.Util.RtlGridLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import ir.viewpagerindicator.CirclePageIndicator;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class FistActiivty extends Fragment implements View.OnClickListener, MyAdapterCallBack {
    private Toolbar toolbar;
    CustomProgressDialog mCustomProgressDialog;
    Typeface IranSans;
    RecyclerView pishnahadrecycle, topsellrecycle, jadidtarinrecycle, rc_cats, pishanadVIjeRc;
    String urls;
    SharedPreferences settings;
    Boolean activeKifPul = false, inCat = false, firstRun = true;
    LinearLayout ln_pishnahad, ln_topsold, ln_jadidtarin, lnchat, banners;
    LinearLayout under_cat, under_special, under_porforush, under_jadidtarin;
    Button tv1;
    LinearLayout product_reycles;
    Drawer drawer;
    TextView loading;
    RecyclerView rc_subcat;

    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.firstactivity, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            declare();
            Recycles();

            long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
            urls = getString(R.string.url) + "/getHomePage2.php?n=" + number + "&uid="
                    + Func.getUid(getActivity()) + "&w=" + Func.getScreenWidth(getActivity())
                    + "&firstCat=" + Func.getCityId(getActivity());
            ;

            SharedPreferences settings = getActivity().getSharedPreferences("settings", getActivity().MODE_PRIVATE);
            if (settings.getString("homedata", "").length() > 5) {
                MakeHome(settings.getString("homedata", ""));

                SharedPreferences.Editor pref = settings.edit();
                pref.putString("homedata", "");
                pref.commit();
            } else {
                if (Func.isInternet(getActivity()) ||
                        Func.hasOffline(getActivity(), "FistActivity", 0)) {
                    new getXml().execute();
                    if (Func.isInternet(getActivity()) == false)
                        Func.NoNet(getActivity());
                } else {
                    Func.NoNet(getActivity());
                }
            }

            actionbar();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        v.findViewById(R.id.drawer).setVisibility(View.GONE);
    }

    private void MakeProdViews(String body) {
        try {
            JSONObject response = new JSONObject(body);
            JSONArray posts = response.optJSONArray("contacts");
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);

                JSONObject responseP = new JSONObject(body);
                JSONArray postsP = responseP.optJSONArray("c" + post.optString("id"));
                List<FeedItem> feedsList = new ArrayList<>();
                for (int p = 0; p < postsP.length(); p++) {
                    JSONObject postP = postsP.optJSONObject(p);
                    FeedItem item = new FeedItem();
                    item.setname(postP.optString("name"));
                    item.setid(postP.optString("id"));
                    item.setPrice(postP.optString("price"));
                    item.setimg(postP.optString("img"));
                    item.setprice2(postP.optString("price2"));
                    item.setnum(postP.optString("num"));
                    item.setMajazi(postP.optString("majazi"));
                    item.setTedad("1");
                    if (postP.optString("vije").equals("1"))
                        item.setForushVije(true);
                    else
                        item.setForushVije(false);
                    feedsList.add(item);
                }

                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = vi.inflate(R.layout.recycle_product_row, null);

                TextView tv_title = (TextView) v.findViewById(R.id.tv_title);
                tv_title.setTypeface(IranSans);
                tv_title.setText(post.optString("name"));

                LinearLayoutManager layoutManager
                        = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                layoutManager.setReverseLayout(true);
                RecyclerView product_recycle = (RecyclerView) v.findViewById(R.id.product_recycle);
                product_recycle.setLayoutManager(layoutManager);

                MyAdapter adapterRec = new MyAdapter(getActivity(), feedsList);
                ScaleInAnimationAdapter scaldeAdapter = new ScaleInAnimationAdapter(adapterRec);
                scaldeAdapter.setDuration(300);
                product_recycle.setAdapter(scaldeAdapter);

                product_reycles.addView(v);
                product_reycles.setVisibility(View.VISIBLE);
            }
            mCustomProgressDialog.dismiss("");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.v("this", e.getMessage());
        }
    }

    private void MakeHome(String data) {
        String bazaryab="",emtazDehi = "false", updateLink = "", pishnahad_vije = "", nemayeshgahi = "", name = "", msg = "", pishnahad = "", porforush = "", jadid = "", gallery = "", mainCats = "", version = "", kif = "", banner = "", version_link;
        try {
            Document doc = null;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(data));
            doc = builder.parse((is));

            NodeList nodes = doc.getElementsByTagName("data");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                NodeList title = element.getElementsByTagName("content");
                Element line = (Element) title.item(0);
                if (i == 0)
                    pishnahad = line.getTextContent();
                else if (i == 1)
                    porforush = line.getTextContent();
                else if (i == 2)
                    jadid = line.getTextContent();
                else if (i == 3)
                    gallery = line.getTextContent();
                else if (i == 4)
                    mainCats = line.getTextContent();
                else if ((i == 5))
                    version = line.getTextContent();
                else if ((i == 7))
                    banner = line.getTextContent();
                else if ((i == 6))
                    kif = line.getTextContent();
                else if ((i == 8))
                    msg = line.getTextContent();
                else if ((i == 9))
                    name = line.getTextContent();
                else if ((i == 10))
                    nemayeshgahi = line.getTextContent();
                else if (i == 11) {
                    pishnahad_vije = line.getTextContent();
                } else if (i == 12) {
                    emtazDehi = line.getTextContent();
                } else if (i == 13) {
                    updateLink = line.getTextContent();
                } else if (i == 15) {
                    bazaryab = line.getTextContent();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("this", e.getMessage());
        }

        LinearLayout lnsabadbottom = (LinearLayout) v.findViewById(R.id.lnsabadbottom);
        lnsabadbottom.setVisibility(View.GONE);

        JSONArray contacts = null;
        AutoScrollViewPager viewPager;// gallery
        PagerAdapter adapter;
        CirclePageIndicator mIndicator;

        makeNavar(msg);


        makeBanners(banner);

        if (kif.equals("removed")) {
            SharedPreferences settings = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
            settings.edit().clear().commit();
            startActivity(new Intent(getActivity(), Home.class));
        }
        if (!emtazDehi.equals("false")) {
            SharedPreferences settings = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
            SharedPreferences.Editor pref = settings.edit();
            pref.putBoolean("emtiazModule", true);
            pref.putString("emtiazUser", emtazDehi);
            pref.commit();
            if (drawer != null)
                drawer.update();
        }

        if (!Func.getUid(getActivity()).equals("0") && activeKifPul) {
            SharedPreferences settings = getActivity().getSharedPreferences("settings", getActivity().MODE_PRIVATE);
            SharedPreferences.Editor pref = settings.edit();
            pref.putString("kif", kif);
            pref.commit();
            if (drawer != null)
                drawer.mAdapter.notifyDataSetChanged();
        }

        if (!Func.getUid(getActivity()).equals("0") && Func.isDizbon(getActivity())) {
            SharedPreferences settings = getActivity().getSharedPreferences("settings", getActivity().MODE_PRIVATE);
            SharedPreferences.Editor pref = settings.edit();
            pref.putString("name", name);
            pref.commit();
        }

        MyAdapter adapterRec;

        PackageInfo pInfo;
        Integer versionC = 0;
        try {
            pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            versionC = pInfo.versionCode;
            Log.v("this", "version " + version + " " + versionC);
            if (Integer.parseInt(version) > versionC) {
                final Dialog dialog = new Dialog(getActivity(), R.style.DialogStylerTransparent);
                dialog.setContentView(R.layout.update_dialog);

                TextView tvtitle = (TextView) dialog.findViewById(R.id.tvtitle);
                tvtitle.setTypeface(IranSans);


                TextView updatekon = (TextView) dialog.findViewById(R.id.updatekon);
                updatekon.setTypeface(IranSans);
                final String finalUpdateLink = updateLink;
                updatekon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        if (finalUpdateLink != null && finalUpdateLink.contains("http")) {
                            Intent i = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(finalUpdateLink));
                            startActivity(i);
                        }
                    }
                });

                TextView badan = (TextView) dialog.findViewById(R.id.badan);
                badan.setTypeface(IranSans);
                badan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.show();
                dialog.getWindow().setAttributes(lp);
            }
        } catch (Exception e) {
        }

//                DefaultItemAnimator animator = new DefaultItemAnimator() ;
//                animator.setAddDuration(1000);
//                animator.setRemoveDuration(1000);

        if (pishnahad != null && pishnahad.length() > 0 && !pishnahad.contains("not##")) {
            adapterRec = new MyAdapter(getActivity(), Func.parseResult(pishnahad));
//                    pishnahadrecycle.setItemAnimator(animator);
            ScaleInAnimationAdapter scaldeAdapter = new ScaleInAnimationAdapter(adapterRec);
            scaldeAdapter.setDuration(300);
            pishnahadrecycle.setAdapter(scaldeAdapter);
        } else
            ln_pishnahad.setVisibility(View.GONE);


        if (porforush != null && porforush.length() > 0 && !porforush.contains("not##")) {
            adapterRec = new MyAdapter(getActivity(), Func.parseResult(porforush));
//                    topsellrecycle.setItemAnimator(animator);
            ScaleInAnimationAdapter scaldeAdapter = new ScaleInAnimationAdapter(adapterRec);
            scaldeAdapter.setDuration(300);
            topsellrecycle.setAdapter(scaldeAdapter);
        } else
            ln_topsold.setVisibility(View.GONE);

        if (jadid != null && jadid.length() > 0 && !jadid.contains("not##")) {
            adapterRec = new MyAdapter(getActivity(), Func.parseResult(jadid));
//                    jadidtarinrecycle.setItemAnimator(animator);
            ScaleInAnimationAdapter scaldeAdapter = new ScaleInAnimationAdapter(adapterRec);
            scaldeAdapter.setDuration(300);
            jadidtarinrecycle.setAdapter(scaldeAdapter);
        } else
            ln_jadidtarin.setVisibility(View.GONE);

        if (pishnahad_vije != null && pishnahad_vije.length() > 0 && !pishnahad_vije.contains("not##")) {
            FrameLayout fl_pishnahad_vije = (FrameLayout) v.findViewById(R.id.fl_pishnahad_vije);
            if (getResources().getBoolean(R.bool.show_pishnahadvije)) {
                TextView tvpishnahadvije = (TextView) v.findViewById(R.id.tvpishnahadvije);
                tvpishnahadvije.setTypeface(IranSans);

                List<PishnahadVijeItems> items = Func.parsePishnahadVije(pishnahad_vije);
                if (items != null && items.size() > 0) {
                    fl_pishnahad_vije.setVisibility(View.VISIBLE);

                    PishnahadVijeAdp pishnahadVijeAdp = new PishnahadVijeAdp(getActivity(), items);
                    pishanadVIjeRc.setAdapter(pishnahadVijeAdp);

                    pishanadVIjeRc.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            Integer.parseInt(items.get(0).getHeight())));
                }
            }
        }


        if (getResources().getBoolean(R.bool.homepage_cats_like_digikala)) {
            rc_cats.setVisibility(View.VISIBLE);
            tv1.setVisibility(View.GONE);

            List<CatItems> shopItems = null;
            try {
                JSONObject response = new JSONObject(mainCats);
                JSONArray posts = response.optJSONArray("contacts");
                shopItems = new ArrayList<>();

                for (int i = 0; i < posts.length(); i++) {
                    JSONObject post = posts.optJSONObject(i);
                    CatItems item = new CatItems();
                    item.setName(post.optString("name"));
                    item.setId(post.optString("id"));
                    shopItems.add(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.v("this", e.getMessage());
            }
            rc_cats.setAdapter(new CatsHomeAdapter(getActivity(), shopItems));
        }

        if (mainCats != null && mainCats.length() > 0) {
            if (getResources().getBoolean(R.bool.show_cats_on_homepage) == false) {
                rc_subcat.setVisibility(View.GONE);
            } else {
                List<CatsItems> items = Func.ParseCatsHome(mainCats);
                SubCatsAdapterPics subcatadapter = new SubCatsAdapterPics(getActivity(), items);
                rc_subcat.setAdapter(subcatadapter);
            }
        }

        if (gallery != null && gallery.length() > 0) {
            int count = 0;
            String img[], link[] = null;
            ArrayList<HashMap<String, String>> dataC = new ArrayList<HashMap<String, String>>();
            try {
                JSONObject jsonObj = new JSONObject(gallery);
                contacts = jsonObj.getJSONArray("contacts");

                if (contacts.length() > 0) {
                    RelativeLayout tv1 = (RelativeLayout) v.findViewById(R.id.slider_ln);
                    tv1.setVisibility(View.VISIBLE);

                    if (Func.isInternet(getActivity()) == false)
                        tv1.setVisibility(View.GONE);
                }


                link = new String[contacts.length()];
                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject c = contacts.getJSONObject(i);
                    HashMap<String, String> contact = new HashMap<String, String>();
                    contact.put("img", new String(c.getString("img").getBytes("ISO-8859-1"), "UTF-8"));
                    link[i] = c.getString("link");
                    contact.put("h", c.getString("h"));
                    contact.put("id", c.getString("id"));
                    contact.put("link_type", c.getString("link_type"));
                    contact.put("link", c.getString("link"));
                    dataC.add(contact);
                }
            } catch (JSONException e) {
                Log.v("this", e.getMessage());
            } catch (UnsupportedEncodingException e) {
                Log.v("this", e.getMessage());
            }
            if (dataC == null)
                count = 0;
            else
                count = dataC.size();
            img = new String[count];
            count--;
            while (count >= 0) {
                img[count] = dataC.get(count).get("img");
                count--;
            }
            viewPager = (AutoScrollViewPager) v.findViewById(R.id.pager);
            adapter = new ViewPagerAdaptergallery(getActivity(), img,
                    "pictures", link, dataC);
            try {
                int height = Integer.parseInt(dataC.get(0).get("h"));
                if (height > 0)
                    viewPager.getLayoutParams().height = height;
            } catch (Exception e) {
                viewPager.getLayoutParams().height = 300;
            }
            viewPager.setAdapter(adapter);
            viewPager.startAutoScroll(5000);
            viewPager.setInterval(5000);

            // ViewPager Indicator
            mIndicator = (CirclePageIndicator) v.findViewById(R.id.indicator);
            mIndicator.setViewPager(viewPager);
            mIndicator.setCurrentItem(adapter.getCount() - 1);
        }

        SharedPreferences settings = getActivity().getSharedPreferences("settings", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor pref = settings.edit();
        pref.putString("nemayeshgahi", nemayeshgahi);
        pref.putString("isBazaryab",bazaryab);
        pref.commit();
        loading.setVisibility(View.GONE);

    }

    public class getXml extends AsyncTask<Void, String, String> {
        Boolean goterror = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (Func.isInternet(getActivity())) {
                mCustomProgressDialog = new CustomProgressDialog(getActivity());
                mCustomProgressDialog.show("");
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Document doc = null;
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                if (Func.isInternet(getActivity())) {
                    URL url = new URL(urls);
                    Log.v("this", urls);
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
                    Func.insertOffline(getActivity(), sw.toString(), "FistActivity", 0);
                    Log.v("this", "va" + sw.toString());
                    return sw.toString();
                } else {
                    return Func.getOffline(getActivity(), "FistActivity", 0);
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
            if (mCustomProgressDialog != null)
                mCustomProgressDialog.dismiss("");

            if (goterror == false && !isCancelled()) {
                MakeHome(result);
            } else {
                loading.setText("اشکالی پیش آمده است . اتصال اینترنت را بررسی کنید" + "\n \n " + "تلاش مجدد..");
                loading.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new getXml().execute();
                    }
                });
            }
        }
    }

    private void makeBanners(String banner) {
        if (banner != null) {
            int width = getActivity().getWindowManager().getDefaultDisplay().getWidth() / 2;

            try {
                JSONObject response = new JSONObject(banner);
                JSONArray posts = response.optJSONArray("contacts");
                for (int i = 0; i < posts.length(); i++) {
                    JSONObject post = posts.optJSONObject(i);

                    String type = post.optString("type");
                    String img = post.optString("img");
                    String link_type = post.optString("link_type");
                    String link = post.optString("link");

                    if (type.equals("ver")) {
                        banners.setVisibility(View.VISIBLE);
                        ImageView imgNew = makeTheBanner(img, link_type, link);
                        Glide.with(getActivity()).load(getString(R.string.url) + "Opitures/" + img).into(imgNew);
                        banners.addView(imgNew);
                    } else if (type.equals("under_cat")) {
                        under_cat.setVisibility(View.VISIBLE);
                        ImageView imgNew = makeTheBanner(img, link_type, link);
                        Glide.with(getActivity()).load(getString(R.string.url) + "Opitures/" + img).into(imgNew);
                        under_cat.addView(imgNew);
                    } else if (type.equals("under_special")) {
                        under_special.setVisibility(View.VISIBLE);
                        ImageView imgNew = makeTheBanner(img, link_type, link);
                        Glide.with(getActivity()).load(getString(R.string.url) + "Opitures/" + img).into(imgNew);
                        under_special.addView(imgNew);
                    } else if (type.equals("under_porforush")) {
                        under_porforush.setVisibility(View.VISIBLE);
                        ImageView imgNew = makeTheBanner(img, link_type, link);
                        Glide.with(getActivity()).load(getString(R.string.url) + "Opitures/" + img).into(imgNew);
                        under_porforush.addView(imgNew);
                    } else if (type.equals("under_jadidtarin")) {
                        under_jadidtarin.setVisibility(View.VISIBLE);
                        ImageView imgNew = makeTheBanner(img, link_type, link);
                        Glide.with(getActivity()).load(getString(R.string.url) + "Opitures/" + img).into(imgNew);
                        under_jadidtarin.addView(imgNew);
                    } else if (type.equals("hor")) {//horzental
                        banners.setVisibility(View.VISIBLE);
                        //LinearLayout lnNew=new LinearLayout(new ContextThemeWrapper(this, R.style.horzentalLayout), null, 0);
                        LinearLayout lnNew = new LinearLayout(getActivity());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(10, 5, 10, 5);
                        lnNew.setLayoutParams(params);

                        LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View v = vi.inflate(R.layout.hor_layout, null);

                        ImageView imgNew = (ImageView) v.findViewById(R.id.imgnew1);
                        CardView.LayoutParams params2 = new CardView.LayoutParams(width - 10, ViewGroup.LayoutParams.WRAP_CONTENT);
                        imgNew.setLayoutParams(params2);
                        final String finalLink1 = link;
                        final String finalLink_type1 = link_type;
                        imgNew.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                doCLickListener(v, finalLink_type1, finalLink1);
                            }
                        });
                        Glide.with(getActivity()).load(getString(R.string.url) + "Opitures/" + img).into(imgNew);

                        i++;
                        if (posts.optJSONObject(i) != null && post.optString("type").equals("hor")) {
                            post = posts.optJSONObject(i);
                            type = post.optString("type");
                            img = post.optString("img");
                            link_type = post.optString("link_type");
                            link = post.optString("link");

                            ImageView imgNew2 = (ImageView) v.findViewById(R.id.imgnew2);
                            imgNew2.setLayoutParams(params2);
                            final String finalLink_type = link_type;
                            final String finalLink = link;
                            imgNew2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    doCLickListener(v, finalLink_type, finalLink);
                                }
                            });
                            Glide.with(getActivity()).load(getString(R.string.url) + "Opitures/" + img).into(imgNew2);
                        }
                        lnNew.addView(v);
                        banners.addView(lnNew);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            firstRun = false;
        }
    }

    private ImageView makeTheBanner(String img, String link_type, String link) {
        ImageView imgNew = new ImageView(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 5, 10, 5);
        imgNew.setLayoutParams(params);
        final String finalLink2 = link;
        final String finalLink_type2 = link_type;
        imgNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCLickListener(v, finalLink_type2, finalLink2);
            }
        });
        return imgNew;
    }

    private void doCLickListener(View v, String link_type, String link) {
        if (link_type.equals("web")) {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(browserIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (link_type.equals("cat")) {
            Intent in = new Intent(getActivity(), Products.class);
            in.putExtra("catId", link);
            in.putExtra("onvan", "");
            startActivity(in);
        } else if (link_type.equals("prod") || link_type.equals("pro")) {
            Intent in = new Intent(getActivity(), Detailss.class);
            in.putExtra("productid", link);
            in.putExtra("name", "");
            startActivity(in);
        }
    }


    private void makeNavar(String msg) {
        Log.v("this", "msg: " + msg);
        try {
            JSONObject j = new JSONObject(msg);
            if (j.getInt("msg_active") == 1) {
                final FrameLayout pad = (FrameLayout) v.findViewById(R.id.pad);

                final CardView marque = (CardView) v.findViewById(R.id.marque);
                marque.setVisibility(View.VISIBLE);
                try {
                    marque.setCardBackgroundColor(Color.parseColor(j.getString("msg_color")));
                } catch (Exception e) {
                    marque.setCardBackgroundColor(Color.parseColor("#395471"));
                }

                TextView tv = null;
                if (j.getInt("msg_marque") == 1) {
                    FrameLayout lnmarq = (FrameLayout) v.findViewById(R.id.lnmarq);
                    lnmarq.setVisibility(View.VISIBLE);

                    tv = (TextView) v.findViewById(R.id.tv_msg_mrq);
                    tv.setMarqueeRepeatLimit(-1);
                } else {
                    tv = (TextView) v.findViewById(R.id.tv_msg);
                }
                tv.setText(j.getString("msg"));
                tv.setTypeface(IranSans);
                tv.setVisibility(View.VISIBLE);
                tv.setSelected(true);

                ImageView closeit = (ImageView) v.findViewById(R.id.closeit);
                if (j.getInt("msg_allow_close") == 0) {
                    closeit.setVisibility(View.GONE);
                } else {
                    closeit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            marque.setVisibility(View.GONE);
                            pad.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Recycles() {
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        jadidtarinrecycle = (RecyclerView) v.findViewById(R.id.jadidtarinrecycle);
        layoutManager.setReverseLayout(true);
        jadidtarinrecycle.setLayoutManager(layoutManager);

        rc_cats = (RecyclerView) v.findViewById(R.id.rc_cats);
        LinearLayoutManager lnrtl = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        lnrtl.setReverseLayout(true);
        rc_cats.setLayoutManager(lnrtl);

        pishanadVIjeRc = (RecyclerView) v.findViewById(R.id.pishnahad_vije_recycle);
        LinearLayoutManager lnpishanadVIjeRc = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        lnpishanadVIjeRc.setReverseLayout(true);
        pishanadVIjeRc.setLayoutManager(lnpishanadVIjeRc);

        LinearLayoutManager layoutManager2
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManager2.setReverseLayout(true);
        topsellrecycle = (RecyclerView) v.findViewById(R.id.topsellrecycle);
        topsellrecycle.setLayoutManager(layoutManager2);

        LinearLayoutManager layoutManager3
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManager3.setReverseLayout(true);
        pishnahadrecycle = (RecyclerView) v.findViewById(R.id.pishnahadrecycle);
        pishnahadrecycle.setLayoutManager(layoutManager3);
    }

    private void declare() {
        if (getResources().getBoolean(R.bool.isActiveKif)) {
            activeKifPul = true;
        }
        if (getResources().getBoolean(R.bool.show_category_digibar) == false) {
            FrameLayout fm = (FrameLayout) v.findViewById(R.id.category_digibar);
            fm.setVisibility(View.GONE);
        }
        if (getResources().getBoolean(R.bool.show_forush_vije) == false) {
            FrameLayout fm = (FrameLayout) v.findViewById(R.id.forush_vije);
            fm.setVisibility(View.GONE);
        }
        if (getResources().getBoolean(R.bool.show_porforush) == false) {
            FrameLayout fm = (FrameLayout) v.findViewById(R.id.porforush);
            fm.setVisibility(View.GONE);
        }
        if (getResources().getBoolean(R.bool.show_jadidtarin) == false) {
            FrameLayout fm = (FrameLayout) v.findViewById(R.id.jadidtarin);
            fm.setVisibility(View.GONE);
        }
        if (getResources().getBoolean(R.bool.show_cat_button) == false) {
            Button tv1 = (Button) v.findViewById(R.id.tv1);
            tv1.setVisibility(View.GONE);
        }
        if (getResources().getBoolean(R.bool.show_slideshow) == false) {
            RelativeLayout tv1 = (RelativeLayout) v.findViewById(R.id.slider_ln);
            tv1.setVisibility(View.GONE);
        }
        drawer = new Drawer(getActivity());
        product_reycles = (LinearLayout) v.findViewById(R.id.product_reycles);

        settings = getActivity().getSharedPreferences("settings", getActivity().MODE_PRIVATE);
        IranSans = Func.getTypeface(getActivity());

        rc_subcat = (RecyclerView) v.findViewById(R.id.subcats);
        RtlGridLayoutManager layoutManager2 = new RtlGridLayoutManager(getActivity(), 3);
        rc_subcat.setLayoutManager(layoutManager2);
        rc_subcat.setNestedScrollingEnabled(false);

        loading = (TextView) v.findViewById(R.id.loading);
        loading.setTypeface(IranSans);

        ln_pishnahad = (LinearLayout) v.findViewById(R.id.ln_pishnahad);
        ln_topsold = (LinearLayout) v.findViewById(R.id.ln_topsold);
        ln_jadidtarin = (LinearLayout) v.findViewById(R.id.ln_jadidtain);
        banners = (LinearLayout) v.findViewById(R.id.banners);

        under_cat = (LinearLayout) v.findViewById(R.id.under_cat);
        under_special = (LinearLayout) v.findViewById(R.id.under_special);
        under_porforush = (LinearLayout) v.findViewById(R.id.under_porforush);
        under_jadidtarin = (LinearLayout) v.findViewById(R.id.under_jadidtarin);

        tv1 = (Button) v.findViewById(R.id.tv1);
        tv1.setTypeface(IranSans);
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getResources().getBoolean(R.bool.category_like_digikala))
                    startActivity(new Intent(getActivity(), Cats_digi.class));
                else
                    startActivity(new Intent(getActivity(), Cats.class));
            }
        });

        TextView tvpishnahad = (TextView) v.findViewById(R.id.tvpishnahad);
        tvpishnahad.setTypeface(IranSans);
        tvpishnahad.setText(android.text.Html.fromHtml("<font color=red>فروش ویژه</font> " + getString(R.string.app_name)));

        TextView tvtopsell = (TextView) v.findViewById(R.id.tvtopsell);
        tvtopsell.setTypeface(IranSans);
        tvtopsell.setText("پرفروش ترین ها");

        TextView tvharaj = (TextView) v.findViewById(R.id.tvjadidtarin);
        tvharaj.setTypeface(IranSans);
        tvharaj.setText("جدیدترین ها");

        TextView tvalltvtopsell = (TextView) v.findViewById(R.id.tvalltvtopsell);
        tvalltvtopsell.setTypeface(IranSans);
        tvalltvtopsell.setOnClickListener(this);

        TextView tvalljadidtarin = (TextView) v.findViewById(R.id.tvalljadidtarin);
        tvalljadidtarin.setTypeface(IranSans);
        tvalljadidtarin.setOnClickListener(this);


        TextView tvalltvjive = (TextView) v.findViewById(R.id.tvalltvjive);
        tvalltvjive.setTypeface(IranSans);
        tvalltvjive.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent in = null;
        switch (v.getId()) {
            case R.id.tvalltvtopsell:
                in = new Intent(getActivity(), Products.class);
                in.putExtra("for", "porForush");
                in.putExtra("title", "پرفروش ترین ها");
                startActivity(in);
                break;
            case R.id.tvalljadidtarin:
                in = new Intent(getActivity(), Products.class);
                in.putExtra("for", "jadidtarinha");
                in.putExtra("title", "جدیدترین ها");
                startActivity(in);
                break;
            case R.id.tvalltvjive:
                in = new Intent(getActivity(), Products.class);
                in.putExtra("for", "haraj");
                in.putExtra("title", "فروش ویژه");
                startActivity(in);
                break;
        }
    }


    private void actionbar() {
        toolbar = (Toolbar) v.findViewById(R.id.appbar);

        Func action = new Func(getActivity());
        action.MakeActionBar("");
        Func.checkSabad(getActivity());
        action.HideSearch();

        ImageView img_sabad = (ImageView) v.findViewById(R.id.img_sabad);
        img_sabad.setVisibility(View.GONE);
        TextView tvnumsabad = (TextView) v.findViewById(R.id.text_numkharid);
        tvnumsabad.setVisibility(View.GONE);


        ImageView profile = (ImageView) v.findViewById(R.id.profile);
        profile.setVisibility(View.VISIBLE);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Func.getUid(getActivity()).equals("0"))
                    startActivity(new Intent(getActivity(), Login.class));
                else
                    startActivity(new Intent(getActivity(), UserProfile.class));
            }
        });
    }


    @Override
    public void checkSabad() {
        actionbar();
    }


//    @Override
//    protected void onStart() {
//        super.onStart();
//        Func.checkSabad(getActivity());
//
//        LeftSabad lsabad = new LeftSabad(getActivity());
//        if (drawer != null)
//            drawer.update();
//
//        if (getResources().getBoolean(R.bool.isActiveKif) && !Func.getUid(getActivity()).equals("0") && firstRun == false)
//            getUserData();
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (pishnahadrecycle != null && pishnahadrecycle.getAdapter() != null)
//            pishnahadrecycle.getAdapter().notifyDataSetChanged();
//        if (topsellrecycle != null && topsellrecycle.getAdapter() != null)
//            topsellrecycle.getAdapter().notifyDataSetChanged();
//        if (jadidtarinrecycle != null && jadidtarinrecycle.getAdapter() != null)
//            jadidtarinrecycle.getAdapter().notifyDataSetChanged();
//    }


    private void getUserData() {
        long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
        new Html(new OnTaskFinished() {
            @Override
            public void onFeedRetrieved(String body) {
                Log.v("this", body);
                SharedPreferences settings = getActivity().getSharedPreferences("settings", getActivity().MODE_PRIVATE);
                SharedPreferences.Editor pref = settings.edit();
                pref.putString("kif", body);
                pref.commit();
            }
        }, false, getActivity(), "").execute(getString(R.string.url)
                + "getMojudiKifPul.php?n=" + number + "&uid=" + Func.getUid(getActivity()));
    }
}
