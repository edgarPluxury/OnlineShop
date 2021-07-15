package com.example.myshopfix.ui.Users;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.myshopfix.HomeActivity;
import com.example.myshopfix.ui.LoginActivity;
import com.example.myshopfix.Model.Users;
import com.example.myshopfix.Prevalent.Prevalent;
import com.example.myshopfix.R;
import com.example.myshopfix.ui.RegisterActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class HomeLogoutActivity extends AppCompatActivity {
    private Button logoutBtn;



    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_logout);


                    logoutBtn = (Button) findViewById(R.id.button);

                    logoutBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Paper.book().destroy();
                            Intent logoutIntent = new Intent(HomeLogoutActivity.this, MainActivity.class);
                            startActivity(logoutIntent);
                        }
                    });
                }

    public static class MainActivity extends AppCompatActivity {
        private Button join_button, login_button;
        private ProgressDialog loading_bar;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            join_button = (Button) findViewById(R.id.main_join_btn);
            login_button = (Button) findViewById(R.id.main_login_btn);
            loading_bar = new ProgressDialog(this);

            Paper.init(this);

            login_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                }
            });
            join_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(registerIntent);
                }
            });

            String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
            String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);


            if (UserPhoneKey != "" && UserPasswordKey != "") {
                if (!TextUtils.isEmpty(UserPhoneKey) && (!TextUtils.isEmpty(UserPasswordKey))) {
                    ValidateUser(UserPhoneKey, UserPasswordKey);
                    loading_bar.setTitle("Вход в приложение");
                    loading_bar.setMessage("Пожайлуйста, подождите");
                    loading_bar.setCanceledOnTouchOutside(false);
                    loading_bar.show();


                }
            }
        }

        private void ValidateUser(String phone, String password) {
            final DatabaseReference RootRef;
            RootRef = FirebaseDatabase.getInstance().getReference();
            RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("Users").child(phone).exists()) {
                        Users usersData = snapshot.child("Users").child(phone).getValue(Users.class);

                        if (usersData.getPhone().equals(phone)) {
                            if (usersData.getPassword().equals(password)) {
                                loading_bar.dismiss();
                                Toast.makeText(MainActivity.this, "Успешный вход", Toast.LENGTH_SHORT).show();
                                Intent homeIntent = new Intent(MainActivity.this, HomeLogoutActivity.class);
                                startActivity(homeIntent);
                            } else {
                                loading_bar.dismiss();
                            }
                        }
                    } else {
                        loading_bar.dismiss();
                        Toast.makeText(MainActivity.this, "Аккаунт с номером" + phone + " не существует", Toast.LENGTH_SHORT).show();
                        Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                        startActivity(registerIntent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
