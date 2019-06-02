package com.persiandesigners.freeze.Util;

import android.app.Dialog;
import android.content.Context;

import com.persiandesigners.freeze.R;


/**
 * Created by navid on 11/10/2015.
 */
public class CustomProgressDialog extends Dialog {
    MaterialProgressBar progress1;

    Context mContext;
    CustomProgressDialog dialog;

    public CustomProgressDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public CustomProgressDialog show(CharSequence message) {

        dialog = new CustomProgressDialog(mContext, R.style.ProgressDialog);
        dialog.setContentView(R.layout.view_material_progress);

        progress1 = (MaterialProgressBar) dialog.findViewById(R.id.progress1);


        try {
            progress1.setColorSchemeResources(R.color.red, R.color.green, R.color.blue, R.color.orange);
            dialog.setCancelable(true);
            if (dialog != null) {
                dialog.show();
            }
        } catch (Exception e) {

        }
        return dialog;
    }

    public CustomProgressDialog dismiss(CharSequence message) {
        if (dialog != null) {
            dialog.dismiss();
        }

        return dialog;

    }


}
