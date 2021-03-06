package com.example.ruletalucasmedina;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private EditText editTextUsuario;
    private EditText editTextContrasena;
    private Button btnLogin;

    private String usuario = "";
    private String contrasena = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        editTextUsuario = (EditText) findViewById(R.id.editTextUsuario);
        editTextContrasena = (EditText) findViewById(R.id.editTextContraseña);
        btnLogin = (Button) findViewById(R.id.btnLogin);

    }

    public void goRegister(View v){
        Intent intent = new Intent(LoginActivity.this, PortadaActivity.class);
        startActivity(intent);
    }

    public void loginUsuari(View v){
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

        mAuth.signInWithEmailAndPassword(usuario, contrasena).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login correcto",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));

                    //updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    //updateUI(null);
                };
            };



            });
    }

}