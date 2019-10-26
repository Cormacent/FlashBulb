package com.example.flashbulb.ui;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.example.flashbulb.R;
import com.example.flashbulb.adapter.PostAdapter;
import com.example.flashbulb.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
public class TopPosts extends AppCompatActivity {
    RecyclerView postrecyclerView;
    List<Post> postList;
    DatabaseReference sortPosts = FirebaseDatabase.getInstance().getReference().child("Posts");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_posts);
        postrecyclerView = findViewById(R.id.postRVTop);
        LinearLayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        postrecyclerView.setLayoutManager(layoutManager);
        postrecyclerView.setHasFixedSize(true);
        sortPosts.orderByChild("ratingUser").limitToLast(5).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList = new ArrayList<>();
                for(DataSnapshot postSnap: dataSnapshot.getChildren()){
                    Post post = postSnap.getValue(Post.class);
                    postList.add(post);
                }
                //postAdapter isinya buat ambil data dari database
                PostAdapter postAdapter = new PostAdapter(TopPosts.this,postList);
                postrecyclerView.setAdapter(postAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
