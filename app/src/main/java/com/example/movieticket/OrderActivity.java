package com.example.movieticket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrderActivity extends AppCompatActivity {

    TextView tvName, tvDate, tvPrice, tvShift, tvNumber, tvTotalPrice;
    Button btnSelectShift, btnConfirm;
    Spinner spinnerMethod;
    CheckBox cbCoin;
    ScrollView scrollView;
    GridView gridviewPosition;
    ArrayAdapter adapter;
    ArrayList<String> data = new ArrayList<>();
    LinearLayout areaSelect;
    ArrayList<Shift> listShift = new ArrayList<>();
    ArrayList<String> listPositionSelected = new ArrayList<>();
    ArrayList<String> listPositionCurrent = new ArrayList<>();
    String id, name, date;
    Double price, totalPrice;
    int number;
    String method;
    final String[] listMethod = {"Tiền mặt", "Thẻ tín dụng", "Ví điện tử"};
    DecimalFormat formatter;
    boolean isUseCoin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order);

        tvName = findViewById(R.id.tvName);
        tvDate = findViewById(R.id.tvDate);
        tvPrice = findViewById(R.id.tvPrice);
        tvShift = findViewById(R.id.tvShift);
        tvNumber = findViewById(R.id.tvNumber);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnSelectShift = findViewById(R.id.btnSelectShift);
        btnConfirm = findViewById(R.id.btnConfirm);
        spinnerMethod = findViewById(R.id.spinnerMethod);
        scrollView = findViewById(R.id.scrollView);
        cbCoin = findViewById(R.id.cbCoin);
        gridviewPosition = findViewById(R.id.gridviewPosition);
        areaSelect = findViewById(R.id.areaSelect);

        areaSelect.setVisibility(View.GONE);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        name = intent.getStringExtra("name");
        date = intent.getStringExtra("date");
        price = intent.getDoubleExtra("price", 0);

        tvName.setText(name);
        tvDate.setText(date);
        formatter = new DecimalFormat("#,### VND");
        tvPrice.setText(formatter.format(price));
        tvNumber.setText("0");
        tvTotalPrice.setText("0 VND");

        for(int i=1;i<=30;i++) {
            data.add(i + "");
        }

        adapter = new ArrayAdapter<String>(
                this,
                R.layout.position_item,
                R.id.btnPosition,
                data) {

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View itemView = super.getView(position, convertView, parent);

                Button btnPosition = itemView.findViewById(R.id.btnPosition);

                if(listPositionSelected.contains(data.get(position))) {
                    btnPosition.setBackgroundColor(Color.YELLOW);
                }
                else {
                    btnPosition.setBackgroundColor(Color.parseColor("#4EB2BF"));
                    btnPosition.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!listPositionCurrent.contains(data.get(position))) {
                                listPositionCurrent.add(data.get(position));
                                btnPosition.setBackgroundColor(Color.parseColor("#57D849"));
                            }
                            else {
                                listPositionCurrent.remove(data.get(position));
                                btnPosition.setBackgroundColor(Color.parseColor("#4EB2BF"));
                            }

                            number = listPositionCurrent.size();
                            tvNumber.setText(number + "");
                            totalPrice = listPositionCurrent.size() * price;
                            if(isUseCoin) {
                                totalPrice = totalPrice * 0.5;
                                tvTotalPrice.setText(formatter.format(totalPrice) + " (-50%)");
                            }
                            else {
                                tvTotalPrice.setText(formatter.format(totalPrice));
                            }
                        }
                    });
                }

                return itemView;
            }

        };

        gridviewPosition.setAdapter(adapter);
        ArrayAdapter<String> adapterMethod = new ArrayAdapter<String>(this, R.layout.custom_spinner_item, listMethod) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextSize(20);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextSize(20);
                return view;
            }
        };

        adapterMethod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMethod.setAdapter(adapterMethod);
        spinnerMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                method = (String) parentView.getItemAtPosition(position);

                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        btnSelectShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] shiftItems = new String[listShift.size()];
                for(int i=0;i<listShift.size();i++) {
                    shiftItems[i] = listShift.get(i).time;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
                builder.setItems(shiftItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String time = shiftItems[which];
                        tvShift.setText(time);
                        loadDataPosition(time);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        cbCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManager sessionManager = new SessionManager(OrderActivity.this);
                if(sessionManager.getCoin() < 100) {
                    showDialogCoin(sessionManager.getCoin());
                    cbCoin.setChecked(false);
                }
                else {
                    isUseCoin = cbCoin.isChecked();
                    displayTotalPrice();
                }
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(number <= 0) {
                    Toast.makeText(OrderActivity.this, "Vui lòng chọn chỗ ngồi", Toast.LENGTH_SHORT).show();
                }
                else {
                    showDialogConfirm();
                }
            }
        });

        loadDataShift();
    }

    private void displayTotalPrice() {
        if(isUseCoin) {
            totalPrice = totalPrice * 0.5;
            tvTotalPrice.setText(formatter.format(totalPrice) + " (-50%)");
        }
        else {
            totalPrice = totalPrice * 2;
            tvTotalPrice.setText(formatter.format(totalPrice));
        }
    }

    private void loadDataShift() {
        OkHttpClient client = new OkHttpClient();

        FormBody.Builder builder = new FormBody.Builder();
        builder.add("movieId", id);
        RequestBody formBody = builder.build();

        Request request = new Request.Builder().url("https://api-movie-ticket.onrender.com/movies/shifts")
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
                            JSONObject shiftObject = dataArray.getJSONObject(i);

                            String id = shiftObject.getString("_id");
                            String movieId = shiftObject.getString("movieId");
                            String time = shiftObject.getString("time");
                            String listSelected = shiftObject.getString("selected");
                            List<String> selected = new ArrayList<>();
                            if(!listSelected.equals("")) {
                                selected = new ArrayList<>(Arrays.asList(listSelected.split(",")));
                            }

                            Shift shift = new Shift(id, movieId, time, selected);
                            listShift.add(shift);
                        }
                    }

                    Log.d("success", "success");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                } catch (JSONException e) {
                    Log.d("onResponse", e.getMessage());
                }
            }
        });
    }

    private void loadDataPosition(String time) {
        listPositionCurrent.clear();
        listPositionSelected.clear();
        number = 0;
        tvNumber.setText("0");
        totalPrice = 0.0;
        tvTotalPrice.setText("0 VND");
        cbCoin.setChecked(false);

        listShift.forEach(shift -> {
            if(shift.time.equals(time)) {
                shift.selected.forEach(position -> {
                    listPositionSelected.add(position);
                });
            }
        });

        adapter.notifyDataSetChanged();
        areaSelect.setVisibility(View.VISIBLE);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.menu_back) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }

        return false;
    }

    private void showDialogCoin(int coin) throws Resources.NotFoundException {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("Không đủ xu");
        alertDialog.setIcon(R.drawable.icons8_coin_75);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView tvCoin = new TextView(this);
        tvCoin.setText("Xu hiện tại: " + coin);
        tvCoin.setTextSize(20);
        tvCoin.setTextColor(Color.parseColor("#2596be"));
        layout.addView(tvCoin);

        TextView tvNotification = new TextView(this);
        tvNotification.setText("Xu tối thiểu cần: 100");
        tvNotification.setTextSize(20);
        tvNotification.setTextColor(Color.parseColor("#FFEA0909"));
        layout.addView(tvNotification);

        layout.setPadding(30, 0, 30, 0);
        layout.setBackgroundColor(Color.parseColor("#eeeee4"));
        alertDialog.setView(layout);

        alertDialog.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        alertDialog.show();
    }

    private void showDialogConfirm() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("Xác nhận thông tin vé");
        alertDialog.setIcon(R.drawable.baseline_payment_24);

        LinearLayout outerLayout = new LinearLayout(this);
        outerLayout.setOrientation(LinearLayout.VERTICAL);

        //name
        LinearLayout nameLayout = new LinearLayout(this);
        nameLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams nameLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        nameLayoutParams.setMargins(0, 10, 0, 0);
        nameLayout.setLayoutParams(nameLayoutParams);
        TextView tvTitleName = new TextView(this);
        tvTitleName.setText(Html.fromHtml("<i>Tên phim:</i>"));
        tvTitleName.setTextSize(20);
        tvTitleName.setWidth(350);
        nameLayout.addView(tvTitleName);
        TextView tvName = new TextView(this);
        tvName.setText(Html.fromHtml("<b>" + name + "</b>"));
        tvName.setTextSize(20);
        tvName.setTextColor(Color.BLACK);
        nameLayout.addView(tvName);
        outerLayout.addView(nameLayout);

        //date
        LinearLayout dateLayout = new LinearLayout(this);
        dateLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams dateLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        dateLayoutParams.setMargins(0, 10, 0, 0);
        dateLayout.setLayoutParams(dateLayoutParams);
        TextView tvTitleDate = new TextView(this);
        tvTitleDate.setText(Html.fromHtml("<i>Ngày chiếu:</i>"));
        tvTitleDate.setTextSize(20);
        tvTitleDate.setWidth(350);
        dateLayout.addView(tvTitleDate);
        TextView tvDate = new TextView(this);
        tvDate.setText(Html.fromHtml("<b>" + date + "</b>"));
        tvDate.setTextSize(20);
        tvDate.setTextColor(Color.BLACK);
        dateLayout.addView(tvDate);
        outerLayout.addView(dateLayout);

        //shift
        LinearLayout timeLayout = new LinearLayout(this);
        timeLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams timeLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        timeLayoutParams.setMargins(0, 10, 0, 0);
        timeLayout.setLayoutParams(timeLayoutParams);
        TextView tvTitleTime = new TextView(this);
        tvTitleTime.setText(Html.fromHtml("<i>Suất chiếu:</i>"));
        tvTitleTime.setTextSize(20);
        tvTitleTime.setWidth(350);
        timeLayout.addView(tvTitleTime);
        TextView tvTime = new TextView(this);
        tvTime.setText(Html.fromHtml("<b>" + tvShift.getText().toString() + "</b>"));
        tvTime.setTextSize(20);
        tvTime.setTextColor(Color.BLACK);
        timeLayout.addView(tvTime);
        outerLayout.addView(timeLayout);

        //number
        LinearLayout numberTicketLayout = new LinearLayout(this);
        numberTicketLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams numberLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        numberLayoutParams.setMargins(0, 10, 0, 0);
        numberTicketLayout.setLayoutParams(numberLayoutParams);
        TextView tvTitleNumberTicket = new TextView(this);
        tvTitleNumberTicket.setText(Html.fromHtml("<i>Số lượng vé:</i>"));
        tvTitleNumberTicket.setTextSize(20);
        tvTitleNumberTicket.setWidth(350);
        numberTicketLayout.addView(tvTitleNumberTicket);
        TextView tvNumberTicket = new TextView(this);
        tvNumberTicket.setText(Html.fromHtml("<b>" + tvNumber.getText().toString() + "</b>"));
        tvNumberTicket.setTextSize(20);
        tvNumberTicket.setTextColor(Color.BLACK);
        numberTicketLayout.addView(tvNumberTicket);
        outerLayout.addView(numberTicketLayout);

        //position selected
        Collections.sort(listPositionCurrent, new Comparator<String>() {
            @Override
            public int compare(String number1, String number2) {
                return Integer.compare(Integer.parseInt(number1), Integer.parseInt(number2));
            }
        });
        String selected = String.join(", ", listPositionCurrent);
        LinearLayout selectedLayout = new LinearLayout(this);
        selectedLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams selectedLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        selectedLayoutParams.setMargins(0, 10, 0, 0);
        selectedLayout.setLayoutParams(selectedLayoutParams);
        TextView tvTitleSelected = new TextView(this);
        tvTitleSelected.setText(Html.fromHtml("<i>Chỗ ngồi:</i>"));
        tvTitleSelected.setTextSize(20);
        tvTitleSelected.setWidth(350);
        selectedLayout.addView(tvTitleSelected);
        TextView tvSelected = new TextView(this);
        tvSelected.setText(Html.fromHtml("<b>" + selected + "</b>"));
        tvSelected.setTextSize(20);
        tvSelected.setTextColor(Color.BLACK);
        selectedLayout.addView(tvSelected);
        outerLayout.addView(selectedLayout);

        //method
        LinearLayout methodLayout = new LinearLayout(this);
        methodLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams methodLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        methodLayoutParams.setMargins(0, 10, 0, 0);
        methodLayout.setLayoutParams(methodLayoutParams);
        TextView tvTitleMethod = new TextView(this);
        tvTitleMethod.setText(Html.fromHtml("<i>Phương thức:</i>"));
        tvTitleMethod.setTextSize(20);
        tvTitleMethod.setWidth(350);
        methodLayout.addView(tvTitleMethod);
        TextView tvMethod = new TextView(this);
        tvMethod.setText(Html.fromHtml("<b>" + method + "</b>"));
        tvMethod.setTextSize(20);
        tvMethod.setTextColor(Color.BLACK);
        methodLayout.addView(tvMethod);
        outerLayout.addView(methodLayout);

        //total price
        LinearLayout totalLayout = new LinearLayout(this);
        totalLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams totalLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        totalLayoutParams.setMargins(0, 10, 0, 0);
        totalLayout.setLayoutParams(totalLayoutParams);
        TextView tvTitleTotal = new TextView(this);
        tvTitleTotal.setText(Html.fromHtml("<b>Tổng tiền:</b>"));
        tvTitleTotal.setTextSize(20);
        tvTitleTotal.setTextColor(Color.BLACK);
        tvTitleTotal.setWidth(350);
        totalLayout.addView(tvTitleTotal);
        TextView tvTotal = new TextView(this);
        tvTotal.setText(Html.fromHtml("<b>" + formatter.format(totalPrice) + "</b>"));
        tvTotal.setTextSize(20);
        tvTotal.setTextColor(Color.RED);
        totalLayout.addView(tvTotal);
        outerLayout.addView(totalLayout);

        outerLayout.setPadding(30, 0, 30, 0);
        alertDialog.setView(outerLayout);
        alertDialog.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                payment();
            }
        });

        alertDialog.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        AlertDialog dialog = alertDialog.create();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }


    private void payment() {
        if(method.equals(listMethod[0])) {
            Toast.makeText(OrderActivity.this, "Sử dụng " + method, Toast.LENGTH_SHORT).show();
        }
        else if(method.equals(listMethod[1])) {
            Toast.makeText(OrderActivity.this, "Sử dụng " + method, Toast.LENGTH_SHORT).show();
        }
        else if(method.equals(listMethod[2])) {
            Toast.makeText(OrderActivity.this, "Sử dụng " + method, Toast.LENGTH_SHORT).show();
        }

    }
}