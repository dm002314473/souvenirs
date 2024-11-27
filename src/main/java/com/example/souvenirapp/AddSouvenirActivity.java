package com.example.souvenirapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddSouvenirActivity extends AppCompatActivity {

    private EditText etName, etPrice, etCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_souvenir);

        etName = findViewById(R.id.etName);
        etPrice = findViewById(R.id.etPrice);
        etCount = findViewById(R.id.etCount);
        Button btnSaveSouvenir = findViewById(R.id.btnSaveSouvenir);

        btnSaveSouvenir.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String priceStr = etPrice.getText().toString();
            String countStr = etCount.getText().toString();

            if (name.isEmpty() || priceStr.isEmpty() || countStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);
            int count = Integer.parseInt(countStr);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("name", name);
            resultIntent.putExtra("price", price);
            resultIntent.putExtra("count", count);

            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}