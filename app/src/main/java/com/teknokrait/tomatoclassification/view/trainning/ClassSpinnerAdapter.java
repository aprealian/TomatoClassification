package com.teknokrait.tomatoclassification.view.trainning;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.teknokrait.tomatoclassification.R;

import java.util.List;

/**
 * Created by Aprilian Nur Wakhid Daini on 12/28/2017.
 */

public class ClassSpinnerAdapter extends ArrayAdapter<String> {

    private List<String> objects;
    private Context context;

    public ClassSpinnerAdapter(Context context, int resourceId,
                              List<String> objects) {
        super(context, resourceId, objects);
        this.objects = objects;
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

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater=(LayoutInflater) context.getSystemService(  Context.LAYOUT_INFLATER_SERVICE );
        View row=inflater.inflate(R.layout.spinner_item_class, parent, false);
        TextView label=(TextView)row.findViewById(R.id.class_textView);
        label.setText(objects.get(position));

        if (position == 0) {//Special style for dropdown header
            label.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }

        return row;
    }

}