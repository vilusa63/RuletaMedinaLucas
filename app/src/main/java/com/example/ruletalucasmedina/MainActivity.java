package com.example.ruletalucasmedina;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;



public class MainActivity extends AppCompatActivity {

    private String LOG = "edu.fje.dam2";
    private boolean isReproduint;
    private Intent intent;
    private static final String[] sectors = { "32 ROJO", "15 NEGRO",
            "19 ROJO", "4 NEGRO", "21 ROJO", "2 NEGRO", "25 ROJO", "17 NEGRO", "34 ROJO",
            "6 NEGRO", "27 ROJO","13 NEGRO", "36 ROJO", "11 NEGRO", "30 ROJO", "8 NEGRO",
            "23 ROJO", "10 NEGRO", "5 ROJO", "24 NEGRO", "16 ROJO", "33 NEGRO",
            "1 ROJO", "20 NEGRO", "14 ROJO", "31 NEGRO", "9 ROJO", "22 NEGRO",
            "18 ROJO", "29 NEGRO", "7 ROJO", "28 NEGRO", "12 ROJO", "35 NEGRO",
            "3 ROJO", "26 NEGRO", "CERO"
    };
    private static final Random RANDOM = new Random();
    private int grados = 0, gradosAnt = 0;
    private static final float HALF_SECTOR = 360f / 37f / 2f;

    public ImageView wheel;
    public TextView resultTv;
    public Button control;
    public TextView textViewUser;
    private TextView textViewNumero;
    public EditText editTextApuesta;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    String []  bar;
    public String numeroResultado;
    public String colorResultado;
    public String dineroApuesta;
    public int dinero;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent= new Intent(this, Audio.class);
        intent.putExtra("operacio", "inici");
        isReproduint = true;
        startService(intent);


        setContentView(R.layout.main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        wheel = (ImageView)findViewById(R.id.wheel);
        resultTv = (TextView)findViewById(R.id.resultTv);
        control = (Button)findViewById(R.id.control);
        textViewUser = (TextView)findViewById(R.id.textViewUser);
        textViewNumero = (TextView) findViewById(R.id.textViewNumero);
        editTextApuesta = (EditText) findViewById(R.id.editTextApuesta);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        agafarInfoUser();

    }

    public void controlMusica(View v){
        if (isReproduint) {
            isReproduint = false;
            control.setText("Play");
            intent.putExtra("operacio", "pausa");
            startService(intent);


        } else {
            isReproduint = true;
            control.setText("Pause");
            intent.putExtra("operacio", "inici");
            startService(intent);


        }
    }


    protected void onPause() {
        super.onPause();
        intent= new Intent(this, Audio.class);
        intent.putExtra("operacio", "pausa");
        startService(intent);
    }

    public void logout(View v){
        mAuth.signOut();
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }

    public void girar(View v){

        gradosAnt = grados % 360;
        // we calculate random angle for rotation of our wheel
        grados = RANDOM.nextInt(360) + 720;
        // rotation effect on the center of the wheel
        RotateAnimation rotateAnim = new RotateAnimation(gradosAnt, grados, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnim.setDuration(3600);
        rotateAnim.setFillAfter(true);
        rotateAnim.setInterpolator(new DecelerateInterpolator());
        rotateAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // we empty the result text view when the animation start
                resultTv.setText("");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // we display the correct sector pointed by the triangle at the end of the rotate animation
                resultTv.setText(getSector(360 - (grados % 360)));
                bar = getSector(360 - (grados % 360)).split(" ");
                //textViewNumero.setText(bar[1]);
                numeroResultado = bar[0];
                colorResultado = bar[1];
                calcularApuesta(numeroResultado, colorResultado);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // we start the animation
        wheel.startAnimation(rotateAnim);
    }

    private String getSector(int degrees) {
        int i = 0;
        String text = null;

        do {
            // start and end of each sector on the wheel
            float start = HALF_SECTOR * (i * 2 + 1);
            float end = HALF_SECTOR * (i * 2 + 3);

            if (degrees >= start && degrees < end) {
                // degrees is in [start;end[
                // so text is equals to sectors[i];
                text = sectors[i];
            }

            i++;
            // now we can test our Android Roulette Game :)
            // That's all !
            // In the second part, you will learn how to add some bets on the table to play to the Roulette Game :)
            // Subscribe and stay tuned !

        } while (text == null  &&  i < sectors.length);

        return text;
    }

    public void agafarInfoUser(){
        String id = mAuth.getCurrentUser().getUid();
        mDatabase.child("Usuarios").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String user = snapshot.child("email").getValue().toString();
                textViewUser.setText(user);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void calcularApuesta(String numero, String color){
        String hola = "";
        //textViewNumero.setText(color);
        dineroApuesta = editTextApuesta.getText().toString();
        dinero = Integer.parseInt(dineroApuesta);
        if(color.equals("ROJO")){
            dinero = dinero * 3;
        }
        if(color.equals("NEGRO")){
            dinero = dinero * 2;
        }
        hola = dinero+"";
        String id = mAuth.getCurrentUser().getUid();
        //String dineroPersona = mDatabase.child("Usuarios").child(id).child("Saldo").get().getResult().getValue().toString();

        mDatabase.child("Usuarios").child(id).child("Saldo").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());

                }
                else {
                    String dineroPersona = String.valueOf(task.getResult().getValue());
                    System.out.println(dineroPersona);
                    int dineroAcumulado = Integer.parseInt(dineroPersona);
                    dinero = dineroAcumulado + dinero;
                    mDatabase.child("Usuarios").child(id).child("Saldo").setValue(dinero);
                    String dineroString = dinero+"";
                    textViewNumero.setText(dineroString);
                }
            }
        });


    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}