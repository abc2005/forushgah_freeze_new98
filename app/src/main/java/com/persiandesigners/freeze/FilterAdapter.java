package com.persiandesigners.freeze;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by navid on 3/30/2017.
 */
public class FilterAdapter extends ArrayAdapter<ParamsVal> {
    Context context;
    int layoutResourceId;
    private List<ParamsVal> values;
    Typeface tf;

    public FilterAdapter(Context context, int layoutResourceId, List<ParamsVal> values) {
        super(context, layoutResourceId, values);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.values = values;
        tf = Typeface.createFromAsset(context.getAssets(), "B Traffic Bold_0.ttf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        AppInfoHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.filter_row, parent, false);

            holder = new AppInfoHolder();
            holder.txtTitle = (TextView) row.findViewById(R.id.tv);
            holder.txtTitle.setTypeface(tf);
            holder.chkSelect = (CheckBox) row.findViewById(R.id.check);
            row.setTag(holder);
        } else {
            holder = (AppInfoHolder) row.getTag();
        }

        holder.txtTitle.setText(values.get(position).getName());
        holder.chkSelect.setChecked(values.get(position).getChecked());
        return row;
    }

    public class AppInfoHolder {
        TextView txtTitle;
        CheckBox chkSelect;
    }

}
