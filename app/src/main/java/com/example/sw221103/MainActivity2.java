package com.example.sw221103;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth firebaseAuth = null;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private EditText editTextEmail;
    private EditText newPasswordEditText;
    private Button buttonLogIn;
    private Button buttonSignUp;
    private Button buttongoogle;
    private TextView textviewFindPassword;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private SignInButton signInButton;
    //private FirebaseAuth mAuth = null;
    private TextView mStatusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.edittext_email);
        newPasswordEditText = findViewById(R.id.new_password_edittext);

        textviewFindPassword = (TextView) findViewById(R.id.textViewFindpassword);
        textviewFindPassword.setOnClickListener(this);

        signInButton = findViewById(R.id.signInButton);
        //mAuth = FirebaseAuth.getInstance();

        /*
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

         */

        buttonSignUp = (Button) findViewById(R.id.btn_join);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SignUpActivity 연결
                Intent intent = new Intent(MainActivity2.this, JoinActivity.class);
                startActivity(intent);
            }
        });

        /*
        buttongoogle = (Button) findViewById(R.id.btn_google);
        buttongoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SignUpActivity 연결
                Intent intent = new Intent(MainActivity.this, GoogleActivity.class);
                startActivity(intent);
            }
        });


         */
        buttonLogIn = (Button) findViewById(R.id.btn_pass);
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!newPasswordEditText.getText().toString().equals("")) {
                    loginUser(editTextEmail.getText().toString(), newPasswordEditText.getText().toString());
                } else {
                    Toast.makeText(MainActivity2.this, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
                }
            }
        });

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(MainActivity2.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                }
            }
        };
    }
/*
    public void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공
                            Toast.makeText(MainActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            firebaseAuth.addAuthStateListener(firebaseAuthListener);
                        } else {
                            // 로그인 실패
                            Toast.makeText(MainActivity.this, "아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

     */

    private void loginUser(String email, String password) {

        firebaseAuth.signInWithEmailAndPassword(email , password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){ // 계정이 등록이 되어 있으면
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if(user.isEmailVerified()){ // 그리고 그때 그 계정이 실제로 존재하는 계정인지
                        EditText newPasswordEditText = findViewById(R.id.new_password_edittext);
                        String newPassword = newPasswordEditText.getText().toString();
                        user.updatePassword(newPassword)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "비밀번호가 업데이트되었습니다.");

                                            // Firebase Realtime Database를 사용하여 사용자 정보를 업데이트합니다.
                                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                                            String userId = user.getUid();
                                            usersRef.child(userId).child("password").setValue(newPassword);
                                            Toast.makeText(MainActivity2.this,
                                                    "비밀번호 업데이트 성공. " , Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.e(TAG, "비밀번호 업데이트 중 오류가 발생했습니다.", task.getException());
                                            Toast.makeText(MainActivity2.this,
                                                    "비밀번호 업데이트 중 오류가 발생했습니다. " , Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        Log.d("login", "signInWithEmail:success" + user.getEmail());
                        Toast.makeText(MainActivity2.this, "signInWithEmail:success." + user.getEmail(), Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(MainActivity2.this , HomeActivity.class);
                        startActivity(i);
                        finish();
                    }else{
                        Toast.makeText(MainActivity2.this, "인증이 되지 않은 이메일입니다 해당 이메일 주소에서 링크를 클릭해주세요",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }else{
                    Log.d("login", "signInWithEmail:failure", task.getException());
                    Toast.makeText(MainActivity2.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }




    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }

    public void onClick(View view) {
        if(view == textviewFindPassword) {
            finish();
            startActivity(new Intent(this, FindActivity.class));
        }
    }

    /*

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Snackbar.make(findViewById(R.id.layout_main), "Authentication Successed.", Snackbar.LENGTH_SHORT).show();
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar.make(findViewById(R.id.layout_main), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) { //update ui code here
        if (user != null) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

     */


}