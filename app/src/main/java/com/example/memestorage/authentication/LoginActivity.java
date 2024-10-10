package com.example.memestorage.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.memestorage.models.UserModel;
import com.example.memestorage.utils.FirebaseHelper;
import com.example.memestorage.activities.MainActivity;
import com.example.memestorage.R;
import com.example.memestorage.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        mAuth = FirebaseHelper.getInstance().getAuth();
        db = FirebaseHelper.getInstance().getDb();
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initUI();
    }

    private void initUI() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  // Get the web client ID from google-services.json
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.fabLoginBack.setOnClickListener(v -> {
            onBackPressed();
        });


        binding.btLogin.setOnClickListener(v -> {
            String password = binding.etPassword.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            Toast.makeText(LoginActivity.this, "click bt login", Toast.LENGTH_SHORT).show();
            if (password.length() < 8) {
                Toast.makeText(this, "Mật khẩu chưa đủ 8 kí tự", Toast.LENGTH_SHORT).show();
            } else {
                signInWithEmailAndPassword(email, password);
            }
        });
        binding.tvLoginForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, PasswordResetActivity.class);
            startActivity(intent);
        });
        binding.ibRegisterGoogle.setOnClickListener(v -> {
            signInWithGoogleAccount();
        });
    }

    private void signInWithEmailAndPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Wrong password or email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void signInWithGoogleAccount() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign-In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign-In failed
                Log.w("SignIn", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("SignIn", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("SignIn", "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        checkIfUserExistsAndCreate(user);
                    } else {
                        // If sign-in fails, display a message to the user
                        Log.w("SignIn", "signInWithCredential:failure", task.getException());
                    }
                });
    }

    private void checkIfUserExistsAndCreate(FirebaseUser firebaseUser) {
        String uid = firebaseUser.getUid();
        db.collection("users").document(uid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().exists()) {
                            // User does not exist, create the user
                            createUser(firebaseUser);

                        } else {

                            // User already exists, you can load their data or redirect as necessary
                            Log.d("User Registration", "User already exists.");
                        }
                        Toast.makeText(LoginActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    } else {
                        Log.e("User Registration", "Error getting user", task.getException());
                    }
                });
    }


    public void createUser(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            UserModel user = new UserModel();
            user.uId = uid;
            user.userName = firebaseUser.getEmail();
            user.userEmail = firebaseUser.getEmail();
            user.userFollowers = 0;
            user.userAge = 20;
            user.userGender = "male";


            db.collection("users").document(uid).set(user)
                    .addOnSuccessListener(aVoid -> {
                        // User data successfully written!
                        //db.collection("users").document(uid).collection("images");
                        Log.d("Create user after register", "successfully !");
                    })
                    .addOnFailureListener(e -> {
                        // Failed to write user data
                        Log.w("Create user after register", "Error", e);
                    });

        }
    }

}