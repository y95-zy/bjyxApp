package com.bjyxszd.bjyxApp;


//使用HttpClient组件访问网络以及获取 网页内容的方法。

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main2Activity extends Activity implements OnClickListener{
    ListView listview;
    private String TAG = "webdatashow";
    private Button jiazai;
    private TextView webDataShow;
    private String pediyUrl = "http://www.stdu.edu.cn/";
    Handler handler;
    List<Map<String, Object>> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        jiazai = (Button)findViewById(R.id.button1);
        webDataShow = (TextView)findViewById(R.id.webDataShow1);
        jiazai.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        handler = getHandler();
        ThreadStart();
    }

    /*httpClientWebData()
     * 返回值类型:String
     * 抓取网页的document
     *
     * */
    protected String httpClientWebData() {
        String content = null;
        DefaultHttpClient httpClinet = new DefaultHttpClient(); //创建一个HttpClient
        HttpGet httpGet = new HttpGet(pediyUrl);//创建一个GET请求
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        try {
            content = httpClinet.execute(httpGet, responseHandler);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }//发送GET请求，并响应内容
        return content;
    }

    /*getDate()
     * 返回值类型:List<Map<String, Object>>
     * 提取网页document的所需内容
     *
     * */
    private List<Map<String, Object>> getDate() {
        String httpstring = httpClientWebData();
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Pattern p = Pattern.compile("latestnewsnews  |  </span>\\s*<span style="white-space:pre">	</span><a href=\"(.*?)\" title=\"(.*?)\"");//正则表达式
        Matcher m = p.matcher(httpstring);
        while (m.find()) {
            MatchResult mr=m.toMatchResult();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", mr.group(1));
            map.put("url", mr.group(2));
            result.add(map);
        }
        return result;
    }

    /*ThreadStart()
     * 开辟新线程
     *
     * */
    private void ThreadStart() {
        new Thread() {
            public void run() {
                Message msg = new Message();
                try {
                    data = getDate();
                    msg.what = data.size();
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = -1;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }
    private Handler getHandler() {
        return new Handler(){
            public void handleMessage(Message msg) {
                if (msg.what < 0) {
                    Toast.makeText(Main2Activity.this, "数据获取失败", Toast.LENGTH_SHORT).show();
                }else {
                    initListview();
                }
            }
        };
    }

    /*initListview()
     * 在listview中显示数据
     *
     * */
    private void initListview() {
        Toast.makeText(getApplicationContext(), "doing......", Toast.LENGTH_SHORT).show();
        ListView listView = (ListView) findViewById(R.id.listView1);
        listView.setAdapter(new SimpleAdapter(this,data , android.R.layout.simple_list_item_2,
                new String[] { "title","href" }, new int[] {
                android.R.id.text1,android.R.id.text2
        }));

    }

}