package com.bpal.mychats;

import static com.bpal.mychats.Utils.Const.USERNAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.bpal.mychats.SubActivity.SecondActivity;
import com.bpal.mychats.Models.User;
import com.bpal.mychats.Services.Token;
import com.bpal.mychats.Utils.Const;
import com.bpal.mychats.Utils.ConstFire;
import com.bpal.mychats.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    DatabaseReference reference;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reference = Const.rootRef.child(ConstFire.USER);
        progressDialog = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();

        Paper.init(getApplicationContext());

        binding.btn.setOnClickListener(view -> {
            String Name = binding.name.getText().toString();

            if (Name.isEmpty()) {
                binding.name.setError("Name is required!");
                binding.name.requestFocus();
                return;
            }

            login(Name);

        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        String user = Paper.book().read(USERNAME);
        if (user != null) {
            login(user);
        }
    }

    private void login(String Name) {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(Name).exists()){
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setMessage("Logging In...");
                    progressDialog.show();
                    User user = snapshot.child(Name).getValue(User.class);
                    if (user != null && Name.equals(user.getName())) {
                        progressDialog.dismiss();
                        Const.currentUser = user;
                        Paper.book().write(USERNAME, Name);
                        Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                        startActivity(intent);
                        finish();
                        Const.showToast(getApplicationContext(), "Logged in successfully.", MainActivity.this);
                    }
                } else {
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setMessage("Registering & Logging In...");
                    progressDialog.show();
                    Const.rootAuth.signInAnonymously().addOnSuccessListener(authResult -> {
                        progressDialog.dismiss();
                        User user = new User(Name, null, authResult.getUser().getUid());
                        reference.child(Name).setValue(user);
                        Const.currentUser = user;
                        Paper.book().write(USERNAME, Name);
                        Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                        startActivity(intent);
                        finish();
                        Const.showToast(getApplicationContext(), "Logged in successfully.", MainActivity.this);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}