package com.example.flashbulb.adapter;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.flashbulb.R;
import com.example.flashbulb.model.Post;
import com.example.flashbulb.ui.PostDetail;
import java.util.List;
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder>{
    Context mContext;
    List<Post> mData;
    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_item,parent,false);
        return new MyViewHolder(row);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvTitle.setText(mData.get(position).getTitle());
        holder.ratingBar.setRating(mData.get(position).getRatingUser());
        Glide.with(mContext).load(mData.get(position).getPicture()).into(holder.imgPost);
        Glide.with(mContext).load(mData.get(position).getUserPhoto()).into(holder.imgPostProfile);
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle;
        ImageView imgPost;
        ImageView imgPostProfile;
        RatingBar ratingBar;
        public MyViewHolder(View itemView){
            super(itemView);
            tvTitle = itemView.findViewById(R.id.title);
            imgPost = itemView.findViewById(R.id.img_pic);
            imgPostProfile = itemView.findViewById(R.id.profile_img);
            ratingBar = itemView.findViewById(R.id.PostRating);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent postDetailActivity = new Intent(mContext, PostDetail.class);
                    int position = getAdapterPosition();
                    postDetailActivity.putExtra("postKey",mData.get(position).getPostkey());
                    mContext.startActivity(postDetailActivity);
                }
            });
        }
    }
}
