package com.example.proj.an13;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mic on 2016/8/24.
 */
public class MyUtils {
    public static InputStream requestByUtils(String Urlstr , String Method , String params)
    {
        if(Method==null)
        {
            Method="GET" ;
        }
        try {
            URL url=new URL(Urlstr) ;
            HttpURLConnection conn=(HttpURLConnection)url.openConnection() ;
            conn.setRequestMethod(Method);
            if(!TextUtils.isEmpty(params))
            {
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                OutputStream os=conn.getOutputStream() ;
                OutputStreamWriter wr=new OutputStreamWriter(os) ;
                wr.write(params);
                wr.flush();
                wr.close();
                os.close();
            }
            if(conn.getResponseCode()==200)
            {
                InputStream is=conn.getInputStream() ;
                return is ;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null ;
    }
}
