package com.example.ruletalucasmedina;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthMultiFactorException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.MultiFactorResolver;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.Collections;
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
    private GridView gridView;
    private ArrayList<String> list;
    private ArrayList<User> userList=new ArrayList<User>();
    private ArrayAdapter<String> adapter;
    int posicion;
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
    public void goRanking(View view) {
        setContentView(R.layout.ranking);
        ConstraintLayout v=(ConstraintLayout) findViewById(R.id.rankingRelative);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu);
        toolbar.setTitle("Ranking");
        writeRankingTable();
    }
    public void writeRankingTable(){
        gridView=(GridView) findViewById(R.id.gridView1);
        //ArrayList
        list=new ArrayList<String>();
        adapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,list);
        posicion=1;
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        Query artpicsRef = rootRef.child("Usuarios").orderByChild("Saldo");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String email = ds.child("email").getValue(String.class);
                    String contasena = ds.child("contrasena").getValue(String.class);
                    Long saldos = ds.child("Saldo").getValue(Long.class);
                    User u=new User(email,contasena,saldos);
                    userList.add(u);
                    order();

                    Log.d("TAG", email + " / " + contasena+"/"+saldos);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        artpicsRef.addListenerForSingleValueEvent(eventListener);
        //Toast.makeText(getBaseContext(),"Name: "+name+"Roll No: "+roll+"Course: "+course , Toast.LENGTH_LONG).show();
    }
    public void order(){
        list.clear();
        posicion=1;
        userList.sort((o1, o2)
                -> o1.money.compareTo(o2.money));
        Collections.reverse(userList);

        for(int i=0;i<userList.size();i++) {
            list.add(Integer.toString(posicion));
            list.add(userList.get(i).username);
            list.add(Long.toString(userList.get(i).money));
            posicion++;
        }
        gridView.setAdapter(adapter);
    }
}
