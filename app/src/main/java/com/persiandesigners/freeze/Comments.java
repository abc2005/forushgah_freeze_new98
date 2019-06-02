package com.persiandesigners.freeze;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.persiandesigners.freeze.Util.HtmlPost;
import com.persiandesigners.freeze.Util.MyToast;
import com.persiandesigners.freeze.Util.OnTaskFinished;
import com.software.shell.fab.ActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by navid on 9/28/2015.
 */
public class Comments extends AppCompatActivity {
    ProgressDialog pDialog;
    private static String url;
    RecyclerView recycle;
    Boolean loadmore = true, taskrunning;
    int page = 0, in, to;
    Typeface typeface;
    private List<CatItems> catItems;
    private CommentsAdapter adapter;

    private int previousTotal = 0;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    LinearLayoutManager mLayoutManager;
    int pid;
    Toolbar toolbar;

    TextView onvan, emtiaz, body;
    RatingBar rate;
    String onvanS, bodyS, rateS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment);
        declare();
        actionbar();

        Bundle bl = getIntent().getExtras();
        url = getString(R.string.url) + "/getComments.php?id=" + bl.getInt("id") + "&page=0";
        pid = bl.getInt("id");
        new AsyncHttpTask().execute(url);

        recycle.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = recycle.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                if (loadmore) {
                    if (totalItemCount > previousTotal) {
                        previousTotal = totalItemCount;
                    }
                }
                if ((totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    if (taskrunning != null && taskrunning == false && loadmore) {
                        loadmore = false;
                        page = page + 1;
                        new AsyncHttpTask().execute(url);
                    }

                }

            }
        });

        ActionButton actionButton = (ActionButton) findViewById(R.id.action_button);
        actionButton.setButtonColor(Color.parseColor("#DA4336"));
        actionButton.setButtonColorPressed(Color.parseColor("#C34B40"));
        actionButton.playShowAnimation();
        actionButton.setShadowRadius(8);
        actionButton.setShadowXOffset(0);
        actionButton.setShadowYOffset(0);
        actionButton.setImageDrawable(ContextCompat.getDrawable(Comments.this, R.drawable.ic_write));

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
                if (settings.getString("uid", "0").equals("0")) {
                    MyToast.makeText(Comments.this, "جهت ارسال نظر ابتدا وارد شوید");
                } else {
                    LayoutInflater factory = LayoutInflater.from(Comments.this);
                    final View textEntryView = factory.inflate(R.layout.text_entry, null);

                    final EditText name = (EditText) textEntryView.findViewById(R.id.name);
                    name.setTypeface(typeface);
                    name.setVisibility(View.GONE);

                    final EditText body = (EditText) textEntryView.findViewById(R.id.body);
                    body.setTypeface(typeface);

                    final TextView tvemtiaz = (TextView) textEntryView.findViewById(R.id.tvemtiaz);
                    tvemtiaz.setTypeface(typeface);

                    final RatingBar rate = (RatingBar) textEntryView.findViewById(R.id.rtbProductRating);

                    final EditText txtUrl2 = new EditText(Comments.this);
                    txtUrl2.setGravity(Gravity.RIGHT);

                    TextView tv2 = new TextView(Comments.this);
                    tv2.setGravity(Gravity.RIGHT);
                    tv2.setTypeface(typeface);
                    tv2.setTextSize(18);
                    tv2.setTextColor(Color.BLACK);
                    tv2.setText("نظر شما درباره این مطلب");
                    tv2.setPadding(5, 5, 5, 5);

                    AlertDialog dialog = new AlertDialog.Builder(Comments.this)
                            .setCustomTitle(tv2)
                            .setMessage(("لطفا نظر خود را بنویسید"))
                            .setView(textEntryView)
                            .setPositiveButton(("ارسال"), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String cmt = body.getText().toString();
                                    String nm = name.getText().toString();
//                                if (nm.length() <= 2)
//                                    MyToast.makeText(Comments.this, "نام را وارد کنید");
//                                else
                                    if (cmt.length() < 5) {
                                        MyToast.makeText(Comments.this, "طول نظر کوتاه است");
                                    } else {
                                        Log.v("this", "Are " + rate.getNumStars());
                                        Uri.Builder builder = new Uri.Builder()
                                                .appendQueryParameter("id", pid + "")
                                                .appendQueryParameter("rate", rate.getNumStars() + "")
                                                .appendQueryParameter("uid", settings.getString("uid", "0"))
                                                .appendQueryParameter("msg", cmt)
                                                .appendQueryParameter("name", settings.getString("name", "0"));
                                        String query = builder.build().getEncodedQuery();

                                        new HtmlPost(new OnTaskFinished() {
                                            @Override
                                            public void onFeedRetrieved(String body) {
                                                Log.v("this", body);
                                                if (body.equals("errordade")) {
                                                    MyToast.makeText(getApplicationContext(), "اشکالی پیش آمده است");
                                                } else if (body.equals("ok")) {
                                                    MyToast.makeText(getApplicationContext(), "نظر شما با موفقیت ثبت شد و بعد از تایید نمایش داده میشود");
                                                } else if (body.equals("err"))
                                                    MyToast.makeText(Comments.this, getString(R.string.problem));
                                            }
                                        }, true, Comments.this, "", query).execute(getString(R.string.url) + "/getComment.php");
                                    }
                                }
                            })
                            .setNegativeButton(("لغو"), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            })
                            .show();

                    TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                    textView.setTypeface(typeface);

                    Button btn1 = (Button) dialog.findViewById(android.R.id.button1);
                    btn1.setTypeface(typeface);

                    Button btn2 = (Button) dialog.findViewById(android.R.id.button2);
                    btn2.setTypeface(typeface);

                }
            }
        });

    }


    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            taskrunning = true;
            pDialog = new ProgressDialog(Comments.this);
            pDialog.setMessage("در حال دریافت اطلاعات");
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected Integer doInBackground(String... params) {
            loadmore = false;
            Integer result = 0;
            HttpURLConnection urlConnection;
            try {
                URL url = new URL(params[0] + page);
                Log.v("this", params[0] + page);
                urlConnection = (HttpURLConnection) url.openConnection();
                int statusCode = urlConnection.getResponseCode();

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }
                    parseResult(response.toString());
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed to fetch data!";
                }
            } catch (Exception e) {
                Log.d("this", e.getLocalizedMessage());
            }
            return result; //"Failed to fetch data!";
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (pDialog.isShowing() && pDialog != null)
                pDialog.dismiss();
            taskrunning = false;
            if (result == 1) {
                if (adapter == null) {
                    adapter = new CommentsAdapter(Comments.this, catItems);
                    recycle.setAdapter(adapter);

                    onvan.setText(onvanS);
                    if (rateS != null && rateS.length() > 0 && !rateS.contains("NAN"))
                        rate.setRating(Float.parseFloat(rateS));

                    if (rateS != null && rateS.equals("NAN")) {
                        rate.setRating(0);
                        emtiaz.setText("0");
                    } else {
                        if (rateS != null && !rateS.equals("null"))
                            emtiaz.setText(rateS + "");
                    }

                    body.setText(bodyS);
                } else {
                    adapter.addAll(catItems);
                }

            } else {
                MyToast.makeText(Comments.this, (getResources().getString(R.string.problemload)));
            }
        }
    }

    public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> {
        private LayoutInflater inflater;
        private List<CatItems> list;

        public CommentsAdapter(Context context, List<CatItems> feedItemList) {
            inflater = LayoutInflater.from(context);
            this.list = feedItemList;
        }


        public void addAll(List<CatItems> catItems) {
            if (this.list == null) {
                this.list = catItems;
            } else {
                this.list.addAll(catItems);
            }
            notifyDataSetChanged();
            notifyItemInserted(list.size());
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parrent, int i) {
            View view = inflater.inflate(R.layout.comment_row, parrent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder viewHolder, int position) {
            CatItems item = list.get(position);
            viewHolder.text.setText(item.getText());
            viewHolder.name.setText(item.getName());
            viewHolder.dates.setText(item.getDates());

        }

        @Override
        public int getItemCount() {
            if (list == null)
                return 0;
            else
                return list.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView text, name, dates;

            public MyViewHolder(View itemView) {
                super(itemView);
                text = (TextView) itemView.findViewById(R.id.text);
                text.setTypeface(typeface);

                name = (TextView) itemView.findViewById(R.id.name);
                name.setTypeface(typeface);

                dates = (TextView) itemView.findViewById(R.id.dates);
                dates.setTypeface(typeface);
            }

        }
    }


    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("contacts");
            catItems = new ArrayList<>();
            if (posts.length() < 20)
                loadmore = false;

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                if (page == 0 && onvanS == null) {
                    onvanS = post.optString("name");
                    bodyS = "امتیاز محصول : \n\n" + " از مجموع " +
                            post.optString("count") + " امتیاز";
                    rateS = (post.optString("emtiaz"));
                } else {
                    CatItems item = new CatItems();
                    item.setName(post.optString("name"));
                    item.setText(post.optString("text"));
                    item.setDate(post.optString("dates"));
                    catItems.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.v("this", e.getMessage());
        }
    }


    public class CatItems {
        private String name, text, dates;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setDate(String dates) {
            this.dates = dates;
        }

        public String getDates() {
            return dates;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

    }

    private void actionbar() {
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        Func action = new Func(this);
        action.MakeActionBar(getString(R.string.Comments));
    }

    private void declare() {
        typeface = Typeface.createFromAsset(this.getAssets(), "yekan.ttf");
        recycle = (RecyclerView) findViewById(R.id.recycle);
        mLayoutManager = new LinearLayoutManager(this);
        // (Context context, int spanCount)
        recycle.setLayoutManager(mLayoutManager);

        onvan = (TextView) findViewById(R.id.onvan);
        onvan.setTypeface(typeface);
        emtiaz = (TextView) findViewById(R.id.emtiaz);
        emtiaz.setTypeface(typeface);
        body = (TextView) findViewById(R.id.body);
        body.setTypeface(typeface);
        rate = (RatingBar) findViewById(R.id.rate);
    }


}
