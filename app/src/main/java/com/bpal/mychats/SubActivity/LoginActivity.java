package com.bpal.mychats.SubActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bpal.mychats.MainActivity;
import com.bpal.mychats.Models.User;
import com.bpal.mychats.R;
import com.bpal.mychats.Utils.Const;
import com.bpal.mychats.Utils.ConstFire;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText name, pass;
    Button btnlogin;
    DatabaseReference reference;
    String Name, Password;
    ProgressDialog progressDialog;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        reference = Const.rootRef.child(ConstFire.USER);
        progressDialog = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();

        name = findViewById(R.id.name);
        pass = findViewById(R.id.pass);
        btnlogin = findViewById(R.id.btnlogin);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Name = name.getText().toString();
                Password = pass.getText().toString();

                if (Name.isEmpty()) {
                    name.setError("Name is required!");
                    name.requestFocus();
                    return;
                }

                if (Password.isEmpty()) {
                    pass.setError("Password is required!");
                    pass.requestFocus();
                    return;
                }

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(Name).exists()){
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.setMessage("Logging In...");
                            progressDialog.show();
                            User user = snapshot.child(Name).getValue(User.class);
                            if (Name.equals(user.getName())) {
                                if (Password.equals(user.getPassword())) {
                                    progressDialog.dismiss();
                                    Const.currentUser = user;
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    Const.showToast(getApplicationContext(), "Logged in successfully.");
                                } else {
                                    progressDialog.dismiss();
                                    Const.showToast(getApplicationContext(), "Wrong Password!");
                                }
                            } else {
                                progressDialog.dismiss();
                                Const.showToast(getApplicationContext(), "Wrong Username!");
                            }
                        } else {
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.setMessage("Registering & Logging In...");
                            progressDialog.show();
                            Const.rootAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    progressDialog.dismiss();
                                    User user = new User(Name, Password, authResult.getUser().getUid());
                                    reference.child(Name).setValue(user);
                                    Const.currentUser = user;
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    Const.showToast(getApplicationContext(), "Logged in successfully.");
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

    }
}