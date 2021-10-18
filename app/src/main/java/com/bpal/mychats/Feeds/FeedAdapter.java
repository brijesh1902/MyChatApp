package com.bpal.mychats.Feeds;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bpal.mychats.R;
import com.bpal.mychats.Utils.Const;
import com.bpal.mychats.databinding.EventRowBinding;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private List<Event> items;
    Context context;
    String time;

    public class ViewHolder extends RecyclerView.ViewHolder {
        EventRowBinding binding;
        TextView channel, event, data, timestamp;
        public ViewHolder(@NonNull View view) {
            super(view);
            channel = view.findViewById(R.id.channel);
            data = view.findViewById(R.id.data);
            event = view.findViewById(R.id.event);
            timestamp = view.findViewById(R.id.timestamp);
        }
    }

    public FeedAdapter(Context c, List<Event> item, String time) {
        this.context = c;
        this.items = item;
        this.time = time;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public FeedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View v = layoutInflater.inflate(R.layout.event_row, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FeedAdapter.ViewHolder viewHolder, int i) {

        Event data = items.get(i);

        viewHolder.event.setText(data.getEvent());
        viewHolder.channel.setText(data.getChannel());
        viewHolder.data.setText("Message: "+data.getData());

        if (time!=null)
            viewHolder.timestamp.setText(Const.DateTime(Long.valueOf(time)));

    }
}