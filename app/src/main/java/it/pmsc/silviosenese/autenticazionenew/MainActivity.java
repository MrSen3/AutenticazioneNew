package it.pmsc.silviosenese.autenticazionenew;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {

    final static private String TAG = "Main Activity";
    Button login;
    Button registrazione;
    Button logout;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        Intent fromRegisterToMain = getIntent();
        Log.i(TAG, ""+fromRegisterToMain.getStringExtra("id"));

        //2)Inizializzo l'istanza di FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
    }

    private void initUI() {
        login = findViewById(R.id.button_login);
        registrazione = findViewById(R.id.button_registrazione);
        logout =findViewById(R.id.bt_logout);
    }

    public void goToLoginActivity(View view){
        Log.d(TAG, "Cliccato sul pulsante Login");
        Intent fromMainToLogin = new Intent(MainActivity.this, LoginActivity.class);
        fromMainToLogin.putExtra("id", "fromMainToLogin");
        startActivity(fromMainToLogin);
    }

    public void goToRegisterActivity(View view){
        Log.d(TAG, "Cliccato sul pulsante Registrazione");
        Intent fromMainToRegister = new Intent(MainActivity.this, RegisterActivity.class);
        fromMainToRegister.putExtra("id", "fromMainToRegister");
        startActivity(fromMainToRegister);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //updateUI(currentUser);
            Log.i(TAG, currentUser.getDisplayName());
            this.setTitle(currentUser.getDisplayName());
            logout.setVisibility(VISIBLE);
            login.setVisibility(INVISIBLE);
        }
        else{
            this.setTitle("");
            logout.setVisibility(INVISIBLE);
            login.setVisibility(VISIBLE);
        }
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();

        //Per ricaricare main e far sparire il bottone logout
        onStart();
    }
}