package com.example.flashbulb.ui;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.flashbulb.R;
import com.example.flashbulb.adapter.CommentAdapter;
import com.example.flashbulb.model.Comment;
import com.example.flashbulb.model.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
public class PostDetail extends AppCompatActivity {
    ImageView imgPost, imgUserPost,imgCurrentUser,popupzoomImage;
    TextView  txtPostUsername,txtPostDesc, txtPostDateName, txtPostTitle,ratingCount,average,userSumTotal;
    EditText editTextComment;
    FloatingActionButton btnRating;
    ImageView btnAddComment;
    Dialog popAddRating,popViewImage;
    RecyclerView commentrecyclerView;
    CommentAdapter commentAdapter;
    List<Comment> commentList;
    //popupRating
    ImageView popupUserImage;
    TextView popuppostUsername;
    RatingBar popupRatingbar;
    //popupProfile
    ImageView popupProfile_image;
    Dialog popupProfilePostUser;
    TextView popupProfileName,popupUserEmail,popuppostCount,popupratingCount;
    //Extras
    String PostKey,postUserId,postuserName,userpostImage,postUserEmail;
    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference ratingReference;
    DatabaseReference postReference = FirebaseDatabase.getInstance().getReference("Posts");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        //buat transparant statusbar
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        imgPost = findViewById(R.id.img_pic_post);
        imgUserPost = findViewById(R.id.post_detail_user_img);
        imgCurrentUser = findViewById(R.id.profile_img_post);
        txtPostTitle = findViewById(R.id.title_post);
        txtPostDesc = findViewById(R.id.detail_desc);
        txtPostUsername = findViewById(R.id.post_username);
        txtPostDateName = findViewById(R.id.detail_date);
        ratingCount = findViewById(R.id.ratingCount);
        average = findViewById(R.id.average);
        userSumTotal = findViewById(R.id.ratingUsers);
        editTextComment = findViewById(R.id.write_comment);
        btnAddComment = findViewById(R.id.add_comment_btn);
        btnRating = findViewById(R.id.btnRating);
        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        //zoomingImage
        imgPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popViewImage.show();
            }
        });
        //Rating
        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAddRating.show();
            }
        });
        //tambah button clik buat comment
        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAddComment.setVisibility(View.INVISIBLE);
                String comment_content = editTextComment.getText().toString();
                String uid = firebaseUser.getUid();
                String uname = firebaseUser.getDisplayName();
                String uimg = (firebaseUser.getPhotoUrl()).toString();
                if (editTextComment.getText().toString().isEmpty()){
                    Toast.makeText(PostDetail.this,"Tidak Dapat Mengirim Teks Kosong!",Toast.LENGTH_SHORT).show();
                    btnAddComment.setVisibility(View.VISIBLE);
                }else {
                    DatabaseReference commentReference = firebaseDatabase.getReference("Comment").child(PostKey).push();
                    Comment comment = new Comment(comment_content,uid,uimg,uname);
                    commentReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showMessage("komentar ditambahkan");
                            editTextComment.setText("");
                            btnAddComment.setVisibility(View.VISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showMessage("gagal menambahkan komentar:"+e.getMessage());
                        }
                    });
                }
            }
        });
        imgUserPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupProfilePostUser.show();

            }
        });
        //buat poto akun comment
        Glide.with(this).load(firebaseUser.getPhotoUrl()).into(imgCurrentUser);
        //buat dapetin ID
        PostKey = getIntent().getStringExtra("postKey");
        postReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.child(PostKey).getValue(Post.class);
                if(post != null){
                    txtPostTitle.setText(post.getTitle());
                    txtPostDesc.setText(post.getDescription());
                    Glide.with(getApplication()).load(post.getUserPhoto()).into(imgUserPost);
                    Glide.with(getApplication()).load(post.getPicture()).into(imgPost);
                    Glide.with(getApplication()).load(post.getPicture()).into(popupzoomImage);
                    String date = timestampToString((Long) post.getTimeStamp());
                    txtPostDateName.setText(date);
                    txtPostUsername.setText(post.getUserName());
                    postUserId = post.getUserId();
                    postUserEmail = post.getUserEmail();
                    userpostImage = post.getUserPhoto();
                    postuserName = post.getUserName();
                    Glide.with(getApplication()).load(post.getUserPhoto()).into(popupUserImage);
                    popuppostUsername.setText(post.getUserName());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        //buat post comment
        commentrecyclerView = findViewById(R.id.postRV);
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(this);
        commentrecyclerView.setLayoutManager(layoutManager);
        commentrecyclerView.setHasFixedSize(true);
        //buat post Rating
        ratingReference = firebaseDatabase.getReference("Posts").child(PostKey);
        ratingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    float ratingSum = 0;
                    float ratingsTotal = 0;
                    float ratingsAvg = 0;
                    for (DataSnapshot child : dataSnapshot.child("Rating").getChildren()) {
                        ratingSum = ratingSum + Float.valueOf((child.getValue()).toString());
                        ratingsTotal++;
                    }
                    if (ratingsTotal != 0) {
                        ratingsAvg = ratingSum/ratingsTotal;
                        String ratingCountAverage = String.valueOf(ratingsAvg);
                        ratingCount.setText(ratingCountAverage);

                        assert postUserId != null;
                        DatabaseReference postRatingAverage = FirebaseDatabase.getInstance().getReference("UserPosts")
                                .child("RatingPosts").child(postUserId).child(PostKey);
                        postRatingAverage.setValue(ratingsAvg);
                        DatabaseReference ratingUserclass = FirebaseDatabase.getInstance().getReference("Posts");
                        ratingUserclass.child(PostKey).child("ratingUser").setValue(ratingsAvg);
                        DatabaseReference ratingUserCount = FirebaseDatabase.getInstance().getReference("UserPosts");
                        ratingUserCount.child(postUserId).child(PostKey).child("ratingUser").setValue(ratingsAvg);
                        int ratingTotalUsers = (int) ratingsTotal;
                        String ratingUsers = String.valueOf(ratingTotalUsers);
                        userSumTotal.setText(ratingUsers);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        ratingPopup();
        Profilepopup();
        zoomImagePopup();
    }

    private void zoomImagePopup() {
        popViewImage = new Dialog(this);
        popViewImage.setContentView(R.layout.view_image);
        popViewImage.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popViewImage.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popViewImage.getWindow().getAttributes().gravity = Gravity.CENTER_HORIZONTAL;
        popupzoomImage= popViewImage.findViewById(R.id.zoomingView);
    }
    private void Profilepopup() {
        popupProfilePostUser = new Dialog(this);
        popupProfilePostUser.setContentView(R.layout.popup_post_profile);
        popupProfilePostUser.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupProfilePostUser.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popupProfilePostUser.getWindow().getAttributes().gravity = Gravity.TOP;
        popupProfile_image = popupProfilePostUser.findViewById(R.id.profile_img);
        popupProfileName = popupProfilePostUser.findViewById(R.id.profilename);
        popupUserEmail = popupProfilePostUser.findViewById(R.id.mail);
        popupratingCount = popupProfilePostUser.findViewById(R.id.ratingCount);
        popuppostCount = popupProfilePostUser.findViewById(R.id.postcount);
        LinearLayout allCurrentUserPost = popupProfilePostUser.findViewById(R.id.allcurrentUserPosts);
        allCurrentUserPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent postUser = new Intent(PostDetail.this,OtherUserPosts.class);
                postUser.putExtra("idUser",postUserId);
                startActivity(postUser);
            }
        });
        Glide.with(getApplicationContext()).load(userpostImage).into(popupProfile_image);
        postReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.child(PostKey).getValue(Post.class);
                if(post != null){
                    Glide.with(getApplication()).load(post.getUserPhoto()).into(popupProfile_image);
                    popupProfileName.setText(post.getUserName());
                    popupUserEmail.setText(post.getUserEmail());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        DatabaseReference countRatingPost = FirebaseDatabase.getInstance().getReference("UserPosts").child("RatingPosts");
        countRatingPost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    float ratingSum = 0;
                    float ratingsTotal = 0;
                    double ratingsAvg = 0;
                    for (DataSnapshot child : dataSnapshot.child(postUserId).getChildren()) {
                        ratingSum = ratingSum + Float.valueOf((child.getValue()).toString());
                        ratingsTotal++;
                    }
                    if (ratingsTotal != 0) {
                        ratingsAvg = ratingSum/ratingsTotal;
                        popupratingCount.setText(new DecimalFormat("##.##").format(ratingsAvg));
                    }
                    else{
                        popupratingCount.setText("0");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        DatabaseReference countPostUser = FirebaseDatabase.getInstance().getReference("UserPosts");
        countPostUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    int totalPost = 0;
                    totalPost = (int) dataSnapshot.child(postUserId).getChildrenCount();
                    popuppostCount.setText(Integer.toString(totalPost));
                }
                else {
                    popuppostCount.setText("0");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void ratingPopup() {
        popAddRating = new Dialog(this);
        popAddRating.setContentView(R.layout.popup_rating);
        popAddRating.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddRating.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popAddRating.getWindow().getAttributes().gravity = Gravity.HORIZONTAL_GRAVITY_MASK;
        //popup widget
        popupUserImage = popAddRating.findViewById(R.id.profile_img);
        popuppostUsername = popAddRating.findViewById(R.id.userName);
        popupRatingbar = popAddRating.findViewById(R.id.AddRating);
        //ambil poto profile user
        String userpostImage = getIntent().getExtras().getString("userPhoto");
        Glide.with(this).load(userpostImage).into(popupUserImage);
        postuserName = getIntent().getExtras().getString("userName");
        popuppostUsername.setText(postuserName);
        popupRatingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingReference = firebaseDatabase.getReference("Posts").child(PostKey).child("Rating").child(firebaseUser.getUid());
                ratingReference.setValue(rating);
                Toast.makeText(PostDetail.this,"Terima kasih atas penilaian Anda!",Toast.LENGTH_SHORT).show();
                popAddRating.dismiss();
            }
        });
        ratingReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot postRate: dataSnapshot.getChildren()){
                        if((postRate.getKey()).equals("Rating")){
                            popupRatingbar.setRating(Integer.valueOf((postRate.getValue()).toString()));
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference commentReference = firebaseDatabase .getReference("Comment").child(PostKey);
        //get list post from databaase
        commentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList = new ArrayList<>();
                for(DataSnapshot postSnap: dataSnapshot.getChildren()){
                    Comment comment = postSnap.getValue(Comment.class);
                    commentList.add(comment);
                }
                //postAdapter isinya buat ambil data dari database
                commentAdapter = new CommentAdapter(PostDetail.this,commentList);
                commentrecyclerView.setAdapter(commentAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void showMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }
    private String timestampToString(long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date;
        date = DateFormat.format("dd-MM-yyyy",calendar).toString();
        return date;
    }
}
