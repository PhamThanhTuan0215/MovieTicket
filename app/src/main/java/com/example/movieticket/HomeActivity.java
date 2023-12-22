package com.example.movieticket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {

    EditText edtSearch;
    Button btnSelectCategory, btnSelectActor, btnSelectAll;
    TextView tvStyleSearch;
    ListView listView;
    LinearLayout areaHistory, areaAccount, areaEmpty;
    ArrayAdapter<Movie> adapter;
    ArrayList<Movie> dataMovies;
    ArrayList<Movie> listMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        edtSearch = findViewById(R.id.edtSearch);
        btnSelectCategory = findViewById(R.id.btnSelectCategory);
        btnSelectActor = findViewById(R.id.btnSelectActor);
        btnSelectAll = findViewById(R.id.btnSelectAll);
        tvStyleSearch = findViewById(R.id.tvStyleSearch);
        listView = findViewById(R.id.listViewMovie);
        areaEmpty = findViewById(R.id.areaEmpty);
        areaHistory = findViewById(R.id.areaHistory);
        areaAccount = findViewById(R.id.areaAccount);

        dataMovies = new ArrayList<>();
        listMovies = new ArrayList<>();
        getDataMovies();
        getMoviesToReminder();

        adapter = new ArrayAdapter<Movie>(
                this,
                R.layout.movie_item,
                R.id.tvName,
                dataMovies) {

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View itemView = super.getView(position, convertView, parent);

                TextView tvName = itemView.findViewById(R.id.tvName);
                TextView tvDate = itemView.findViewById(R.id.tvDate);
                TextView tvPrice = itemView.findViewById(R.id.tvPrice);
                ImageView imgView = itemView.findViewById(R.id.imgView);
                tvName.setText(dataMovies.get(position).name);
                tvDate.setText(dataMovies.get(position).date);
                DecimalFormat formatter = new DecimalFormat("#,### VND");
                tvPrice.setText(formatter.format(dataMovies.get(position).price));

                Picasso.get().load(dataMovies.get(position).url_avatar).into(imgView);

                return itemView;
            }
        };

        listView.setAdapter(adapter);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString();
                loadDataMovies(keyword, null, null);
            }
        });

        btnSelectCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectCategory();
            }
        });

        btnSelectActor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectActor();
            }
        });

        btnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearch.setText("");
                loadDataMovies(null,null, null);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HomeActivity.this, DetailsMovieActivity.class);
                intent.putExtra("id", dataMovies.get(position).id);
                intent.putExtra("name", dataMovies.get(position).name);
                intent.putExtra("date", dataMovies.get(position).date);
                intent.putExtra("price", dataMovies.get(position).price);
                intent.putExtra("description", dataMovies.get(position).description);
                intent.putExtra("url_avatar", dataMovies.get(position).url_avatar);
                intent.putExtra("url_trailer", dataMovies.get(position).url_trailer);
                String category = String.join(", ", dataMovies.get(position).category);
                intent.putExtra("category", category);
                String actor = String.join(", ", dataMovies.get(position).actor);
                intent.putExtra("actor", actor);

                startActivity(intent);
            }
        });

        areaHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        areaAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadDataMovies(String keyword, String category, String actor) {

        dataMovies.clear();
        areaEmpty.setVisibility(View.GONE);

        if(keyword != null) {
            listMovies.forEach(movie -> {
                String lowerCaseName = movie.name.toLowerCase();
                String lowerCaseKeyword  = keyword.toLowerCase();

                if (lowerCaseName.contains(lowerCaseKeyword)) {
                    dataMovies.add(movie);
                }
            });

            tvStyleSearch.setText("Từ khóa: " + keyword);
        }
        else if(category != null) {
            listMovies.forEach(movie -> {
                if(movie.category.contains(category)) {
                    dataMovies.add(movie);
                }
            });

            tvStyleSearch.setText("Thể loại: " + category);
        }
        else if(actor != null) {
            listMovies.forEach(movie -> {
                if(movie.actor.contains(actor)) {
                    dataMovies.add(movie);
                }
            });

            tvStyleSearch.setText("Diễn viên: " + actor);
        }
        else {
            listMovies.forEach(movie -> {
                dataMovies.add(movie);
            });

            tvStyleSearch.setText("(Tất cả phim)");
        }

        adapter.notifyDataSetChanged();
        if(dataMovies.size() == 0) {
            areaEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void openSelectCategory() {
        String[] categoryItems = { "Action", "Adventure", "Animation", "Comedy", "Crime", "Drama", "Horror", "Sci-Fi", "Romance", "Fantasy",
                "Mystery", "Thriller", "War", "Social", "Detective", "Documentary", "Family", "Historical", "Musical", "Sport"};
        Arrays.sort(categoryItems);

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setItems(categoryItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String category = categoryItems[which];
                edtSearch.setText("");
                loadDataMovies(null, category, null);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openSelectActor() {
        String[] actorItems = {"Leonardo DiCaprio", "Ellen Page", "Tim Robbins", "Morgan Freeman", "Christian Bale",
                "Heath Ledger", "Tom Hanks", "Robin Wright", "Keanu Reeves", "Carrie-Anne Moss"};

        Arrays.sort(actorItems);

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setItems(actorItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String actor = actorItems[which];
                edtSearch.setText("");
                loadDataMovies(null, null, actor);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getDataMovies() {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://api-movie-ticket.onrender.com/movies").build();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("onFailure", e.getMessage());
            }
            @Override
            public void onResponse(Call call, final Response response)
                    throws IOException {
                try {
                    String responseData = response.body().string();
                    JSONObject json = new JSONObject(responseData);
                    JSONArray dataArray = json.getJSONArray("data");

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject movieObject = dataArray.getJSONObject(i);

                        String id = movieObject.getString("_id");
                        String name = movieObject.getString("name");
                        String listCategory = movieObject.getString("category");
                        String listActor = movieObject.getString("actor");
                        String date = movieObject.getString("date");
                        String description = movieObject.getString("description");
                        Double price = movieObject.getDouble("price");
                        String url_avatar = movieObject.getString("url_avatar");
                        String url_trailer = movieObject.getString("url_trailer");

                        List<String> category = new ArrayList<>(Arrays.asList(listCategory.split(",")));
                        List<String> actor = new ArrayList<>(Arrays.asList(listActor.split(",")));

                        Movie movie = new Movie(id, name, category, actor, date, description, price, url_avatar, url_trailer);
                        listMovies.add(movie);
                    }

                    Log.d("success", "success");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadDataMovies(null, null, null);
                        }
                    });
                } catch (JSONException e) {
                    Log.d("onResponse", e.getMessage());
                }
            }
        });
    }

    public void reminderMovie(String id, String movieName, String date) {
        AlarmManager alarmManager;
        PendingIntent pendingIntent;

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        Date dateMovie = null;
        try {
            dateMovie = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        if (date != null) {
            calendar.setTime(dateMovie);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }

        Intent intent = new Intent(HomeActivity.this, ReminderReceiver.class);
        intent.setAction("Reminder");
        intent.putExtra("name", movieName);
        intent.putExtra("date", date);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        int hashValueId = id.hashCode();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(HomeActivity.this, hashValueId, intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(HomeActivity.this, hashValueId, intent, PendingIntent.FLAG_MUTABLE);
        }

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        Log.d("message", "gửi nhắc nhở: " + movieName);
    }

    private void getMoviesToReminder() {
        SessionManager sessionManager = new SessionManager(this);

        if(!sessionManager.isLoggedIn()) {
            return;
        }

        ArrayList<Reminder> listReminder = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();

        FormBody.Builder builder = new FormBody.Builder();
        builder.add("username", sessionManager.getUsername());
        RequestBody formBody = builder.build();

        Request request = new Request.Builder().url("https://api-movie-ticket.onrender.com/orders")
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("onFailure", e.getMessage());
            }
            @Override
            public void onResponse(Call call, final Response response)
                    throws IOException {
                try {
                    String responseData = response.body().string();
                    JSONObject json = new JSONObject(responseData);
                    int code = json.getInt("code");

                    if(code == 0) {
                        JSONArray dataArray = json.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject orderObject = dataArray.getJSONObject(i);

                            String id = orderObject.getString("_id");
                            String movieName = orderObject.getString("movieName");
                            String date = orderObject.getString("date");

                            Reminder reminder = new Reminder(id, movieName, date);
                            listReminder.add(reminder);
                        }
                    }

                    Log.d("success", "success");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(code == 0) {
                                if(listReminder.size() > 0) {
                                    for (int i = 0; i < listReminder.size(); i++) {
                                        String idReminder = listReminder.get(i).id;
                                        if(!sessionManager.getStatusReminderMovie(idReminder)) {
                                            reminderMovie(idReminder, listReminder.get(i).movieName, listReminder.get(i).date);
                                            sessionManager.setStatusReminderMovie(idReminder);
                                        }
                                    }
                                }
                            }
                        }
                    });
                } catch (JSONException e) {
                    Log.d("onResponse", e.getMessage());
                }
            }
        });
    }
}