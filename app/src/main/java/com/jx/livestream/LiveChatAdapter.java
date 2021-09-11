package com.jx.livestream;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jx.livestream.databinding.LiveChatItemBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class LiveChatAdapter extends RecyclerView.Adapter<LiveChatAdapter.LiveChatItemViewHolder>{
    private Context context;
    private List<String> chatContentList;

    public LiveChatAdapter(Context context) {
        this.context = context;
        chatContentList = new ArrayList<>();
        for (int i = 0; i<50; i++)
        {
            chatContentList.add("用户 "+i+"说点什么呗！");
        }

    }

    @NonNull
    @Override
    public LiveChatItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LiveChatItemBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.
                from(context),R.layout.live_chat_item,parent,false);
        return new LiveChatItemViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull LiveChatItemViewHolder holder, int position) {
        Picasso.get().load(R.drawable.default_avatar).into(holder.liveChatItemBinding.liveChatItemAvatar);
        holder.liveChatItemBinding.liveChatItemText.setText(chatContentList.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return chatContentList.size();
    }

    public static class LiveChatItemViewHolder extends RecyclerView.ViewHolder {
        private LiveChatItemBinding liveChatItemBinding;

        public LiveChatItemViewHolder(@NonNull LiveChatItemBinding liveChatItemBinding) {
            super(liveChatItemBinding.getRoot());
            this.liveChatItemBinding = liveChatItemBinding;
        }
    }
}
