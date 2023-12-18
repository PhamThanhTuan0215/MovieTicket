package com.example.movieticket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class DetailsMovieActivity extends AppCompatActivity {

    ImageView imgViewDetail;
    TextView tvNameDetail, tvCategory, tvActor, tvDate, tvPrice, tvDescription;
    WebView webViewTrailer;
    ScrollView scrollView;

    Button btnOrder, btnPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_movie);

        imgViewDetail = findViewById(R.id.imgViewDetail);
        tvNameDetail = findViewById(R.id.tvNameDetail);
        tvCategory = findViewById(R.id.tvCategory);
        tvActor = findViewById(R.id.tvActor);
        tvDate = findViewById(R.id.tvDateDetail);
        tvPrice = findViewById(R.id.tvPriceDetail);
        tvDescription = findViewById(R.id.tvDescription);
        webViewTrailer = findViewById(R.id.webViewTrailer);
        scrollView = findViewById(R.id.scrollView);
        btnOrder = findViewById(R.id.btnOrder);
        btnPlay = findViewById(R.id.btnPlay);

        Intent intent = getIntent();
        tvNameDetail.setText(intent.getStringExtra("name"));
        tvCategory.setText(intent.getStringExtra("category"));
        tvActor.setText(intent.getStringExtra("actor"));
        tvPrice.setText(covertPrice(intent.getDoubleExtra("price", 0)));
        tvDescription.setText(intent.getStringExtra("description"));
        String url_avatar = intent.getStringExtra("url_avatar");

        Picasso.get().load(url_avatar).into(imgViewDetail);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
                String url_trailer = intent.getStringExtra("url_trailer");
                PlayTrailer(url_trailer);
            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetailsMovieActivity.this, "Mở activity đặt vé", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String covertPrice(double price) {
        DecimalFormat formatter = new DecimalFormat("#,### VND");
        return formatter.format(price);
    }

    private void PlayTrailer(String url_trailer) {
        webViewTrailer.loadData(url_trailer, "text/html", "utf-8");
        webViewTrailer.getSettings().setJavaScriptEnabled(true);
        webViewTrailer.setWebChromeClient(new WebChromeClient());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.details_menu_back) {
            finish();
            return true;
        }

        return false;
    }
}