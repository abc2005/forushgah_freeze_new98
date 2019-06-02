package com.persiandesigners.freeze;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.persiandesigners.freeze.Util.Alert;
import com.persiandesigners.freeze.Util.DatabaseHandler;
import com.persiandesigners.freeze.Util.MyToast;

public class Home extends AppCompatActivity {
    Boolean exit = false;
    Typeface IranSans;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        declare();
        BottomMenu();
        pushNotification();

        if (getResources().getBoolean(R.bool.has_notification))
            FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.notificationId));
    }

    @Override
    protected void onStart() {
        super.onStart();
        LeftSabad lsabad=new LeftSabad(this);
    }

    private void declare() {
        IranSans = Func.getTypeface(this);
    }

    private void pushNotification() {
        final Intent intent = getIntent();
        if (intent != null) {
            if (intent.getExtras() != null && intent.getExtras().getString("message") != null
                    ) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(intent.getExtras().getString("message"))
                        .setCancelable(false)
                        .setPositiveButton(("بستن"), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                TextView messageView = (TextView) alert.findViewById(android.R.id.message);
                messageView.setGravity(Gravity.RIGHT);
                messageView.setTypeface(IranSans);

            }
        }

        String scheme = intent.getDataString();
        if (scheme != null) {
            try {
                scheme = scheme.substring(scheme.lastIndexOf("=") + 1);
                if (scheme.equals("false")) {
                    MyToast.makeText(this, "پرداخت ناموفق");
                } else if (scheme.length() > 0) {
                    DatabaseHandler db = new DatabaseHandler(this);
                    db.open();
                    db.clearSabadKharid();

                    final Alert mAlert = new Alert(this, R.style.mydialog);
                    mAlert.setIcon(android.R.drawable.ic_dialog_alert);
                    mAlert.setMessage((" خرید شما با موفقیت انجام شد . کد رهگیری سفارش شما "
                            + scheme + " میباشد " +
                            "\n" +
                            "با تشکر از خرید شما "));
                    mAlert.setPositveButton("بستن", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mAlert.dismiss();
                        }
                    });

                    final String finalScheme = scheme;
                    mAlert.setNegativeButton("نمایش فاکتور", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mAlert.dismiss();
                            final Dialog dialog_fac = new Dialog(Home.this, R.style.DialogStyler);
                            dialog_fac.setContentView(R.layout.page2);
                            dialog_fac.setTitle("وضعیت سفارش شما");
                            dialog_fac.setCancelable(false);

                            final ProgressBar progress = (ProgressBar) dialog_fac.findViewById(R.id.progress);

                            Button text = (Button) dialog_fac.findViewById(R.id.ok);
                            text.setVisibility(View.VISIBLE);
                            text.setText("بستن");
                            text.setTypeface(IranSans);
                            text.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog_fac.dismiss();
                                }
                            });
                            WebView wb = (WebView) dialog_fac.findViewById(R.id.webView1);
                            WebSettings settingswb = wb.getSettings();
                            text.bringToFront();
                            settingswb.setDefaultTextEncodingName("utf-8");
                            settingswb.setCacheMode(WebSettings.LOAD_NO_CACHE);
                            settingswb.setSaveFormData(false);
                            settingswb.setSupportZoom(true);
                            settingswb.setDisplayZoomControls(false);
                            settingswb.setBuiltInZoomControls(true);

                            String code = finalScheme;
                            wb.loadUrl(getString(R.string.url) + "admin/printData.php?id=" + code + "&codRah=" + code + "&fromApp=true");
                            Log.v("this", getString(R.string.url) + "admin/printData.php?id=" + code + "&codRah=" + code + "&fromApp=true");
                            wb.setWebViewClient(new WebViewClient() {
                                @Override
                                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                                    handler.proceed();
                                }

                                @Override
                                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                    view.loadUrl(url);
                                    return true;
                                }

                                public void onPageFinished(WebView view, String url) {
                                    try {
                                        progress.setVisibility(View.GONE);
                                    } catch (final IllegalArgumentException e) {

                                    } catch (final Exception e) {
                                    } finally {

                                    }
                                }
                            });

                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            lp.copyFrom(dialog_fac.getWindow().getAttributes());
                            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                            dialog_fac.show();
                            dialog_fac.getWindow().setAttributes(lp);
                        }
                    });
                    mAlert.show();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void BottomMenu() {
        final TextView tv_shop = (TextView) findViewById(R.id.tv_shop);
        tv_shop.setTypeface(IranSans, Typeface.BOLD);
        final ImageView img_shop = (ImageView) findViewById(R.id.img_shop);

        final TextView tv_basket = (TextView) findViewById(R.id.tv_basket);
        tv_shop.setTypeface(IranSans);
        final ImageView img_sabads = (ImageView) findViewById(R.id.img_sabads);

        final TextView tv_jari = (TextView) findViewById(R.id.tv_menu);
        tv_jari.setTypeface(IranSans);
        final ImageView img_jari = (ImageView) findViewById(R.id.img_jari);


        final TextView tv_profile = (TextView) findViewById(R.id.tv_profile);
        tv_profile.setTypeface(IranSans);
        final ImageView img_profile = (ImageView) findViewById(R.id.img_profile);

        final TextView tv_search = (TextView) findViewById(R.id.tv_search);
        tv_search.setTypeface(IranSans);
        final ImageView img_search = (ImageView) findViewById(R.id.img_search);

        LinearLayout shop = (LinearLayout) findViewById(R.id.shop);
        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeDefault(1, tv_search, tv_shop, img_shop, tv_basket, img_sabads, tv_jari, img_jari, tv_profile, img_profile, img_search);
                tv_shop.setTypeface(IranSans, Typeface.BOLD);
                tv_shop.setTextColor(getResources().getColor(R.color.colorPrimary));
//                img_shop.setImageDrawable(ContextCompat.getDrawable(Home.this, R.drawable.not_selected));
                img_shop.setColorFilter(ContextCompat.getColor(Home.this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        });

        LinearLayout sabadkharid = (LinearLayout) findViewById(R.id.sabadkharid);
        sabadkharid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeDefault(2, tv_search, tv_shop, img_shop, tv_basket, img_sabads, tv_jari, img_jari, tv_profile, img_profile, img_search);
                tv_basket.setTypeface(IranSans, Typeface.BOLD);
                tv_basket.setTextColor(getResources().getColor(R.color.colorPrimary));
                img_sabads.setColorFilter(ContextCompat.getColor(Home.this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        });

        LinearLayout jari = (LinearLayout) findViewById(R.id.jari);
        jari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeDefault(4, tv_search, tv_shop, img_shop, tv_basket, img_sabads, tv_jari, img_jari, tv_profile, img_profile, img_search);
                tv_jari.setTypeface(IranSans, Typeface.BOLD);
                tv_jari.setTextColor(getResources().getColor(R.color.colorPrimary));
                img_jari.setColorFilter(ContextCompat.getColor(Home.this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        });

        LinearLayout profile = (LinearLayout) findViewById(R.id.profileln);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeDefault(5, tv_search, tv_shop, img_shop, tv_basket, img_sabads, tv_jari, img_jari, tv_profile, img_profile, img_search);
                tv_profile.setTypeface(IranSans, Typeface.BOLD);
                tv_profile.setTextColor(getResources().getColor(R.color.colorPrimary));
//                img_profile.setImageDrawable(ContextCompat.getDrawable(Home.this, R.drawable.account_s_sel));
                img_profile.setColorFilter(ContextCompat.getColor(Home.this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        });
        LinearLayout support = (LinearLayout) findViewById(R.id.lnsupport);
        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeDefault(3, tv_search, tv_shop, img_shop, tv_basket, img_sabads, tv_jari, img_jari, tv_profile, img_profile, img_search);
                tv_search.setTypeface(IranSans, Typeface.BOLD);
                tv_search.setTextColor(getResources().getColor(R.color.colorPrimary));
                img_search.setColorFilter(ContextCompat.getColor(Home.this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        });

        Bundle bl = getIntent().getExtras();
        if (bl != null && bl.getString("sabad") != null) {
            makeDefault(2, tv_search, tv_shop, img_shop, tv_basket, img_sabads, tv_jari, img_jari, tv_profile, img_profile, img_search);
            tv_basket.setTypeface(IranSans, Typeface.BOLD);
            tv_basket.setTextColor(getResources().getColor(R.color.colorPrimary));
            img_sabads.setColorFilter(ContextCompat.getColor(Home.this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
        }else if (bl != null && bl.getString("search") != null) {
            makeDefault(3, tv_search, tv_shop, img_shop, tv_basket, img_sabads, tv_jari, img_jari, tv_profile, img_profile, img_search);
            tv_basket.setTypeface(IranSans, Typeface.BOLD);
            tv_basket.setTextColor(getResources().getColor(R.color.colorPrimary));
            img_sabads.setColorFilter(ContextCompat.getColor(Home.this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
        }else if (bl != null && bl.getString("cat") != null) {
            makeDefault(5, tv_search, tv_shop, img_shop, tv_basket, img_sabads, tv_jari, img_jari, tv_profile, img_profile, img_search);
            tv_profile.setTypeface(IranSans, Typeface.BOLD);
            tv_profile.setTextColor(getResources().getColor(R.color.colorPrimary));
            img_profile.setColorFilter(ContextCompat.getColor(Home.this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
        }else if (bl != null && bl.getString("listkharid") != null) {
            makeDefault(4, tv_search, tv_shop, img_shop, tv_basket, img_sabads, tv_jari, img_jari, tv_profile, img_profile, img_search);
            tv_jari.setTypeface(IranSans, Typeface.BOLD);
            tv_jari.setTextColor(getResources().getColor(R.color.colorPrimary));
            img_jari.setColorFilter(ContextCompat.getColor(Home.this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            makeDefault(1, tv_search, tv_shop, img_shop, tv_basket, img_sabads, tv_jari, img_jari, tv_profile, img_profile, img_search);
            tv_shop.setTypeface(IranSans, Typeface.BOLD);
            tv_shop.setTextColor(getResources().getColor(R.color.colorPrimary));
            img_shop.setColorFilter(ContextCompat.getColor(Home.this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    private void makeDefault(int SelectedFrg, TextView tv_search, TextView tv_shop, ImageView
            img_shop, TextView tv_basket, ImageView img_sabads,
                             TextView tv_jari, ImageView img_jari, TextView tv_profile, ImageView
                                     img_profile, ImageView img_search) {

        Fragment selectedFragment = null;
        switch (SelectedFrg) {
            case 1:
                selectedFragment = new FistActiivty();
                break;
            case 3:
                selectedFragment = new Search();
                break;
            case 2:
                selectedFragment = new SabadKharid_s2();
                break;
            case 5:
                selectedFragment = new Cats();
                break;
            case 4:
                selectedFragment = new ListKharid();
                break;
//
//            default:
//                selectedFragment = new FistActiivty();
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();

        tv_search.setTypeface(IranSans, Typeface.BOLD);
        tv_search.setTextColor(getResources().getColor(R.color.not_selected));
        img_search.setColorFilter(ContextCompat.getColor(Home.this, R.color.not_selected), android.graphics.PorterDuff.Mode.SRC_IN);

        tv_profile.setTypeface(IranSans, Typeface.BOLD);
        tv_profile.setTextColor(getResources().getColor(R.color.not_selected));
//        img_profile.setImageDrawable(ContextCompat.getDrawable(Home.this, R.drawable.account_s));
        img_profile.setColorFilter(ContextCompat.getColor(Home.this, R.color.not_selected), android.graphics.PorterDuff.Mode.SRC_IN);

        tv_jari.setTypeface(IranSans, Typeface.BOLD);
        tv_jari.setTextColor(getResources().getColor(R.color.not_selected));
//        img_jari.setImageDrawable(ContextCompat.getDrawable(Home.this, R.drawable.menu_s));
        img_jari.setColorFilter(ContextCompat.getColor(Home.this, R.color.not_selected), android.graphics.PorterDuff.Mode.SRC_IN);

        tv_basket.setTypeface(IranSans, Typeface.BOLD);
        tv_basket.setTextColor(getResources().getColor(R.color.not_selected));
//        img_sabads.setImageDrawable(ContextCompat.getDrawable(Home.this, R.drawable.basket_s));
        img_sabads.setColorFilter(ContextCompat.getColor(Home.this, R.color.not_selected), android.graphics.PorterDuff.Mode.SRC_IN);

        tv_shop.setTypeface(IranSans, Typeface.BOLD);
        tv_shop.setTextColor(getResources().getColor(R.color.not_selected));
//        img_shop.setImageDrawable(ContextCompat.getDrawable(Home.this, R.drawable.store_s));
        img_shop.setColorFilter(ContextCompat.getColor(Home.this, R.color.not_selected), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onBackPressed() {

        if (exit) {

            ActivityCompat.finishAffinity(this);
            super.onBackPressed();
        } else {
            Toast.makeText(this, (getString(R.string.exit)), Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }
}
