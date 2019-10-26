package com.example.flashbulb.adapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.flashbulb.R;
import com.example.flashbulb.model.Comment;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context mContext;
    private List<Comment> mComment;
    public CommentAdapter(Context mContext, List<Comment> mComment) {
        this.mContext = mContext;
        this.mComment = mComment; }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent,false);
        return new CommentAdapter.ViewHolder(view); }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.username.setText(mComment.get(position).getUname());
        Glide.with(mContext).load(mComment.get(position).getUimg()).into(holder.image_profile);
        holder.comment.setText(mComment.get(position).getContent());
        holder.date.setText(timestampToString((long)mComment.get(position).getTimestamp())); }
    @Override
    public int getItemCount() {
        return mComment.size(); }
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image_profile;
        TextView username,comment,date;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            image_profile = itemView.findViewById(R.id.current_img_profile);
            username = itemView.findViewById(R.id.comment_username);
            comment = itemView.findViewById(R.id.write_comment);
            date = itemView.findViewById(R.id.comment_date);
        }
    }
    private String timestampToString(long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date;
        date = DateFormat.format("HH:mm",calendar).toString();
        return date;
    }
}
