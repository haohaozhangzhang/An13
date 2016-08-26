package com.example.proj.an13;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserInfoActivity extends AppCompatActivity {
    static protected final int IMAGE_REQUEST_CODE=1 ;
    //static protected final int PICK_CONTACT_REQUEST=2 ;
    private String picturePath = "";
    User newUser ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Intent intent=getIntent() ;
        final User user=(User) intent.getSerializableExtra("key") ;
        newUser=user ;
        if(user!=null)
        {
            ((Button)findViewById(R.id.buttonadd)).setVisibility(View.INVISIBLE);
        }
        else
        {
            ((Button)findViewById(R.id.buttondelete)).setVisibility(View.INVISIBLE);
            ((Button)findViewById(R.id.buttonset)).setVisibility(View.INVISIBLE);
        }
        if(user!=null)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    InputStream is=MyUtils.requestByUtils(MainActivity.HOST+"/user/"+user.getId(),"GET",null) ;
                    Gson gson=new Gson() ;
                    final User user=gson.fromJson(new InputStreamReader(is),User.class) ;
                    InputStream iis=MyUtils.requestByUtils(MainActivity.HOST+user.getPhoto(),"GET",null) ;
                    final Bitmap bitmap= BitmapFactory.decodeStream(iis) ;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((EditText)findViewById(R.id.infousername)).setText(user.getName().toString());
                            ((EditText)findViewById(R.id.infouserpassword)).setText(user.getPassword().toString());
                            ((EditText)findViewById(R.id.infousertel)).setText(user.getTel().toString());
                            ((ImageView)findViewById(R.id.infouserimage)).setImageBitmap(bitmap);
                        }
                    });

                }
            }).start();

        }
    }
    public void userDelete(View view)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream iss= MyUtils.requestByUtils(MainActivity.HOST+"/user/"+newUser.getId() ,"DELETE",null) ;
                Gson gson=new Gson() ;
                final MyResult result=gson.fromJson(new InputStreamReader(iss),MyResult.class) ;
                Log.v("result",result.getStatus()) ;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(result.getStatus().compareTo("success")==0)
                        {
                            Toast ts=Toast.makeText(getApplicationContext(),"删除成功",Toast.LENGTH_SHORT) ;
                            ts.show();
                            finish();
                        }
                        else
                        {
                            Toast ts=Toast.makeText(getApplicationContext(),"删除失败",Toast.LENGTH_SHORT) ;
                            ts.show();
                            finish();
                        }
                    }
                });
            }
        }).start();
    }

    public void userSet(View view)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream is=MyUtils.requestByUtils(MainActivity.HOST+"/user/"+newUser.getId(),"GET",null) ;
                Gson gson=new Gson() ;
                User user=gson.fromJson(new InputStreamReader(is),User.class) ;
                user.setName(((EditText)findViewById(R.id.infousername)).getText().toString());
                user.setPassword(((EditText)findViewById(R.id.infouserpassword)).getText().toString());
                user.setTel(((EditText)findViewById(R.id.infousertel)).getText().toString());
                InputStream iis=MyUtils.requestByUtils(MainActivity.HOST+"/user/"+newUser.getId(),"PUT",gson.toJson(user)) ;
                final MyResult result=gson.fromJson(new InputStreamReader(iis),MyResult.class) ;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(result.getStatus().compareTo("success")==0)
                        {
                            Toast ts=Toast.makeText(getApplicationContext(),"修改成功",Toast.LENGTH_SHORT) ;
                            ts.show();
                            finish();
                        }
                        else
                        {
                            Toast ts=Toast.makeText(getApplicationContext(),"修改失败",Toast.LENGTH_SHORT) ;
                            ts.show();
                            finish();
                        }
                    }
                });
            }
        }).start();

    }
    /*public void editAdd(View view)
    {
        new  Thread(new Runnable() {
            @Override
            public void run() {
                User user=new User() ;
                user.setName(((EditText)findViewById(R.id.infousername)).getText().toString());
                user.setPassword(((EditText)findViewById(R.id.infouserpassword)).getText().toString());
                user.setTel(((EditText)findViewById(R.id.infousertel)).getText().toString());
                Gson gson=new Gson() ;
                InputStream is=MyUtils.requestByUtils(MainActivity.HOST+"/user","POST",gson.toJson(user)) ;
                final MyResult result=gson.fromJson(new InputStreamReader(is),MyResult.class) ;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(result.getStatus().compareTo("success")==0)
                        {
                            Toast ts=Toast.makeText(getApplicationContext(),"增加成功",Toast.LENGTH_SHORT) ;
                            ts.show();
                            finish();
                        }
                        else
                        {
                            {
                                Toast ts=Toast.makeText(getApplicationContext(),"增加失败",Toast.LENGTH_SHORT) ;
                                ts.show();
                                finish();
                            }
                        }
                    }
                });
            }
        }).start();


    }*/
    public void myAddPicture(View view)
    {
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI) ;
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent date) {
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && date != null) {
            Uri selectedImage = date.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            Log.v("picturePath", picturePath);
            cursor.close();
            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            ((ImageView) findViewById(R.id.infouserimage)).setImageBitmap(bitmap);

        }
    }
    public void editAdd(View view)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

//                    String charset = "UTF-8";
//                    String requestURL = HOST + "/user";
//
//                    MultipartUtility multipart = new MultipartUtility(requestURL, charset);
//                    multipart.addFormField("name", "qianyongfeng");
//                    multipart.addFormField("password", "123");
//                    multipart.addFormField("tel", "123");
//                    multipart.addFilePart("testFile", new File(picturePath));
//                    List<String> response = multipart.finish();
//                    Log.v("response", response.get(0));


                    OkHttpClient client = new OkHttpClient();
                    File file = new File(picturePath);
                    MultipartBody body =  new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("photo", file.getName(),
                                    RequestBody.create(MediaType.parse("text/plain"), file))
                            .addFormDataPart("name", ((EditText)findViewById(R.id.infousername)).getText().toString())
                            .addFormDataPart("password", ((EditText)findViewById(R.id.infouserpassword)).getText().toString())
                            .addFormDataPart("tel", ((EditText)findViewById(R.id.infousertel)).getText().toString())
                            .build();
                    Request request = new Request.Builder().url(MainActivity.HOST + "/user").post(body).build();
                    Response response = client.newCall(request).execute();
                    Log.v("return body", response.body().string());

                    if(response.code() == HttpURLConnection.HTTP_OK) {
                        Log.v("photo", "uploaded");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast ts=Toast.makeText(getApplicationContext(),"添加成功",Toast.LENGTH_SHORT) ;
                                ts.show();
                                finish();
                            }
                        });
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
