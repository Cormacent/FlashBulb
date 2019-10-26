package com.example.flashbulb.ui;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.text.method.LinkMovementMethod;

import com.example.flashbulb.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
public class LogIn extends AppCompatActivity {
    EditText edtId, edtPassword;
    TextView lnkRegister,lnkForgot;
    Button btnSignIn;
    //popup
    Button popupForgotBtn;
    TextView popupEmailForgot;
    Dialog popupForgotDialog;
    private ProgressBar loginProgres;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        //Views
        edtId = findViewById(R.id.edtId);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignIn = findViewById(R.id.btnSignin);
        loginProgres = findViewById(R.id.progressBar);
        lnkRegister = findViewById(R.id.lnkRegister);
        lnkForgot = findViewById(R.id.forgotpass);
        lnkRegister.setMovementMethod(LinkMovementMethod.getInstance());
        loginProgres.setVisibility(View.INVISIBLE);
        lnkRegister.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();
        popupForgotPass();
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProgres.setVisibility(View.VISIBLE);
                btnSignIn.setVisibility(View.INVISIBLE);

                final String email = edtId.getText().toString();
                final String password = edtPassword.getText().toString();

                if(email.isEmpty() || password.isEmpty()){
                    showMessage("Silakan Isi Teks Kosong");
                    btnSignIn.setVisibility(View.VISIBLE);
                    loginProgres.setVisibility(View.INVISIBLE);
                }
                else
                {
                    Login(email,password);
                }
            }
        });
        lnkRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogIn.this, SignUp.class);
                startActivity(intent);
            }
        });
        lnkForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupForgotDialog.show();
            }
        });
    }
    private void popupForgotPass() {
        popupForgotDialog = new Dialog(this);
        popupForgotDialog.setContentView(R.layout.popup_forgotpass);
        popupForgotDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupForgotDialog.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popupForgotDialog.getWindow().getAttributes().gravity = Gravity.TOP;
        popupForgotBtn = popupForgotDialog.findViewById(R.id.forgotBtn);
        popupEmailForgot = popupForgotDialog.findViewById(R.id.edtId);
        popupForgotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userEmail = popupEmailForgot.getText().toString();
                if(TextUtils.isEmpty(userEmail)){
                    Toast.makeText(LogIn.this,"Silakan masukkan alamat email Anda terlebih dahulu!",Toast.LENGTH_SHORT).show();
                }else {
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(LogIn.this,"Silakan periksa email Anda",Toast.LENGTH_LONG).show();
                                popupForgotDialog.dismiss();
                            }else {
                                String message = task.getException().getMessage();
                                Toast.makeText(LogIn.this,"Terjadi kesalahan:"+message,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    private void Login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    loginProgres.setVisibility(View.INVISIBLE);
                    btnSignIn.setVisibility(View.VISIBLE);
                    updateUI();
                }
                else
                {
                    showMessage(task.getException().getMessage());
                    btnSignIn.setVisibility(View.VISIBLE);
                    loginProgres.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
    private void updateUI() {
        Intent intent = new Intent(LogIn.this, Home.class);
        startActivity(intent);
        finish();
    }
    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            updateUI();
        }
    }
}
