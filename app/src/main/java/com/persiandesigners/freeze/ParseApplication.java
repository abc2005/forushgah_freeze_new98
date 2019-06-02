package com.persiandesigners.freeze;


import com.google.firebase.FirebaseApp;

public class ParseApplication extends android.app.Application {

  @Override
  public void onCreate() {
    super.onCreate();

    if(getResources().getBoolean(R.bool.has_notification))
        FirebaseApp.initializeApp(this);

//    Cheshmak.with(this);
//    Cheshmak.initTracker("b6Cc+Vu/FkUclHz4Z7da6Q==");

  }
}