package com.example.flashbulb.ui;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.example.flashbulb.R;
import com.example.flashbulb.adapter.PostCurrentUserAdapter;
import com.example.flashbulb.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
public class UserPosts extends AppCompatActivity {
    RecyclerView postrecyclerView;
    List<Post> postList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_posts);
        postrecyclerView = findViewById(R.id.postRV2);
        LinearLayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        postrecyclerView.setLayoutManager(layoutManager);
        postrecyclerView.setHasFixedSize(true);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserPosts");
        assert currentUser != null;
        databaseReference.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList = new ArrayList<>();
                for(DataSnapshot postSnap: dataSnapshot.getChildren()){
                    Post post = postSnap.getValue(Post.class);
                    postList.add(post);
                }
                //postAdapter isinya buat ambil data dari database
                PostCurrentUserAdapter postCurrentUserAdapter = new PostCurrentUserAdapter(UserPosts.this,postList);
                postrecyclerView.setAdapter(postCurrentUserAdapter);
        }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
