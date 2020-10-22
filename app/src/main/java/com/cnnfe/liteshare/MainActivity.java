package com.cnnfe.liteshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button send, receive;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        send = (Button) findViewById(R.id.send);
        receive = (Button) findViewById(R.id.receive);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Get Set Share...");
        toolbar.setLogo(R.drawable.ic_baseline_mobile_screen_share_24);
        setSupportActionBar(toolbar);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FileActivity.class));
            }
        });
    }
}