package com.example.movieticket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

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
        listView = findViewById(R.id.listView);
        areaEmpty = findViewById(R.id.areaEmpty);
        areaHistory = findViewById(R.id.areaHistory);
        areaAccount = findViewById(R.id.areaAccount);

        dataMovies = new ArrayList<>();
        listMovies = new ArrayList<>();
//        getDataMovies();

        new DatabaseTask().execute();

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

        loadDataMovies(null, null, null);

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
        Movie movie1 = new Movie(1,
                "Inception",
                Arrays.asList("Sci-Fi", "Action"),
                Arrays.asList("Leonardo DiCaprio", "Ellen Page"),
                "2010-07-16",
                "A thief who enters the dreams of others to steal their secrets.",
                150000,
                "https://m.media-amazon.com/images/M/MV5BZGFjOTRiYjgtYjEzMS00ZjQ2LTkzY2YtOGQ0NDI2NTVjOGFmXkEyXkFqcGdeQXVyNDQ5MDYzMTk@._V1_.jpg",
                "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/YoHD9XEInc0?si=xyz8sHdECrZvQxZN\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>");

        Movie movie2 = new Movie(2,
                "The Shawshank Redemption",
                Arrays.asList("Drama"),
                Arrays.asList("Tim Robbins", "Morgan Freeman"),
                "1994-09-23",
                "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
                120000,
                "https://m.media-amazon.com/images/M/MV5BMTc3NjM4MTY3MV5BMl5BanBnXkFtZTcwODk4Mzg3OA@@._V1_.jpg",
                "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/PLl99DlL6b4?si=eCQrmW7yNoQbqSof\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>");

        Movie movie3 = new Movie(3,
                "The Dark Knight",
                Arrays.asList("Action", "Crime", "Drama"),
                Arrays.asList("Christian Bale", "Heath Ledger"),
                "2008-07-18",
                "When the menace known as The Joker emerges from his mysterious past, he wreaks havoc and chaos on the people of Gotham.",
                110000,
                "https://m.media-amazon.com/images/M/MV5BMTkyMjQ4ODk1NF5BMl5BanBnXkFtZTcwMjcxMTk2Mw@@._V1_.jpg",
                "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/_PZpmTj1Q8Q?si=R9netO7e6rWWKtYq\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>");

        Movie movie4 = new Movie(4,
                "Forrest Gump",
                Arrays.asList("Drama", "Romance"),
                Arrays.asList("Tom Hanks", "Robin Wright"),
                "1994-07-06",
                "The presidencies of Kennedy and Johnson, the Vietnam War, the Watergate scandal, and other historical events unfold from the perspective of an Alabama man with an IQ of 75.",
                200000,
                "https://m.media-amazon.com/images/M/MV5BODQ0NjYyOWMtOGE4ZS00MDUxLTgwYzYtNDE2YWY2ODIyNWIzXkEyXkFqcGdeQXVyMTYzMDM0NTU@._V1_.jpg",
                "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/bLvqoHBptjg?si=N-d6jAj4rJwJtzH_\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>");

        Movie movie5 = new Movie(5,
                "The Matrix",
                Arrays.asList("Action", "Sci-Fi"),
                Arrays.asList("Keanu Reeves", "Carrie-Anne Moss"),
                "1999-03-31",
                "A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.",
                100000,
                "https://m.media-amazon.com/images/M/MV5BNzQzOTk3OTAtNDQ0Zi00ZTVkLWI0MTEtMDllZjNkYzNjNTc4L2ltYWdlXkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_.jpg",
                "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/vKQi3bBA1y8?si=0L2rvi0eZCCZkSpw\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>");

        listMovies.add(movie1);
        listMovies.add(movie2);
        listMovies.add(movie3);
        listMovies.add(movie4);
        listMovies.add(movie5);
    }

    private class DatabaseTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            return DatabaseExecutor.executeQuery();
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(HomeActivity.this, result, Toast.LENGTH_SHORT).show();
        }
    }
}