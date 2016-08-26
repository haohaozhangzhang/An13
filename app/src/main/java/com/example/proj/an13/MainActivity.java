package com.example.proj.an13;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    static public final String HOST="http://192.168.0.102:3000" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    public void userLogin(View view)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                User user=new User() ;
                user.setName(((EditText)findViewById(R.id.user)).getText().toString());
                user.setPassword(((EditText)findViewById(R.id.password)).getText().toString());
                Gson gson=new Gson() ;
                InputStream is=MyUtils.requestByUtils(HOST+"/login","POST",gson.toJson(user)) ;
                final User finduser=gson.fromJson(new InputStreamReader(is),User.class) ;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(finduser.getId()!=null)
                          {
                            Intent intent=new Intent(MainActivity.this,NameListActivity.class) ;
                            startActivity(intent);
                        }
                        else
                        {
                            Toast ts=Toast.makeText(getApplicationContext(),"登录失败",Toast.LENGTH_SHORT) ;
                            ts.show();
                        }
                    }
                });
            }
        }).start();
    }
}
