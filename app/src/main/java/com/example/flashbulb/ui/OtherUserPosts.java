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
public class OtherUserPosts extends AppCompatActivity {
    RecyclerView postrecyclerView;
    PostAdapter postAdapter;
    List<Post> postList;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String IdUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_posts);
        postrecyclerView = findViewById(R.id.postRV1);
        IdUser = getIntent().getStringExtra("idUser");
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setReverseLayout(true);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
        postrecyclerView.setLayoutManager(layoutManager);
        postrecyclerView.setHasFixedSize(true);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("UserPosts").child(IdUser);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList = new ArrayList<>();
                for(DataSnapshot postSnap: dataSnapshot.getChildren()){
                    Post post = postSnap.getValue(Post.class);
                    postList.add(post);
                }
                //postAdapter isinya buat ambil data dari database
                postAdapter = new PostAdapter(OtherUserPosts.this,postList);
                postrecyclerView.setAdapter(postAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
