package it.pmsc.silviosenese.autenticazionenew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
final private String TAG = "Login Activity";
    EditText nome;
    EditText email;
    EditText password;
    Button invia;
    TextView registrazione;

    //1) Creazione di mAuth
    private FirebaseAuth mAuth;

    //Google
    GoogleSignInClient mGoogleSignInClient;

    //DAjeDajeDaje

    //Facebook
    LoginButton loginButton;
    CallbackManager mCallbackManager=CallbackManager.Factory.create();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Righe di codice per evitare errori di fb
        FacebookSdk.setApplicationId("1908257995998423");
        FacebookSdk.sdkInitialize(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //2) Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        initUI();

        Intent fromRegisterToLogin = getIntent();
        Log.i(TAG, ""+fromRegisterToLogin.getStringExtra("id"));
        //riempiCampi(fromRegisterToLogin);

    }

    private void initUI(){
        //Inizializzo UI
        nome = (EditText)findViewById(R.id.text_name);
        email = (EditText) findViewById(R.id.text_mail);
        password = (EditText) findViewById(R.id.text_pwd);
        invia = findViewById(R.id.bt_login);
        registrazione = findViewById(R.id.text_registra);

        //login con google

        //login con facebook
        // Initialize Facebook Login button
        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Login di Google


        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void riempiCampi(Intent fromRegisterToLogin) {
        Log.i(TAG, ""+fromRegisterToLogin.getStringExtra("nome"));
        Log.i(TAG, ""+fromRegisterToLogin.getStringExtra("email"));
        Log.i(TAG, ""+fromRegisterToLogin.getStringExtra("password"));
        nome.setText(""+fromRegisterToLogin.getStringExtra("nome").toString());
        email.setText(""+fromRegisterToLogin.getStringExtra("email").toString());
        password.setText(""+fromRegisterToLogin.getStringExtra("password").toString());

        /* con utente non sono riuscito
        Utente utente = i.getParcelableExtra("utente");
        nome.setText(utente.getNome());
        email.setText(utente.getEmail());
        password.setText(utente.getPassword());
        */

    }

    public void goToRegisterActivity(View view) {
        Log.d(TAG, "Cliccato sul pulsante Registrazione");
        Intent fromMainToRegister = new Intent(LoginActivity.this, RegisterActivity.class);
        fromMainToRegister.putExtra("id", "fromLoginToRegister");
        startActivity(fromMainToRegister);
    }
    public void login (View view){
        Log.i(TAG, "Cliccato sul pulsante Login");
        String emailUtente = email.getText().toString();
        String passwordUtente = password.getText().toString();
        signInWithFirebase(emailUtente, passwordUtente);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //updateUI(currentUser);
        }
    }

    private void signInWithFirebase(String email, String password){
    mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithEmail:success");
                FirebaseUser user = mAuth.getCurrentUser();
                updateUI(user);
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithEmail:failure", task.getException());
                Toast.makeText(LoginActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
                updateUI(null);
            }
        }
    });
    }

    private void updateUI(FirebaseUser user) {
        Log.i(TAG, "Connesso: "+user);

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

}