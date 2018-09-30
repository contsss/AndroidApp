package com.example.in0.nodoublecontract;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class BuyerSearchAddress extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_search_address);
    }

    public void searchAddress(View view) {
        Intent intent = new Intent(this, DaumWebViewActivity.class);
        TextView textView = (TextView) findViewById(R.id.category);
        String category_value = textView.getText().toString();
        intent.putExtra(category_value, "1"); // 0:구매자 모드, 1:판매자 모드
        startActivity(intent);
    }
}
