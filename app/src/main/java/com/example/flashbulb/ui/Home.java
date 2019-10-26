package com.example.flashbulb.ui;
import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.flashbulb.R;
import com.example.flashbulb.adapter.PostAdapter;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
public class Home extends AppCompatActivity {
    ImageView profile_image;
    TextView profileName;
    TextView popupLogOut;
    private static final int PReCode = 2;
    private static final int REQUESTCODE =2 ;
    //firebase
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference profile_user = FirebaseDatabase.getInstance().getReference();
    //popupAdposts
    Dialog popAddpost;
    ImageView popupUserImage,popupPostImage,popupAddBtn;
    TextView popupTitle, popupDecscription,popupPostHint,ratingCount,postCount;
    //popupProfile
    ImageView popupProfile_image;
    Dialog popProfileCurrentUser;
    TextView popupUserEmail,popupProfileName;
    //popupInfo
    Dialog popupInfo;
    ProgressBar popupClickProgress;
    private Uri pickedImgUri = null;
    RecyclerView postrecyclerView;
    PostAdapter postAdapter;
    List<Post> postList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        profile_image = findViewById(R.id.profile_img);
        profileName = findViewById(R.id.profileName);
        CircleMenu circleMenu = findViewById(R.id.circleMenu);
        //firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        circleMenu.setMainMenu(Color.parseColor("#AA0A0A"),R.drawable.ic_add_24dp,R.drawable.ic_clear_24dp)
                .addSubMenu(Color.parseColor("#D04545"),R.drawable.ic_create_black_24dp)
                .addSubMenu(Color.parseColor("#ffb300"),R.drawable.rankingcup)
                .addSubMenu(Color.parseColor("#0000CD"),R.drawable.info)
                .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                    @Override
                    public void onMenuSelected(int index) {
                        if(index == 0){
                            popAddpost.show();
                            popupTitle.setText("");
                            popupDecscription.setText("");
                            popupPostImage.setImageResource(R.drawable.image_post);
                            popupPostHint.setVisibility(View.VISIBLE);
                        }else if(index == 1){
                            Intent toTopRank = new Intent(Home.this,TopPosts.class);
                            startActivity(toTopRank);
                        }else if(index == 2){
                            popupInfo.show();
                        }
                    }
                });
        //buat popup
        infopopup();
        inipopup();
        Profilepopup();
        profile_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                profileName.setText(currentUser.getDisplayName());
                Glide.with(getApplicationContext()).load(currentUser.getPhotoUrl()).into(profile_image);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popProfileCurrentUser.show();
            }
        });
        postrecyclerView = findViewById(R.id.postRV);
        LinearLayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        postrecyclerView.setLayoutManager(layoutManager);
        postrecyclerView.setHasFixedSize(true);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Posts");
        }
    private void infopopup() {
        popupInfo = new Dialog(this);
        popupInfo.setContentView(R.layout.popup_info);
        popupInfo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupInfo.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popupInfo.getWindow().getAttributes().gravity = Gravity.START;
    }
    private void Profilepopup() {
        popProfileCurrentUser = new Dialog(this);
        popProfileCurrentUser.setContentView(R.layout.popup_profile);
        popProfileCurrentUser.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popProfileCurrentUser.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popProfileCurrentUser.getWindow().getAttributes().gravity = Gravity.TOP;
        popupProfile_image = popProfileCurrentUser.findViewById(R.id.profile_img);
        popupLogOut = popProfileCurrentUser.findViewById(R.id.LogOut);
        popupProfileName = popProfileCurrentUser.findViewById(R.id.profilename);
        ratingCount = popProfileCurrentUser.findViewById(R.id.ratingCount);
        postCount = popProfileCurrentUser.findViewById(R.id.postcount);
        popupUserEmail = popProfileCurrentUser.findViewById(R.id.mail);
        LinearLayout allCurrentUserPost = popProfileCurrentUser.findViewById(R.id.allcurrentUserPosts);
        allCurrentUserPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent postUser = new Intent(Home.this,UserPosts.class);
                startActivity(postUser);
            }
        });
        profile_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                popupProfileName.setText(currentUser.getDisplayName());
                popupUserEmail.setText(currentUser.getEmail());
                Glide.with(getApplicationContext()).load(currentUser.getPhotoUrl()).into(popupProfile_image);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        DatabaseReference countRatingPost = FirebaseDatabase.getInstance().getReference("UserPosts").child("RatingPosts").child(currentUser.getUid());
        countRatingPost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    float ratingSum = 0;
                    float ratingsTotal = 0;
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        ratingSum = ratingSum + Float.valueOf((child.getValue()).toString());
                        ratingsTotal++;
                    }
                    if (ratingsTotal != 0) {
                        double ratingsAvg = ratingSum/ratingsTotal;
                        ratingCount.setText(new DecimalFormat("##.##").format(ratingsAvg));
                    }else{
                        ratingCount.setText("0");
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
                    totalPost = (int) dataSnapshot.child(currentUser.getUid()).getChildrenCount();
                    postCount.setText(Integer.toString(totalPost));
                }
                else {
                    postCount.setText("0");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        popupLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent signin = new Intent(Home.this, LogIn.class);
                startActivity(signin);
                finish();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        //get list post from databaase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList = new ArrayList<>();
                for(DataSnapshot postSnap: dataSnapshot.getChildren()){
                    Post post = postSnap.getValue(Post.class);
                    postList.add(post);
                }
                //postAdapter isinya buat ambil data dari database
                postAdapter = new PostAdapter(Home.this,postList);
                postrecyclerView.setAdapter(postAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void setupPopupImageClick() {
        popupPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndRequestForPermission();
            }
        });
    }
    //ini permission buat akses gallery
    private void checkAndRequestForPermission() {
        if(ContextCompat.checkSelfPermission(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(Home.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(Home.this,"Harap terima untuk izin yang diperlukan",Toast.LENGTH_SHORT).show();
            }
            else
            {
                ActivityCompat.requestPermissions(Home.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PReCode);
            }
        }
        else
            openGallery();
    }
    private void openGallery(){
        //buat buka dan pilih gallery
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESTCODE);
    }
    //ketika mengambil gambar
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null){
            //buat masukin data poto ke database jadi url
            pickedImgUri = data.getData();
            popupPostImage.setImageURI(pickedImgUri);
            popupPostHint.setVisibility(View.INVISIBLE);
        }
    }
    private void inipopup() {
        popAddpost = new Dialog(this);
        popAddpost.setContentView(R.layout.popup_create_post);
        popAddpost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddpost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popAddpost.getWindow().getAttributes().gravity = Gravity.TOP;
        //popup widget
        popupUserImage = popAddpost.findViewById(R.id.profile_img);
        popupPostImage = popAddpost.findViewById(R.id.img_pic);
        popupTitle = popAddpost.findViewById(R.id.title);
        popupDecscription = popAddpost.findViewById(R.id.desc);
        popupAddBtn = popAddpost.findViewById(R.id.create);
        popupClickProgress = popAddpost.findViewById(R.id.progressBar);
        popupPostHint =popAddpost.findViewById(R.id.hint);
        //ambil poto profile user
        Glide.with(getApplication()).load(currentUser.getPhotoUrl()).into(popupUserImage);
        //button popup
        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupAddBtn.setVisibility(View.INVISIBLE);
                popupClickProgress.setVisibility(View.VISIBLE);
                //ini memeriksa field tidak ada yg kosong
                if (!popupTitle.getText().toString().isEmpty() && !popupDecscription.getText().toString().isEmpty() && pickedImgUri !=null){
                    //jika aman
                    // TODO Create Post Object and add it to firebase database
                    //bagian upload image ke firebase storage
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("blog_image");
                    final StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());
                    imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageDownloadLink = uri.toString();
                                    //membuat post object
                                    Post post = new Post(popupTitle.getText().toString(),popupDecscription.getText().toString(),
                                            imageDownloadLink,
                                            currentUser.getUid(),
                                            currentUser.getPhotoUrl().toString(),
                                            currentUser.getDisplayName(),currentUser.getEmail(),0);
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef = database.getReference("Posts").push();
                                    //buat dapetin id unik dan kunci post
                                    String key = myRef.getKey();
                                    post.setPostkey(key);
                                    //nah ini buat masukin semua datanya ke database
                                    myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            showMessage("Anda Baru Mengunggah Ciptaan Anda");
                                            popupClickProgress.setVisibility(View.INVISIBLE);
                                            popupAddBtn.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    DatabaseReference postUsers = FirebaseDatabase.getInstance()
                                            .getReference("UserPosts")
                                            .child(currentUser.getUid())
                                            .child(post.getPostkey());
                                    postUsers.setValue(post);
                                    popAddpost.dismiss();
                                    //perintah add post to firebase database
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //jika ada kesalahan upload poto
                                    showMessage(e.getMessage());
                                    popupClickProgress.setVisibility(View.INVISIBLE);
                                    popupAddBtn.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });
                }
                else {
                    showMessage("Silakan periksa apakah teks atau gambar ditambahkan.");
                    popupAddBtn.setVisibility(View.VISIBLE);
                    popupClickProgress.setVisibility(View.INVISIBLE);
                }
            }

        });
        setupPopupImageClick();
    }
    private void showMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();

    }
}
