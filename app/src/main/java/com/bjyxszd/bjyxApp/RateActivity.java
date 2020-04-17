package com.bjyxszd.bjyxApp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class RateActivity extends AppCompatActivity {

    EditText rmb;
    TextView show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        rmb = (EditText)findViewById(R.id.rmb);
        show = (TextView)findViewById(R.id.showOut);
    }

    public void onClick(View v){
        String str = rmb.getText().toString();
        float r=0;
        float val=0;

        if(str.length()>0){
            r = Float.parseFloat(str);
        }else{
            String errorMsg = getResources().getString(R.string.msg_error_input2);
            Toast.makeText(this,errorMsg,Toast.LENGTH_LONG).show();
        }

        if(v.getId()==R.id.r2dollar){
            val = r * (1/6.7f);
        }else if(v.getId()==R.id.r2euro){
            val = r * (1/11f);
        }else{
            val = r * 500f;
        }
        show.setText(String.valueOf(new DecimalFormat("#.00").format(val)));
    }

}
