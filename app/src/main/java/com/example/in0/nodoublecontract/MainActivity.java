package com.example.in0.nodoublecontract;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    String session_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this.getIntent());
        session_id = intent.getStringExtra("id");
        TextView tvId = findViewById(R.id.id);
        tvId.setText(session_id);
    }

    public void runSellerMode(View view) {
        Intent intent = new Intent(this,FragmentSearchAddress.class);
        intent.putExtra("category_value", "1");
        intent.putExtra("id", session_id);
        startActivity(intent);
    }

    public void runBuyerMode(View view) {
        Intent intent = new Intent(this,FragmentSearchAddress.class);
        intent.putExtra("category_value", "0");
        intent.putExtra("id", session_id);
        startActivity(intent);
    }
}
