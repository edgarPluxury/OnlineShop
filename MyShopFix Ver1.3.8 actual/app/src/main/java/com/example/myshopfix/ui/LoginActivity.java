package com.example.myshopfix.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myshopfix.HomeActivity;
import com.example.myshopfix.R;
import com.example.myshopfix.Model.Users;
import com.example.myshopfix.Prevalent.Prevalent;
import com.example.myshopfix.ui.Admin.AdminCategoryActivity;
import com.example.myshopfix.ui.Users.HomeLogoutActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private Button login_btn;
    private EditText  phone_input, password_input;
    private ProgressDialog loading_bar;
    private String parentDbName = "Users";
    private com.rey.material.widget.CheckBox checkBoxRememberMe;

    private TextView AdminLink, NotAdminLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_btn = (Button) findViewById(R.id.login_btn);
        phone_input = (EditText) findViewById(R.id.login_phone_input);
        password_input = (EditText) findViewById(R.id.login_password_input);
        loading_bar = new ProgressDialog(this);
        checkBoxRememberMe = (CheckBox) findViewById(R.id.login_checkbox);
        Paper.init(this);

        AdminLink = (TextView) findViewById(R.id.admin_panel_link);
        NotAdminLink = (TextView) findViewById(R.id.not_admin_panel_link);


        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });


        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                login_btn.setText("Вход для админа");
                parentDbName = "Admins";
            }
        });
        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                login_btn.setText("Войти");
                parentDbName = "Users";
            }
        });
    }

    private void loginUser() {
        String phone = phone_input.getText().toString();
        String password = password_input.getText().toString();
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Введите номер телефона", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
        }
        else {
            loading_bar.setTitle("Вход в приложение");
            loading_bar.setMessage("Пожайлуйста, подождите");
            loading_bar.setCanceledOnTouchOutside(false);
            loading_bar.show();

            ValidateUser(phone,password);
        }
    }

    private void ValidateUser(String phone, String password) {

        if(checkBoxRememberMe.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(parentDbName).child(phone).exists()){
                    Users usersData = snapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if (usersData.getPhone().equals(phone)){
                        if (usersData.getPassword().equals(password)){
                            if (parentDbName.equals("Users")){
                                loading_bar.dismiss();
                                Toast.makeText(LoginActivity.this, "Успешный вход", Toast.LENGTH_SHORT).show();
                                Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                Prevalent.currentOnlineUser = usersData;
                                startActivity(homeIntent);
                            }
                            else if(parentDbName.equals("Admins")){
                                loading_bar.dismiss();
                                Toast.makeText(LoginActivity.this, "Успешный вход", Toast.LENGTH_SHORT).show();
                                Intent homeIntent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                startActivity(homeIntent);
                            }
                        }
                        else {
                            loading_bar.dismiss();
                            Toast.makeText(LoginActivity.this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    loading_bar.dismiss();
                    Toast.makeText(LoginActivity.this, "Аккаунт с номером" + phone + " не существует", Toast.LENGTH_SHORT).show();
                    Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(registerIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}