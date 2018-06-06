package com.example.appmovil;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class RegistroUsuario extends AppCompatActivity {
    EditText txtAgregaNombre;
    EditText txtAgregaUsuario;
    EditText txtAgregaEmail;
    EditText txtAgregaPass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist_user);
        txtAgregaUsuario = findViewById(R.id.txtAddUser);
        txtAgregaNombre = findViewById(R.id.txtAddName);
        txtAgregaEmail = findViewById(R.id.txtAddEmail);
        txtAgregaPass = findViewById(R.id.txtAddPass);
    }

    public void bntAceptar_Click(View view){
        if ( txtAgregaNombre.getText().toString().length()<2 || txtAgregaPass.getText().toString().length()<2)
        {
            Toast.makeText(this,"Nombre o Contraseña no validos", Toast.LENGTH_LONG).show();
        }else{
            new DaoUsuario().ConcetWhitFirebase(
                    new Usuario(txtAgregaUsuario.getText().toString(),
                            txtAgregaNombre.getText().toString(),
                            txtAgregaEmail.getText().toString(),txtAgregaPass.getText().toString(),
                            FirebaseInstanceId.getInstance().getToken()));
            Toast.makeText(this,"Usuario Registrado",Toast.LENGTH_LONG).show();

            this.finish();
        }
    }
    public void bntCancel_Click(View view){
       this.finish();
    }

}

