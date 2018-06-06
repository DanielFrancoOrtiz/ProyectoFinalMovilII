package com.example.appmovil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class EmailActivity extends AppCompatActivity {

    private EditText mEmailField;
    private EditText mPasswordField;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        mEmailField = findViewById(R.id.txtEAEmail);
        mPasswordField = findViewById(R.id.txtEAPass);
        mAuth = FirebaseAuth.getInstance();




    }


    public void bntCrearClick(View view) {

        mAuth.createUserWithEmailAndPassword(mEmailField.getText().toString(), mPasswordField.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull  Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            new DaoUsuario().ConcetWhitFirebase(
                                    new Usuario(
                                            user.getDisplayName()==null?user.getEmail().split("@")[0]:user.getDisplayName(),
                                            "Nombre",
                                            user.getEmail(),"*****",
                                            FirebaseInstanceId.getInstance().getToken()));


                        } else {
                            Log.w("", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(EmailActivity.this,"Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void bntAEEntrarClick(View view) {

        mAuth.signInWithEmailAndPassword(mEmailField.getText().toString(), mPasswordField.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                                SharedPreferences settings = getSharedPreferences("user", MODE_PRIVATE);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("user",user.getDisplayName());
                                editor.commit();
                                FirebaseMessaging.getInstance().subscribeToTopic("news");

                                new DaoUsuario().ConcetWhitFirebase( new Usuario(
                                        user.getDisplayName()==null?user.getEmail().split("@")[0]:user.getDisplayName(),
                                        "Nombre",
                                        user.getEmail(),"*****",
                                        FirebaseInstanceId.getInstance().getToken()));
                                Intent i = new Intent(EmailActivity.this,Lista.class);
                                startActivity(i);
                            }else{
                                Log.w("", "signInWithEmail:failure", task.getException());

                            }
                    }
                });
    }

}
