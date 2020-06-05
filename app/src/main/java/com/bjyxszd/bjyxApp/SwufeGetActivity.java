package com.bjyxszd.bjyxApp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SwufeGetActivity extends AppCompatActivity {

    float rate = 0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swufe_get);
        String title = getIntent().getStringExtra("title");
        rate = getIntent().getFloatExtra("rate",0f);

        ((TextView)findViewById(R.id.get_item)).setText(title);


    }
}
