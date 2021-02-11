package com.jumia.egyptblackfriday;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import static java.lang.Thread.sleep;

public class SplashScreen extends AppCompatActivity {
private Intent it;
private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        it=new Intent(SplashScreen.this,MainActivity.class);
        sharedPreferences=getSharedPreferences("privacy_poliicy",MODE_PRIVATE);
       boolean isfound= sharedPreferences.getBoolean("p_code",false);
        if(!isfound)
        {
            //running for first time
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putBoolean("p_code",true);
            it.putExtra("p_code",true);
            editor.commit();
        }
        else{
            it.putExtra("p_code",false);
        }
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(6600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {

                    it.putExtra("ads_shown",false);
                    startActivity(it);
                    SplashScreen.this.finish();
                }
            }
        });
        thread.start();
    }
}