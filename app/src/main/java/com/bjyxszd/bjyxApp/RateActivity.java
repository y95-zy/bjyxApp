package com.bjyxszd.bjyxApp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DecimalFormat;

public class RateActivity extends AppCompatActivity implements  Runnable{

    private final String TAG = "Rate";
    private float dollarrate = 0.1f;
    private float eurorate = 0.1f;
    private float wonrate = 0.1f;
    Handler handler;


    EditText rmb;
    TextView show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        rmb = (EditText)findViewById(R.id.rmb);
        show = (TextView)findViewById(R.id.showOut);

        //获取SP里保存的数据
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        dollarrate = sharedPreferences.getFloat("dollar_rate",0.0f);
        eurorate = sharedPreferences.getFloat("euro_rate",0.0f);
        wonrate = sharedPreferences.getFloat("won_rate",0.0f);
        Log.i(TAG, "onCreate: dollarrate=" + dollarrate);
        Log.i(TAG, "onCreate: eurorate=" + eurorate);
        Log.i(TAG, "onCreate: wonrate=" + wonrate);

        //开启子线程
        Thread t = new Thread(this);
        t.start();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what == 5){
                    Bundle bdl = (Bundle) msg.obj;
                    dollarrate = bdl.getFloat("dollar-rate");
                    eurorate = bdl.getFloat("euro-rate");
                    wonrate = bdl.getFloat("won-rate");

                    Log.i(TAG, "handleMessage: dollarrate=" + dollarrate);
                    Log.i(TAG, "handleMessage: dollarrate=" +eurorate);
                    Log.i(TAG, "handleMessage: dollarrate=" +wonrate);

                    Toast.makeText(RateActivity.this,"汇率已更新",Toast.LENGTH_LONG).show();

                }
                super.handleMessage(msg);
            }
        };


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
            val = r * dollarrate;
        }else if(v.getId()==R.id.r2euro){
            val = r * eurorate;
        }else{
            val = r * wonrate;
        }
        show.setText(String.valueOf(new DecimalFormat("#.00").format(val)));
    }

    public void openOne(View v){
        openConfig();
    }

    private void openConfig() {
        Intent config = new Intent(this, ConfigActivity.class);

        config.putExtra("dollar_rate_key", dollarrate);
        config.putExtra("euro_rate_key", eurorate);
        config.putExtra("won_rate_key", wonrate);

        startActivityForResult(config, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.rate_menu){
            openConfig();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode==1 && resultCode==2){
            Bundle bundle = data.getExtras();
            dollarrate = bundle.getFloat("key_dollar",0.1f);
            eurorate = bundle.getFloat("key_euro",0.1f);
            wonrate = bundle.getFloat("key_won",0.1f);



            SharedPreferences sharedPreferences = getSharedPreferences("myrate",Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("dollar_rate",dollarrate);
            editor.putFloat("euro_rate",eurorate);
            editor.putFloat("won_rate",wonrate);
            editor.commit();
            Log.i(TAG, "onActivityResult:  数据已保存。。。");

        }

        super.onActivityResult(requestCode,resultCode,data);
    }


    @Override
    public void run() {
        Log.i(TAG, "run: run()....");
        try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        //用于保存汇率
        Bundle bundle = new Bundle();

        //获取msg
        Message msg = handler.obtainMessage(5);
        msg.obj = "Hello ...";
        handler.sendMessage(msg);



        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.usd-cny.com/icbc.htm").get();
            Log.i(TAG, "run: "+doc.title());
            Elements tables = doc.getElementsByTag("table");
            Log.i(TAG, "run: table6=" + tables);

            Element table6 = tables.get(5);
            Elements tds = table6.getElementsByTag("td");
            for(int i=0;i<tds.size();i+=8){
                Element td1 = tds.get(i);
                Element td2 = tds.get(i+5);
                Log.i(TAG, "run: " + td1.text() + "==>" + td2.text());

                if("美元".equals(td1.text())){
                    bundle.putFloat("dollar-rate",100f/Float.parseFloat(td2.text()));
                }else if("欧元".equals(td1.text())){
                    bundle.putFloat("euro-rate",100f/Float.parseFloat(td2.text()));
                }else if("韩国元".equals(td1.text())){
                    bundle.putFloat("won-rate",100f/Float.parseFloat(td2.text()));
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        //bundle保存获取的汇率
        //获取Msg对象，用于返回主线程





    }


    private String inputStream2String(InputStream inputStream) throws IOException{

        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream,"gb2312");
        for(;;){
            int rsz = in.read(buffer,0,buffer.length);
            if(rsz < 0){
                break;
            }
            out.append(buffer,0,rsz);
        }
        return out.toString();
    }
}
