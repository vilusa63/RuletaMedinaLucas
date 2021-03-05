package com.example.ruletalucasmedina;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthMultiFactorException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.MultiFactorResolver;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class PortadaActivity extends AppCompatActivity {



    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private EditText editTextUsuario;
    private EditText editTextContrasena;
    private Button btnRegister;
    private TextView textViewLogin;

    private String usuario = "";
    private String contrasena = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.portada);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        editTextUsuario = (EditText) findViewById(R.id.editTextUsuario);
        editTextContrasena = (EditText) findViewById(R.id.editTextContraseña);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        textViewLogin = (TextView) findViewById(R.id.textViewLogin);
    }





    public void registro(View v){
        usuario = editTextUsuario.getText().toString();
        contrasena = editTextContrasena.getText().toString();

        if(TextUtils.isEmpty(usuario)){
            editTextUsuario.setError("Usuario necesario");
            return;
        }
        if(TextUtils.isEmpty(contrasena)){
            editTextContrasena.setError("Contraseña necesaria");
            return;
        }
        if(contrasena.length() < 6){
            editTextContrasena.setError("Contraseña mínimo de 6 carácteres");
            return;
        }

            registrarUsuario();

    }

    public void registrarUsuario(){

        mAuth.createUserWithEmailAndPassword(usuario, contrasena).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("email", usuario);
                    map.put("contrasena", contrasena);

                    String id = mAuth.getCurrentUser().getUid();

                    mDatabase.child("Usuarios").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful()) {
                                Intent intent = new Intent(PortadaActivity.this, MainActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(PortadaActivity.this, "Error failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


                    //updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(PortadaActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    //updateUI(null);
                };
            };
            });
    }

    public void login(View v){
        Intent intentLogin = new Intent(PortadaActivity.this, LoginActivity.class);
        startActivity(intentLogin);
    }



    public void jugar(View v){
        Intent intent = new Intent(PortadaActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void goLogin(View v){
        Intent intent = new Intent(PortadaActivity.this, LoginActivity.class);
        startActivity(intent);
    }


}
