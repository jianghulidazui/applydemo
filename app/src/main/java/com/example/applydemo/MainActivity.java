package com.example.applydemo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("--->", "===================");
        Hello hello = new Hello();
        Log.e("--->", "===================");
        Log.e("--->", "hello father is : " + hello.getClass().getSuperclass().getSimpleName());
    }

    @NonNull
    @Override
    public AppCompatDelegate getDelegate() {
        return super.getDelegate();
    }

}
