package com.example.barcode;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Button btn_login;
    EditText txtEmail,txtPassword;
    boolean sayi = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtEmail=findViewById(R.id.email);
        txtPassword=findViewById(R.id.password);
        btn_login=findViewById(R.id.btn_login);
        mAuth = FirebaseAuth.getInstance();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(sayi){
                    sayi=false;
                    String email =txtEmail.getText().toString().trim();
                    String password =txtPassword.getText().toString().trim();

                    if(TextUtils.isEmpty(email)){
                        Toast.makeText(Login.this,"Please Enter Email",Toast.LENGTH_SHORT).show();
                        sayi=true;
                        return;
                    }
                    if(TextUtils.isEmpty(password)){
                        Toast.makeText(Login.this, "Please Enter Password",Toast.LENGTH_SHORT).show();
                        sayi=true;
                        return;
                    }

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(getApplicationContext(),Scan.class));
                                        Toast.makeText(Login.this,"Login Success",Toast.LENGTH_SHORT).show();
                                        sayi=true;
                                    } else {
                                        Toast.makeText(Login.this,"Login Failed. Check Your Mail And Password",Toast.LENGTH_SHORT).show();
                                        sayi=true;
                                    }

                                }
                            });

                }


            }
        });

    }
}
