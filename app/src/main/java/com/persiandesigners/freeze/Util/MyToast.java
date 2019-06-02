package com.persiandesigners.freeze.Util;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.persiandesigners.freeze.R;

public class MyToast {
    public static void makeText(Context context, String message) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "IRAN Sans Bold.ttf");
        android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f);

        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        TextView textView = new TextView(context);
        textView.setBackgroundColor(Color.parseColor("#cc273D74"));
        textView.setBackgroundResource(R.drawable.tags_rounded_corners);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.toast));
        textView.setTypeface(typeface);
        textView.setPadding(10, 10, 10, 12);
        textView.setText(message);
        textView.setLayoutParams(params);
        textView.setGravity(Gravity.RIGHT);
        toast.setView(textView);
        toast.show();
    }
}