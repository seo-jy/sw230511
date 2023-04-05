package com.example.sw221103;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Emailcheck extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    //private DatabaseReference mDatabaseReference;
    private EditText editTextEmail;
    private Button buttonCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_check);

        firebaseAuth = FirebaseAuth.getInstance();
        ///mDatabaseReference = FirebaseDatabase.getInstance().getReference("FirebaseEmailAccount");

        editTextEmail = (EditText) findViewById(R.id.edittext_email);

        buttonCheck = (Button) findViewById(R.id.btn_check);
        buttonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailVerification();
            }
        });
    }


    private void sendEmailVerification() {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(Emailcheck.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // 이메일 인증 메일 전송 성공
                            Toast.makeText(getApplicationContext(), "이메일 인증 메일을 보냈습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            // 이메일 인증 메일 전송 실패
                            Toast.makeText(getApplicationContext(), "이메일 인증 메일을 보내기 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}


