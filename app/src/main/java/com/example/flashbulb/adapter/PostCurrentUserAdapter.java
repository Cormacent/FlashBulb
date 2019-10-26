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
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.flashbulb.R;
import com.example.flashbulb.model.Post;
import com.example.flashbulb.ui.PostDetail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;
public class PostCurrentUserAdapter extends RecyclerView.Adapter<PostCurrentUserAdapter.MyViewHolder> {
    Context mContext;
    List<Post> mData;
    public PostCurrentUserAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_currentuser_item,parent,false);
        return new MyViewHolder(row);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.tvTitle.setText(mData.get(position).getTitle());
        holder.ratingBar.setRating(mData.get(position).getRatingUser());
        Glide.with(mContext).load(mData.get(position).getPicture()).into(holder.imgPost);
        Glide.with(mContext).load(mData.get(position).getUserPhoto()).into(holder.imgPostProfile);
        holder.deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Posting Dihapus", Toast.LENGTH_SHORT).show();
                Post adapter = mData.get(position);
                removePost(adapter);
            }
        });
    }
    private void removePost(Post adapter) {
        int currentposition = mData.indexOf(adapter);
        String postKey = mData.get(currentposition).getPostkey();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference deletefromCurrentPost = FirebaseDatabase.getInstance().getReference("UserPosts");
        DatabaseReference deletefromPublicPost = FirebaseDatabase.getInstance().getReference("Posts");
        DatabaseReference deletefromCommentPost = FirebaseDatabase.getInstance().getReference("Comment");
        DatabaseReference deletefromRatingPost = FirebaseDatabase.getInstance().getReference("UserPosts").child("RatingPosts");
        assert currentUser != null;
        deletefromCurrentPost.child(currentUser.getUid()).child(postKey).removeValue();
        deletefromPublicPost.child(postKey).removeValue();
        deletefromCommentPost.child(postKey).removeValue();
        deletefromRatingPost.child(currentUser.getUid()).child(postKey).removeValue();
        mData.remove(currentposition);
        notifyItemRemoved(currentposition);
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView imgPost,imgPostProfile,deletePost;
        RatingBar ratingBar;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.title);
            imgPost = itemView.findViewById(R.id.img_pic);
            imgPostProfile = itemView.findViewById(R.id.profile_img);
            ratingBar = itemView.findViewById(R.id.PostRating);
            deletePost = itemView.findViewById(R.id.deletePost);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent postDetailActivity = new Intent(mContext, PostDetail.class);
                    int position = getAdapterPosition();
                    postDetailActivity.putExtra("title",mData.get(position).getTitle());
                    postDetailActivity.putExtra("postImage",mData.get(position).getPicture());
                    postDetailActivity.putExtra("description",mData.get(position).getDescription());
                    postDetailActivity.putExtra("postKey",mData.get(position).getPostkey());
                    postDetailActivity.putExtra("userPhoto",mData.get(position).getUserPhoto());
                    postDetailActivity.putExtra("userName",mData.get(position).getUserName());
                    postDetailActivity.putExtra("userId",mData.get(position).getUserId());
                    postDetailActivity.putExtra("userEmail",mData.get(position).getUserEmail());
                    long timestamp = (long) mData.get(position).getTimeStamp();
                    postDetailActivity.putExtra("postDate",timestamp);
                    mContext.startActivity(postDetailActivity);
                }
            });
        }
    }
}
