package com.example.glg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class SelectLang extends Activity {
    public String sourceLang = "en";
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_lang);
        Button button = (Button) findViewById(R.id.go_to_list);

        spinner = (Spinner) findViewById(R.id.spinner);
        final ArrayList<String> list = new ArrayList<>();
        list.add("미국");
        list.add("일본");
        list.add("중국");

        ArrayAdapter spinnerAdapter;
        spinnerAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, list);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String a = spinner.getItemAtPosition(position).toString();
                if (a == "미국")
                    sourceLang = "en";
                else if (a == "일본")
                    sourceLang = "ja";
                else
                    sourceLang = "zh-CN";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a = sourceLang;
                Intent intent = new Intent(SelectLang.this, MainActivity.class);
                intent.putExtra("key", a);
                startActivity(intent);
            }
        });
    }
}
