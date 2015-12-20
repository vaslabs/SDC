package com.vaslabs.sdc.ui.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vaslabs.logbook.LogbookSummary;
import com.vaslabs.sdc.types.LogbookSummaryEntry;
import com.vaslabs.sdc.ui.R;

public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder> {
    private LogbookSummaryEntry[] mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView contentTextView;
        public TextView titleTextView;
        public ViewHolder(View v) {
            super(v);
            titleTextView = (TextView) v.findViewById(R.id.title_text_view);
            contentTextView = (TextView) v.findViewById(R.id.content_text_view);
        }
        public void makeView(LogbookSummaryEntry lse) {
            titleTextView.setText(lse.getTitle());
            contentTextView.setText(lse.getContent());
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CardViewAdapter(LogbookSummaryEntry[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CardViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_cardview, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.makeView(mDataset[position]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
