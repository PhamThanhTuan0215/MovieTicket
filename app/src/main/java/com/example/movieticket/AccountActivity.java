package com.example.movieticket;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AccountActivity extends AppCompatActivity {

    SharedPreferences myPrefs;
    LinearLayout areaHome, areaHistory, areaInformation, areaLoginRegister;
    TextView tvUsername, tvEmail;
    Button btnLogin, btnRegister, btnLogout, btnChangeInformation;

    static String id = "";
    String username;
    String email;
    String password;

    final static int LoginCode = 1001;
    final static int RegisterCode = 1002;
    final static int ChaneInformationCode = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);

        areaHome = findViewById(R.id.areaHome);
        areaHistory = findViewById(R.id.areaHistory);
        areaInformation = findViewById(R.id.areaInformation);
        areaLoginRegister = findViewById(R.id.areaLoginRegister);
        tvUsername = findViewById(R.id.tvUsername);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        tvEmail = findViewById(R.id.tvEmail);
        btnLogout = findViewById(R.id.btnLogout);
        btnChangeInformation = findViewById(R.id.btnChangeInformation);

        myPrefs = getSharedPreferences("my_prefs", Activity.MODE_PRIVATE);
        id = myPrefs.getString("id", "");
        username = myPrefs.getString("username", "");
        email = myPrefs.getString("email", "");
        password = myPrefs.getString("password", "");

        checkLogin();

        areaHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        areaHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                startActivityForResult(intent, LoginCode);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, RegisterActivity.class);
                startActivityForResult(intent, RegisterCode);
            }
        });

        btnChangeInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, ChangeInformationActivity.class);

                intent.putExtra("id", id);
                intent.putExtra("username", username);
                intent.putExtra("email", email);
                intent.putExtra("password", password);

                startActivityForResult(intent, ChaneInformationCode);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogLogout();
            }
        });
    }

    private void checkLogin() {
        if(id.equals("")) {
            tvUsername.setText("");
            tvEmail.setText("");

            areaLoginRegister.setVisibility(View.VISIBLE);
            areaInformation.setVisibility(View.GONE);
        }

        else {
            areaLoginRegister.setVisibility(View.GONE);
            areaInformation.setVisibility(View.VISIBLE);

            tvUsername.setText(username);
            tvEmail.setText(email);
        }
    }

    private void showDialogLogout() throws Resources.NotFoundException {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận đăng xuất")
                .setMessage("Bạn có muốn đăng xuất?")
                .setIcon(R.drawable.baseline_logout_24)
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = myPrefs.edit();
                        editor.putString("id", "");
                        editor.putString("username", "");
                        editor.putString("email", "");
                        editor.putString("password", "");
                        editor.commit();

                        id = "";
                        username = "";
                        email = "";
                        password = "";

                        checkLogin();
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                }).show();
    }

    private void showDialogRegisterSuccess() throws Resources.NotFoundException {
        new AlertDialog.Builder(this)
                .setTitle("Trạng thái đăng ký")
                .setMessage("Đăng ký tài khoản thành công")
                .setIcon(R.drawable.baseline_update_success)
                .setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            id = data.getStringExtra("id");
            username = data.getStringExtra("username");
            password = data.getStringExtra("password");
            email = data.getStringExtra("email");

            SharedPreferences.Editor editor = myPrefs.edit();
            editor.putString("id", id);
            editor.putString("username", username);
            editor.putString("email", email);
            editor.putString("password", password);
            editor.commit();

            checkLogin();

            if(requestCode == RegisterCode) {
                showDialogRegisterSuccess();
            }
        }
    }
}