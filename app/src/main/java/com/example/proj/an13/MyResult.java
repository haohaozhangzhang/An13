package com.example.proj.an13;

/**
 * Created by mic on 2016/8/24.
 */
public class MyResult {
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String status ;
    MyResult()
    {

    }
    MyResult(String status)
    {
        this.status=status ;
    }

}
