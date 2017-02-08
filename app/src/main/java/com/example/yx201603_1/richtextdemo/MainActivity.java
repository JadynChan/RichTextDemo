package com.example.yx201603_1.richtextdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        final RichSrcollView richSrcollVIew = (RichSrcollView) findViewById(R.id.scrollview);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RichImageView richImageView = new RichImageView(MainActivity.this);
                richImageView.setEditImageView("");
                richSrcollVIew.insertEditView(richImageView);
            }
        });
    }
}
