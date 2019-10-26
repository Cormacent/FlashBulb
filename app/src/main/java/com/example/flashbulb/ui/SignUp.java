package com.example.flashbulb.ui;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashbulb.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
public class SignUp extends AppCompatActivity {
    EditText edtPassword, edtNick, edtEmail;
    TextView lnkLogin;
    ImageView profileImg;
    Button btnSignUp;
    private static int PReCode = 1;
    private static int REQUESTCODE = 1;
    private ProgressBar loadingProgres;
    private FirebaseAuth mAuth;
    Uri pickedImgUri ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        profileImg = findViewById(R.id.profile_img);
        edtPassword = findViewById(R.id.edtPassword);
        edtNick = findViewById(R.id.edtNick);
        edtEmail = findViewById(R.id.edtId);
        btnSignUp = findViewById(R.id.btnSignUp);
        loadingProgres = findViewById(R.id.progressBar);
        loadingProgres.setVisibility(View.INVISIBLE);
        lnkLogin = findViewById(R.id.lnkLogin);
        lnkLogin.setMovementMethod(LinkMovementMethod.getInstance());
        //firebase
        mAuth = FirebaseAuth.getInstance();
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >=22){
                    checkAndRequestForPermission();
                }
                else
                {
                    openGallery();
                }
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSignUp.setVisibility(View.INVISIBLE);
                loadingProgres.setVisibility(View.VISIBLE);
                final String email = edtEmail.getText().toString();
                final String nick = edtNick.getText().toString();
                final String password = edtPassword.getText().toString();
                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(nick) || TextUtils.isEmpty(password) ){
                    //ada yg salah, buat show message
                    showMessage("Silahkan Isi seluruh data!");
                    btnSignUp.setVisibility(View.VISIBLE);
                    loadingProgres.setVisibility(View.INVISIBLE);
                }
                else if(pickedImgUri != null && !pickedImgUri.equals(Uri.EMPTY)) {
                    //kalo udh bener
                    CreateUserAccount(email,nick,password);
                }
                else if(password.length() < 6){
                    Toast.makeText(SignUp.this,"Harus memiliki 6 karakter atau lebih.",Toast.LENGTH_SHORT).show();
                }
                else{
                    showMessage("Gambar Tidak Dapat Kosong");
                    btnSignUp.setVisibility(View.VISIBLE);
                    loadingProgres.setVisibility(View.INVISIBLE);
                }
            }
        });
        lnkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, LogIn.class);
                startActivity(intent);
            }
        });
    }
    private void CreateUserAccount(String email, final String nick, String password) {
        //buat akun disini
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //buat user sukses
                            showMessage("Akun anda telah dibuat");
                            //pas udh kelar buat akun, ini buat update nama, poto dll
                            updateUserInfo(nick,pickedImgUri,mAuth.getCurrentUser());
                        }
                        else
                        {
                            showMessage("Gagal!!"+task.getException().getMessage());
                            btnSignUp.setVisibility(View.VISIBLE);
                            loadingProgres.setVisibility(View.INVISIBLE);
                        }

                    }
                });
    }
    //update data user
    private void updateUserInfo(final String nick,Uri pickedImgUri, final FirebaseUser currentUser) {
        //buat push poto ke storage
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("user_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //poto kelar d upload
                //disini ambil url poto
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //url yg memiliki user image url
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(nick)
                                .setPhotoUri(uri)
                                .build();
                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //user info berhasil ke update
                                            showMessage("Daftar Selesai");
                                            updateUI();
                                        }
                                    }
                                });
                    }
                });
            }
        });
    }
    private void updateUI() {
        startActivity(new Intent(SignUp.this, IntroGuide.class));
        finish();
    }
    //buat toast kalimat periksa
    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }
    private void openGallery(){
        //buat buka dan pilih gallery
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESTCODE);
    }
    private void checkAndRequestForPermission() {
        if(ContextCompat.checkSelfPermission(SignUp.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(SignUp.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(SignUp.this,"Harap terima untuk izin yang diperlukan",Toast.LENGTH_SHORT).show();
            }
            else
            {
                ActivityCompat.requestPermissions(SignUp.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PReCode);
            }
        }
        else
            openGallery();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null){
            pickedImgUri = data.getData();
            profileImg.setImageURI(pickedImgUri);
        }
    }
}