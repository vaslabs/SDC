package com.vaslabs.sdc.ui.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.vaslabs.sdc.logs.LogbookStats;
import com.vaslabs.sdc.ui.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vnicolao on 27/06/15.
 */
public class LogBookAdapter extends BaseAdapter {

    private final Object[] names;
    private final Map<String, Float> values;
    private final Context context;
    public LogBookAdapter(LogbookStats stats, Context context) {
        values = new HashMap<String, Float>();
        values.put(context.getString(R.string.freeFallTime), stats.getFreeFallTime());
        values.put(context.getString(R.string.maximumSpeed), stats.getMaximumSpeed());
        values.put(context.getString(R.string.deploymentAltitude), stats.getDeploymentAltitude());
        values.put(context.getString(R.string.exitAltitude), stats.getExitAltitude());
        //values.put(context.getString(R.string.maxAltitude), stats.getMaxAltitude());
        this.context = context;

        names = values.keySet().toArray();
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int i) {
        return values.get(names[i].toString());
    }

    @Override
    public long getItemId(int i) {
        return names[i].toString().hashCode();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View viewFromTemplate = LayoutInflater.from(context).inflate(R.layout.logbook_item_view, null);
        TextView attributeNameTextView = (TextView) viewFromTemplate.findViewById(R.id.itemNameTextView);
        EditText attributeValueEditText = (EditText) viewFromTemplate.findViewById(R.id.valueEditView);
        attributeNameTextView.setText(names[i].toString());
        attributeValueEditText.setText(String.valueOf(values.get(names[i].toString())));

        return viewFromTemplate;

    }
}
