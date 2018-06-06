package com.example.appmovil;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class NotificationDAO {
    Context context;

    public NotificationDAO(Context context) {
        this.context=context;
    }

    public boolean  insert$update(Notification notification){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user",Context.MODE_PRIVATE);
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Notifications_"+sharedPreferences.getString("user",""));
            myRef.child(notification.getTitle()).setValue(notification);
            Log.e("NOTIF","value:"+notification.getMessage()+" "+notification.getTitle());
        }catch (Exception ex){
            Log.e("ERROR","value:"+ex+" "+notification.getMessage()+" "+notification.getTitle());
        }
        return true;
    }
}
