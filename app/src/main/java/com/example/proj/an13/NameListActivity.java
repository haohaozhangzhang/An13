package com.example.proj.an13;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class NameListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

    }
    protected void onStart()
    {
        super.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
               InputStream is= MyUtils.requestByUtils(MainActivity.HOST+"/users","GET",null) ;
                BufferedReader reader=new BufferedReader(new InputStreamReader(is)) ;
                String line ;
                StringBuilder sb=new StringBuilder() ;
                try {
                    while ((line=reader.readLine())!=null)
                    {
                        sb.append(line) ;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                String connect=sb.toString() ;
                final ArrayList<String>nameArr=new ArrayList<String>() ;
                final ArrayList<User>userArr=new ArrayList<User>() ;
                try {
                    JSONArray arr=new JSONArray(connect) ;
                    Gson gson=new Gson() ;
                    for(int i=0 ;i<arr.length() ;i++)
                    {
                        JSONObject object=arr.getJSONObject(i) ;
                        nameArr.add(object.getString("name")) ;
                        userArr.add(gson.fromJson(object.toString(),User.class)) ;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ListView listView= (ListView) findViewById(R.id.usernamelist);
                            ArrayAdapter<String>adapter=new ArrayAdapter<String>(NameListActivity.this,android.R.layout.simple_list_item_1,nameArr) ;
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent=new Intent(NameListActivity.this,UserInfoActivity.class) ;
                                    intent.putExtra("key",userArr.get(position)) ;
                                    startActivity(intent);
                                }
                            });
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public void addUser(View view)
    {
        Intent intent=new Intent(NameListActivity.this,UserInfoActivity.class) ;
        startActivity(intent);
    }

}
