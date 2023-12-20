package com.example.movieticket;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class AccountActivity extends AppCompatActivity {

    SharedPreferences myPrefs;
    LinearLayout areaCoin, areaHome, areaHistory, areaInformation, areaLoginRegister;
    TextView tvCoin, tvUsername, tvEmail, tvPhone;
    Button btnLogin, btnRegister, btnLogout, btnChangeInformation;
    static String id = "";
    static int coin = 0;
    String username, email, phone, password;
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
        tvCoin = findViewById(R.id.tvCoin);
        areaCoin = findViewById(R.id.areaCoin);
        tvUsername = findViewById(R.id.tvUsername);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        btnLogout = findViewById(R.id.btnLogout);
        btnChangeInformation = findViewById(R.id.btnChangeInformation);

        myPrefs = getSharedPreferences("my_prefs", Activity.MODE_PRIVATE);
        id = myPrefs.getString("id", "");
        coin = myPrefs.getInt("coin", 0);
        username = myPrefs.getString("username", "");
        email = myPrefs.getString("email", "");
        phone = myPrefs.getString("phone", "");
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
                intent.putExtra("phone", phone);
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

        areaCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogCoin();
            }
        });
    }

    private void checkLogin() {
        if(id.equals("")) {
            tvCoin.setText("0");
            tvUsername.setText("");
            tvEmail.setText("");
            tvPhone.setText("");

            areaLoginRegister.setVisibility(View.VISIBLE);
            areaInformation.setVisibility(View.GONE);
        }

        else {
            areaLoginRegister.setVisibility(View.GONE);
            areaInformation.setVisibility(View.VISIBLE);

            tvCoin.setText(coin + "");
            tvUsername.setText(username);
            tvEmail.setText(email);
            tvPhone.setText(phone);
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
                        editor.putInt("coin", 0);
                        editor.putString("username", "");
                        editor.putString("email", "");
                        editor.putString("phone", "");
                        editor.putString("password", "");
                        editor.commit();

                        id = "";
                        coin = 0;
                        username = "";
                        email = "";
                        phone = "";
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

    private void showDialogCoin() throws Resources.NotFoundException {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("Xu thưởng");
        alertDialog.setIcon(R.drawable.icons8_coin_75);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView tvCoin = new TextView(this);
        tvCoin.setText("Xu hiện tại: " + coin);
        tvCoin.setTextSize(20);
        tvCoin.setTextColor(Color.parseColor("#2596be"));
        layout.addView(tvCoin);

        TextView tvNotification = new TextView(this);
        tvNotification.setText("Nhận 10 xu mỗi 100.000 VND tiền vé thanh toán");
        tvNotification.setTextSize(20);
        tvNotification.setTextColor(Color.parseColor("#FFEA0909"));
        layout.addView(tvNotification);

        TextView tvTip = new TextView(this);
        tvTip.setText("Sử dụng 100 xu để giảm 50% tổng tiền thanh toán giá vé");
        tvTip.setTextSize(20);
        tvTip.setTextColor(Color.parseColor("#FFEA0909"));
        layout.addView(tvTip);

        layout.setPadding(30, 0, 30, 0);
        layout.setBackgroundColor(Color.parseColor("#eeeee4"));
        alertDialog.setView(layout);

        alertDialog.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            id = data.getStringExtra("id");
            coin = data.getIntExtra("coin", 0);
            username = data.getStringExtra("username");
            email = data.getStringExtra("email");
            phone = data.getStringExtra("phone");
            password = data.getStringExtra("password");

            SharedPreferences.Editor editor = myPrefs.edit();
            editor.putString("id", id);
            editor.putInt("coin", coin);
            editor.putString("username", username);
            editor.putString("email", email);
            editor.putString("phone", phone);
            editor.putString("password", password);
            editor.commit();

            checkLogin();

            if(requestCode == RegisterCode) {
                showDialogRegisterSuccess();
            }
        }
    }
}