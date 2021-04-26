package it.pmsc.silviosenese.autenticazionenew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    final private String TAG = "Register Activity";
    EditText nome;
    EditText email;
    EditText password;
    EditText confermaPassword;
    Button invia;
    Button main;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //2)Inizializzo l'istanza di FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        //Inizializzo UI
        initUI();

        Intent fromMainToRegister = getIntent();
        Log.i(TAG, ""+fromMainToRegister.getStringExtra("id").toString());


    }

    private void initUI(){
        //Inizializzo UI: assegno agli oggetti grafici degli oggetti java
        nome = (EditText)findViewById(R.id.et_nome);
        email = (EditText) findViewById(R.id.et_mail);
        password = (EditText) findViewById(R.id.et_password);
        confermaPassword = (EditText) findViewById(R.id.et_conferma);
        invia = findViewById(R.id.bt_invia);
        main = findViewById(R.id.bt_main);

    }

    public void registrati (View view) {
        Log.i(TAG, "Cliccato sul pulsante Invia");
        //Intent fromRegisterToLibrary = new Intent(this, MyLibrary.class);
        //startActivity(fromRegisterToLibrary);
        String nomeUtente = nome.getText().toString();
        String emailUtente = email.getText().toString();
        String passwordUtente = password.getText().toString();
        String confermaPasswordUtente = confermaPassword.getText().toString();

        Log.i(TAG, "Info inserite: "+nomeUtente+ " " +emailUtente+ " " +passwordUtente+ " "+confermaPasswordUtente);
        //5)invoco il metodo createFirebaseUser precedentemente creato
        //TODO: controlli su nome, mail e confronto password (posso usare le regex??)

        //if(controlli(nomeUtente, emailUtente, passwordUtente, confermaPasswordUtente))
        if ((controlloNome(nomeUtente)==true) && (controlloMail(emailUtente)==true) && (controlloPasswordUguali(passwordUtente, confermaPasswordUtente)==true) && (controlloPassword(passwordUtente)==true))
            createFirebaseUser(emailUtente, passwordUtente, nomeUtente);
        else
            Log.i(TAG, "Utente non creato");
    }



    private boolean controlloNome(String nomeUtente) {
        //Username > 3 characters
        if (nomeUtente.length()>3){
            Log.i(TAG, "Controllo nome superato");
            return true;
        } else{
            Log.i(TAG, "Controllo nome NON superato: username deve avere almeno tre caratteri");
        return false;
        }
    }
    private boolean controlloMail(String email){
        //E-mail correct
        if(email.contains("@") && email.contains(".")) {
            Log.i(TAG, "Controllo mail superato");
           return true;
        }else{
            Log.i(TAG, "Controllo mail NON superato: email deve avere almeno una '@' e un '.'");
            return false;
        }
    }

    private boolean controlloPassword(String passwordUtente) {
        //Password > 5 characters and both
        if(passwordUtente.length()>5){
            Log.i(TAG, "Controllo lunghezza password superato");
            return true;
        }else{
            Log.i(TAG, "Controllo lunghezza password NON superato: pwd deve essere almeno lunga 5 caratteri");
            return false;
        }
    }

    private boolean controlloPasswordUguali(String pwd1, String pwd2){
        //passwords equal
        if(pwd1.compareTo(pwd2)==0){
            Log.i(TAG, "Controllo password uguali superato");
            return true;
        }else{
            Log.i(TAG, "Controllo password uguali NON superato: le password devono essere identiche (case sensitive)");
            return false;
        }
    }

    public void login (View view) {
        Log.d(TAG, "Cliccato sulla scritta Login");

        //Utente u = new Utente(nome.getText().toString());
        Intent fromRegisterToLogin = new Intent(RegisterActivity.this, LoginActivity.class);
        fromRegisterToLogin.putExtra("id", "fromRegisterToLogin");
        //fromRegisterToLogin.putExtra("utente", (Parcelable) u);
        Log.d(TAG, ""+nome.getText().toString());
        Log.d(TAG, ""+email.getText().toString());
        Log.d(TAG, ""+password.getText().toString());
        fromRegisterToLogin.putExtra("nome", nome.getText().toString());
        fromRegisterToLogin.putExtra("email", email.getText().toString());
        fromRegisterToLogin.putExtra("password", password.getText().toString());
        startActivity(fromRegisterToLogin);
    }

    public void torna (View view) {
        Log.d(TAG, "Cliccato sul pulsante Torna");
        Intent fromRegisterToHome = new Intent(RegisterActivity.this, MainActivity.class);
        fromRegisterToHome.putExtra("id", "fromRegisterToHome");
        startActivity(fromRegisterToHome);
    }

    //3) Override del metodo onStart per controllare se l'utente è già loggato
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //updateUI();
        }
    }

    private void updateUI() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
    }

    //4)creo il metodo per la registrazione di un utente su firebase
    private void createFirebaseUser(String email, String password, String nomeUtente) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            setNome(user, nomeUtente);
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI();
                        }
                    }

                    private void setNome(FirebaseUser user, String nomeUser) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(nomeUser.toString())
                                //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "User profile updated.");
                                        }
                                    }
                                });
                    }
                });
    }
}