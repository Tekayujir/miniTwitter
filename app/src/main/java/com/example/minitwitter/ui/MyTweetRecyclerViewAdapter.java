package com.example.minitwitter.ui;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.minitwitter.R;
import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.common.SharedPreferencesManager;
import com.example.minitwitter.retrofit.response.Like;
import com.example.minitwitter.retrofit.response.Tweet;
import java.util.List;

public class MyTweetRecyclerViewAdapter extends RecyclerView.Adapter<MyTweetRecyclerViewAdapter.ViewHolder> {

    private List<Tweet> mValues;
    private Context ctx;
    String username;

    public MyTweetRecyclerViewAdapter(Context contexto, List<Tweet> items) {
        mValues = items;
        ctx = contexto;
        username = SharedPreferencesManager.getSomeStringValue(Constantes.PREF_USERNAME);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_tweet, parent, false); // R.layout... FRAGMENT DASHBOARD
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(mValues != null){
            holder.mItem = mValues.get(position);

            holder.tvUsername.setText("@"+holder.mItem.getUser().getUsername());
            holder.tvMessage.setText(holder.mItem.getMensaje());
            holder.tvLikesCount.setText(String.valueOf(holder.mItem.getLikes().size()));

            // Carga la foto del usuario en el tweet
            String photo = holder.mItem.getUser().getPhotoUrl();
            if(!photo.equals("")){
                Glide.with(ctx)
                        .load("https://www.minitwitter.com/apiv1/uploads/photos/" +photo)
                        .into(holder.ivAvatar);
            }

            Glide.with(ctx)
                    .load(R.drawable.ic_thumb_up_black_24dp)
                    .into(holder.ivLike);
            holder.tvLikesCount.setTextColor(ctx.getResources().getColor(R.color.colorGris));
            holder.tvLikesCount.setTypeface(null, Typeface.NORMAL);

            // Recorremos la lista para saber si estamos en esa lista y le hemos dado like
            for(Like like: holder.mItem.getLikes()){
                // si este USERNAME(que es único) es el mismo que el del usuario que está logeado, ha dado like (por lo tanto cambiamos el color de la imagen t el contador Like)
                if(like.getUsername().equals(username)){
                    Glide.with(ctx)
                            .load(R.drawable.ic_thumb_up_pink_24dp)
                            .into(holder.ivLike);
                    holder.tvLikesCount.setTextColor(ctx.getResources().getColor(R.color.colorRosa));
                    holder.tvLikesCount.setTypeface(null, Typeface.BOLD);
                    break;
                }
            }
        }
    }

    public void setData(List<Tweet> tweetList){
        this.mValues = tweetList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(mValues != null){
            return mValues.size();
        }else return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView ivAvatar;
        public final ImageView ivLike;
        public final TextView tvUsername;
        public final TextView tvMessage;
        public final TextView tvLikesCount;
        public Tweet mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ivAvatar = view.findViewById(R.id.imageViewAvatar);
            ivLike = view.findViewById(R.id.imageViewLike);
            tvUsername = view.findViewById(R.id.textViewUserName);
            tvMessage = view.findViewById(R.id.textViewMessage);
            tvLikesCount = view.findViewById(R.id.textViewLikes);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvUsername.getText() + "'";
        }
    }
}
