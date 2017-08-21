package com.whatsonline.androidmultitouch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Splash extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash);

    }
    public void proceed(View v){
        Intent intent=new Intent(Splash.this, MainActivity.class);
        startActivity(intent);
    }
}
