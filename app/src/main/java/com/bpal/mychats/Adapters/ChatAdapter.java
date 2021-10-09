package com.bpal.mychats.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bpal.mychats.Models.Messages;
import com.bpal.mychats.Models.User;
import com.bpal.mychats.R;
import com.bpal.mychats.Utils.Const;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter {

    private static final int ITEM_SENT = 1;
    private static final int ITEM_RECEIVE = 2;
    Context context;
    ArrayList<Messages> data;
    User user = Const.currentUser;

    public ChatAdapter(Context c, ArrayList<Messages> list){
        this.context = c;
        this.data = list;
    }

    @Override
    public int getItemViewType(int position) {
        Messages messages = data.get(position);
        if (user.getUid().equals(messages.getSenderID())) {
            return ITEM_SENT;
        } else {
            return ITEM_RECEIVE;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.view_sent, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.view_receive, parent, false);
            return new ReceiveViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Messages messages = data.get(position);

        if (holder.getClass() == SentViewHolder.class) {
            SentViewHolder viewHolder = (SentViewHolder) holder;
            viewHolder.message.setText(messages.getMessage());
            viewHolder.time.setText(Const.DateTime(Long.valueOf(messages.getTime())));
        } else {
            ReceiveViewHolder viewHolder = (ReceiveViewHolder) holder;
            viewHolder.message.setText(messages.getMessage());
            viewHolder.time.setText(Const.DateTime(Long.valueOf(messages.getTime())));
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class SentViewHolder extends RecyclerView.ViewHolder {

        TextView message, time;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.send_msg);
            time = itemView.findViewById(R.id.sTime);
        }
    }

    public class ReceiveViewHolder extends RecyclerView.ViewHolder {

        TextView message, time;
        public ReceiveViewHolder(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.rec_msg);
            time = itemView.findViewById(R.id.rTime);
        }
    }
}
