package com.example.appmovil;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView txtName;
    private TextView txtPass;
    private Usuario usr;
    private ArrayList<Usuario> userList;

    /*Declaracion de variables para la autenticacion con Google*/
    private SignInButton msiSignInButton;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN=2;
    private GoogleApiClient miGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InicioFirebasseService();
        InicioFirebaseGoogleAutentication();
    }
    public void InicioFirebasseService(){
        Intent service = new Intent(getApplicationContext(),  MyFirebaseMessagingService.class);
        this.startService(service);
        Intent service1 = new Intent(this,  MyFirebaseInstanceIDService.class);
        this.startService(service1);
        Intent service2 = new Intent(this,  MyJobService.class);
        this.startService(service2);
        txtName = findViewById(R.id.txtName);
        txtPass = findViewById(R.id.txtPass);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_HIGH));
        }


        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");

        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList =new ArrayList<>();
                for(DataSnapshot childSnapShot : dataSnapshot.getChildren()) {
                    String user= (String) childSnapShot.child("user").getValue();
                    String name= (String) childSnapShot.child("nombre").getValue();
                    String email= (String) childSnapShot.child("email").getValue();
                    String password = (String) childSnapShot.child("password").getValue();
                    String token = (String) childSnapShot.child("token").getValue();
                    userList.add(new Usuario(user,name,email,password,token));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "DatabaseError", databaseError.toException());
            }
        });

    }
    public void InicioFirebaseGoogleAutentication(){
        msiSignInButton = findViewById(R.id.sign_in_button);
        mAuth = FirebaseAuth.getInstance();
        msiSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(miGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        miGoogleApiClient =  new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(MainActivity.this,"Sowting went wrong",Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
    }
    public void bntEntrar_Click(View view){
        String name = String.valueOf(txtName.getText());
        String pass = String.valueOf(txtPass.getText());
        try
        {
            if ( name.length()<2 || pass.length()<2)
            {
                Toast.makeText(MainActivity.this,"Nombre o Contraseña no validos", Toast.LENGTH_LONG).show();
            }
            else
            {
                if(!findUser(new Usuario(name,pass,FirebaseInstanceId.getInstance().getToken()))){
                    Toast.makeText(MainActivity.this,"Nombre o contraseña no validos", Toast.LENGTH_LONG).show();
                }else{
                    SharedPreferences settings = getSharedPreferences("user", MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("user",name );
                    editor.commit();
                    FirebaseMessaging.getInstance().subscribeToTopic("news");

                    new DaoUsuario().ConcetWhitFirebase(usr);
                    Intent i = new Intent(MainActivity.this,Lista.class);
                    startActivity(i);
                }
            }
        }catch(Exception e)
        {
            Toast.makeText(MainActivity.this,e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public void bntRegistrar_Click(View view){
        Intent i = new Intent(MainActivity.this,RegistroUsuario.class);
        startActivity(i);
    }
    public boolean findUser(final Usuario u){
        for (Usuario user: userList){
            if(userList.size()>0&&user.user.equals(u.user)&&user.password.equals(u.password)){

            this.usr = user;
            return true;
            }
        }
        return false;
    }
    public boolean findUserByUser(final Usuario u){
        for (Usuario user: userList){
            if(userList.size()>0&&user.user.equals(u.user)){
                this.usr = user;
                return true;
            }
        }
        return false;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }else{
                Toast.makeText(MainActivity.this,"Error",Toast.LENGTH_LONG).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(!findUserByUser(new Usuario(user.getDisplayName(),FirebaseInstanceId.getInstance().getToken()))){
                                new DaoUsuario().ConcetWhitFirebase(
                                        new Usuario(user.getDisplayName(),
                                                user.getDisplayName(),
                                                user.getEmail(),"*****",
                                                FirebaseInstanceId.getInstance().getToken()));
                            }else{
                                SharedPreferences settings = getSharedPreferences("user", MODE_PRIVATE);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("user",user.getDisplayName());
                                editor.commit();
                                FirebaseMessaging.getInstance().subscribeToTopic("news");

                                new DaoUsuario().ConcetWhitFirebase(usr);
                                Intent i = new Intent(MainActivity.this,Lista.class);
                                startActivity(i);
                            }


                        } else {
                            Log.w("firebaseAuth", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });

    }

    public void email_buttonClick(View view) {
        Intent i = new Intent(MainActivity.this,EmailActivity.class);
        startActivity(i);
    }
}
