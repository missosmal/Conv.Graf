package com.yarkov.convgraf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        EditText from = findViewById(R.id.editText);

        from.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Consider();
            }
        });

        Spinner spinner = findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Consider();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        LoadCurrencies();
    }
    Map<String, Сurrency> currs = new HashMap<String, Сurrency>();


    public void AlertDialog(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User taps OK button.
            }
        });
        builder.setTitle(title);
        builder.setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void loafSpinnerItem() {
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        for (String item : currs.keySet()) {
            adapter.add(item);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void URL(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.sberbank.ru/ru/quotes/currencies"));
        startActivity(intent);
    }


    void LoadCurrencies(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String body;
                Document ressponse = null;
                try {
                    ressponse = (Document) Jsoup.connect("https://www.cbr-xml-daily.ru/daily_json.js").ignoreContentType(true).get();
                } catch (IOException e) {
                    AlertDialog("Error", e.getMessage());
                    e.printStackTrace();
                } catch (NetworkOnMainThreadException e) {
                    AlertDialog("Error", "Ошибка подключения");
                    e.printStackTrace();
                }

                if (ressponse != null) {
                    Currencies currency = new Gson().fromJson(ressponse.text(), Currencies.class);

                    for (Сurrency item : currency.Valute.values()) {
                        currs.put(item.Name, item);
                    }

                } else body = "Error!";

                loafSpinnerItem();
            }
        });
    }

    public void Consider() {
        EditText from = findViewById(R.id.editText);

        Spinner spinner = findViewById(R.id.spinner);

        Сurrency currency =  currs.get(spinner.getSelectedItem());

        TextView tv = findViewById(R.id.text2);

        if (from.getText().length() > 0) {
            float fromValue = Float.parseFloat(String.valueOf(from.getText()));

            float result = currency.Value * fromValue;

            tv.setText(String.valueOf(result));
        } else tv.setText("Nan");
    }
}