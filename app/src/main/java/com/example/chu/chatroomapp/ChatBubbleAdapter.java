package com.example.chu.chatroomapp;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.List;

/*
TruTel Communications - August 2014
Created by Benjamin Chu - Simple G2Sky Chat Application w/ XMPP Connection to eJabberd server
 */

public class ChatBubbleAdapter extends ArrayAdapter<Message> {

    private static final String TAG = "ChatBubbleAdapter";

    private Context mContext;
    protected ArrayList<Message> smackMessageList;

    public ChatBubbleAdapter(Context context, ArrayList<Message> smackMessages) {
        super(context, R.layout.msg_row, smackMessages);
        mContext = context;
        smackMessageList = smackMessages;
    }

    public int getCount() {
        return smackMessageList.size();
    }

    public Message getItem(int position) {
        return smackMessageList.get(position);
    }

    public long getItemsId(int position) {
        return position;
    }

    static class ViewHolder {
        public TextView message;
        public TextView fromMessage;
        public LinearLayout bubble;
        public LinearLayout parent_bubble;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        Message getInfoMessage = (Message) this.getItem(position);
        if (getInfoMessage.getType().toString().equals("headline")) {
            return 3;
        } else {
            String name1 = Lobby.userName;
            String name2 = getInfoMessage.getFrom().substring(Lobby.lengthOfRoom + 1);
            if (name1.equals(name2)) {
                return 0;
            } else {
                return 1;
            }
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = (Message) this.getItem(position);
        Log.i(TAG, "Message Body: " + message.getBody());
        Log.i(TAG, "Message Type: " + message.getType());
        Log.i(TAG, "this is the item type: " + getItemViewType(position));

        int layoutNum = getItemViewType(position);

        if (layoutNum == 0) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.msg_row, parent, false);
                holder.fromMessage = (TextView) convertView.findViewById(R.id.message_from);
                holder.message = (TextView) convertView.findViewById(R.id.message_text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.fromMessage.setText(message.getFrom().substring(Lobby.lengthOfRoom + 1));
            holder.message.setText(message.getBody());
            holder.bubble = (LinearLayout) convertView.findViewById(R.id.bubblelayout);
            holder.bubble.setGravity(Gravity.RIGHT);
            holder.parent_bubble = (LinearLayout) convertView.findViewById(R.id.parentbubblelayout);
            holder.parent_bubble.setGravity(Gravity.RIGHT);
            holder.message.setBackgroundResource(R.drawable.speech_bubble_green);
            holder.message.setTextColor(Color.BLACK);

        } else if (layoutNum == 1) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.msg_row, parent, false);
                holder.fromMessage = (TextView) convertView.findViewById(R.id.message_from);
                holder.message = (TextView) convertView.findViewById(R.id.message_text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.message.setBackgroundResource(R.drawable.speech_bubble_orange);
            holder.message.setTextColor(Color.BLACK);
            holder.message.setText(message.getBody());
            holder.fromMessage.setText(message.getFrom().substring(Lobby.lengthOfRoom + 1));
            holder.bubble = (LinearLayout) convertView.findViewById(R.id.bubblelayout);
            holder.bubble.setGravity(Gravity.LEFT);
            holder.parent_bubble = (LinearLayout) convertView.findViewById(R.id.parentbubblelayout);
            holder.parent_bubble.setGravity(Gravity.LEFT);

        } else {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.msg_row, parent, false);
                holder.fromMessage = (TextView) convertView.findViewById(R.id.message_from);
                holder.message = (TextView) convertView.findViewById(R.id.message_text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.message.setText(message.getBody());
            holder.fromMessage.setText(message.getFrom());
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.message.getLayoutParams();
            holder.message.setTextColor(Color.BLUE);
            lp.gravity = Gravity.LEFT;
            holder.message.setLayoutParams(lp);
        }
        return convertView;
    }
}