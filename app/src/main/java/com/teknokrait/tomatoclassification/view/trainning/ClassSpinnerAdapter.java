package com.teknokrait.tomatoclassification.view.trainning;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.teknokrait.tomatoclassification.R;
import com.teknokrait.tomatoclassification.model.Status;

import java.util.List;

/**
 * Created by Aprilian Nur Wakhid Daini on 12/28/2017.
 */

public class ClassSpinnerAdapter extends ArrayAdapter<Status> {

    private List<Status> statusList;
    private Context context;

    public ClassSpinnerAdapter(Context context, int resourceId,
                              List<Status> statusList) {
        super(context, resourceId, statusList);
        this.statusList = statusList;
        this.context = context;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Nullable
    @Override
    public Status getItem(int position) {
        return super.getItem(position);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater=(LayoutInflater) context.getSystemService(  Context.LAYOUT_INFLATER_SERVICE );
        View row=inflater.inflate(R.layout.spinner_item_class, parent, false);
        TextView label=(TextView)row.findViewById(R.id.class_textView);
        label.setText(statusList.get(position).getStatus());

        if (position == 0) {//Special style for dropdown header
            label.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }

        return row;
    }

}