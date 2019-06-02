package com.persiandesigners.freeze.Util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.DisplayMetrics;
import android.widget.RemoteViews;

import com.persiandesigners.freeze.Home;
import com.persiandesigners.freeze.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NotificationHelper {
    private Context mContext;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private String imageUrl = "";

    public NotificationHelper(Context context) {
        mContext = context;
    }

    public void createNotification(String title, String message, String imageUrl) {
        mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mBuilder.setSmallIcon(R.drawable.not);
////            mBuilder.setColor(getResources().getColor(R.color.notification_color));
//        } else {
//            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
//        }
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        makeIt();
    }

    public void createNotification(String title, String message, String imageUrl, Class<?> intentClass) {
        this.imageUrl = imageUrl;
        if (imageUrl.length() > 3) {
            new generatePictureStyleNotification(mContext,title, message,imageUrl).execute();
        } else {
            Intent resultIntent = resultIntent = new Intent(mContext, intentClass);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            resultIntent.putExtra("onvan", title);
            resultIntent.putExtra("message", message);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            TaskStackBuilder TSB = TaskStackBuilder.create(mContext);
            TSB.addParentStack(intentClass);
            TSB.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    TSB.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                mBuilder.setSmallIcon(R.drawable.not);
////            mBuilder.setColor(getResources().getColor(R.color.notification_color));
//            } else {
//                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
//            }
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setContentIntent(resultPendingIntent);

            makeIt();
        }
    }

    private void makeIt() {
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
    }

    public class generatePictureStyleNotification extends AsyncTask<String, Void, Bitmap> {

        private Context mContext;
        private String title, message, imageUrl;

        public generatePictureStyleNotification(Context context, String title, String message, String imageUrl) {
            super();
            this.mContext = context;
            this.title = title;
            this.message = message;
            this.imageUrl = imageUrl;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
            try {
                URL url = new URL(this.imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);


            Intent resultIntent = resultIntent = new Intent(mContext, Home.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            resultIntent.putExtra("onvan", title);
            resultIntent.putExtra("message", message);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            TaskStackBuilder TSB = TaskStackBuilder.create(mContext);
            TSB.addParentStack( Home.class);
            TSB.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    TSB.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                mBuilder.setSmallIcon(R.drawable.not);
////            mBuilder.setColor(getResources().getColor(R.color.notification_color));
//            } else {
//                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
//            }
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setContentIntent(resultPendingIntent)
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(result))
            ;

            makeIt();
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int width ) {
        float aspectRatio = bm.getWidth() /
                (float) bm.getHeight();
        int height = Math.round(width / aspectRatio);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                bm, width, height, false);

        return resizedBitmap;
    }
}