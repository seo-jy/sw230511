package com.example.sw221103;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ResultClicked extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_clicked);


        String imageUrl = "";

        Bundle extras = getIntent().getExtras();

        imageUrl = extras.getString("imageUrl");


        TextView textView = (TextView) findViewById(R.id.textView_result1);

        String str = imageUrl;
        textView.setText(str);

    }
}
