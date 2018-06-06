package com.example.appmovil;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DaoUsuario {
    public boolean  ConcetWhitFirebase(Usuario u){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        myRef.child(u.user).setValue(u);
        return true;
    }
}
