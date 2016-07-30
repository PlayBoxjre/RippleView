package com.example.pc.ripplelayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.pc.ripplelayout.com.aaron.ripple.RippleLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((RippleLayout) findViewById(R.id.ripple)).startRippleAniamtion();

    }
}
