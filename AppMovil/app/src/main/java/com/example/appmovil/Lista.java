package com.example.appmovil;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Lista extends AppCompatActivity {
    ListView lstNotif;
    ArrayList<Notification>notifList = new ArrayList<>();
    public static final String TAG ="ListActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_notifications);
        lstNotif = findViewById(R.id.lstNotif);
        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("user",MODE_PRIVATE);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Notifications_"+sharedPreferences.getString("user",""));

        myRef.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                notifList=new ArrayList<>();
                for(DataSnapshot childSnapShot : dataSnapshot.getChildren()) {

                    String title = (String) childSnapShot.child("title").getValue();
                    String msg = (String) childSnapShot.child("message").getValue();

                    notifList.add(new Notification(title,msg));


                }
                NotificationsAdapter adp = new NotificationsAdapter(Lista.this,notifList);
                lstNotif.setAdapter(adp);
                String value="";
                try {
                     value= dataSnapshot.getValue().toString();
                }catch (Exception ex){
                    Log.e("Error",ex.toString());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
                Toast.makeText(getBaseContext(),"Error: " + databaseError.toException(),Toast.LENGTH_LONG).show();
            }
        });

    }
}
