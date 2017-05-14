package com.paulgof.soundwave;


import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartPage extends AppCompatActivity {

    Button inOnline;
    Button inOffline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        inOnline = (Button) findViewById(R.id.in_online_button);
        inOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartPage.this, OnlineMod.class);
                startActivity(intent);
            }
        });
        inOffline = (Button) findViewById(R.id.in_offline_button);
        inOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartPage.this, OfflineMod.class);
                startActivity(intent);
            }
        });
    }
}
