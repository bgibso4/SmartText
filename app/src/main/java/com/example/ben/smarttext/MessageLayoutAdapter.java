package com.example.ben.smarttext;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import java.util.List;

public class MessageLayoutAdapter extends RecyclerView.Adapter<MessageLayoutAdapter.MessageViewHolder> {

    List<TextMessage> dataSet;

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView messageRecipient;
        TextView messageDate;
        TextView messageBody;
        MessageViewHolder(View v) {
            super(v);
            messageRecipient = v.findViewById(R.id.recipientName);
            messageDate = v.findViewById(R.id.messageDate);
            messageBody = v.findViewById(R.id.messageBody);
        }
    }

    MessageLayoutAdapter(List<TextMessage> ds){
        this.dataSet = ds;
    }

    @NonNull
    @Override
    public MessageLayoutAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_message_view, parent, false);
        return new MessageViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull MessageLayoutAdapter.MessageViewHolder holder, int position) {
        holder.messageRecipient.setText(dataSet.get(position).getName());
        holder.messageDate.setText(String.valueOf(dataSet.get(position).timeAway()));
        holder.messageBody.setText(dataSet.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return this.dataSet.size();
    }



}
