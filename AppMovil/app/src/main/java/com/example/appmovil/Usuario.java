package com.example.appmovil;
public class Usuario {


    public String nombre;
    public String user;
    public String email;
    public String password;
    public String token;
    
    public Usuario(String user, String password, String token) {
        this.user=user;
        this.password=password;
        this.token=token;
    }
    public Usuario(String user, String token) {
        this.user=user;
        this.token=token;
    }

    public Usuario(String user,String nombre,String email,  String password, String token) {
        this.user=user;
        this.nombre = nombre;
        this.email=email;
        this.password=password;
        this.token=token;
    }


}