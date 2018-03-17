package com.memoseed.simpletwitterclient.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.makeramen.roundedimageview.RoundedImageView;
import com.memoseed.simpletwitterclient.R;
import com.memoseed.simpletwitterclient.activities.FollowerInformation;
import com.memoseed.simpletwitterclient.activities.FollowerInformation_;
import com.twitter.sdk.android.core.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserFollowersRVAdapter extends RecyclerView.Adapter<UserFollowersRVAdapter.ViewHolderItem> {

    public List<User> listUsers = new ArrayList<>();
    Context context;

    String TAG = getClass().getSimpleName();

    public static class ViewHolderItem extends RecyclerView.ViewHolder {

        TextView txtName, txtBio;
        ImageView imLine;
        RoundedImageView imPic;
        LinearLayout linItem;


        public ViewHolderItem(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtBio = itemView.findViewById(R.id.txtBio);
            imPic = itemView.findViewById(R.id.imPic);
            imLine = itemView.findViewById(R.id.imLine);
            linItem = itemView.findViewById(R.id.linItem);
        }


    }


    public UserFollowersRVAdapter(List<User> listUsers, Context context) {
        this.listUsers = listUsers;
        this.context = context;
    }

    @Override
    public ViewHolderItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_user_followers, parent, false);
        return new ViewHolderItem(view);
    }

    public void addUser(User userFollower) {
        listUsers.add(userFollower);
        notifyDataSetChanged();
    }


    public void addUsers(List<User> userFollowers) {
        listUsers.addAll(userFollowers);
        notifyDataSetChanged();
    }

    public void removeUsers() {
        listUsers.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolderItem holder, int position) {
        User user = listUsers.get(position);

        holder.imLine.setVisibility(View.VISIBLE);
        Glide.with(context).load(user.profileImageUrl).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Glide.with(context).load(R.drawable.avatar).into(holder.imPic);
                e.printStackTrace();
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(holder.imPic);
        holder.txtName.setText(user.name);

        if (user.description != null && !user.description.isEmpty()) {
            holder.txtBio.setVisibility(View.VISIBLE);
            holder.txtBio.setText(user.description);
        }
        else holder.txtBio.setVisibility(View.GONE);

        holder.linItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               context.startActivity(new Intent(context, FollowerInformation_.class)
                       .putExtra("name",user.name)
                       .putExtra("description",user.description)
                       .putExtra("profileImageUrl",user.profileImageUrl)
                       .putExtra("profileBannerUrl",user.profileBannerUrl)
                       .putExtra("id",user.id));
            }
        });

    }

    @Override
    public int getItemCount() {
        return listUsers.size();
    }
}
