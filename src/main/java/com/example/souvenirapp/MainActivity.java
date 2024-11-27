package com.example.souvenirapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private SouvenirDatabaseHelper dbHelper;
    private ArrayList<Souvenir> souvenirs;
    private SouvenirAdapter adapter; // Custom Adapter for ListView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new SouvenirDatabaseHelper(this);
        souvenirs = dbHelper.getAllSouvenirs();

        ListView lvSouvenirs = findViewById(R.id.lvSouvenirs);
        adapter = new SouvenirAdapter(this, souvenirs, dbHelper); // Create your own custom adapter
        lvSouvenirs.setAdapter(adapter);

        sortSouvenirsAlphabetically();

        SearchView svSearch = findViewById(R.id.svSearch);
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterSouvenirs(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterSouvenirs(newText);
                return false;
            }
        });

        findViewById(R.id.btnAddSouvenir).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddSouvenirActivity.class);
            startActivityForResult(intent, 1);
        });

        // GestureDetector for swipe-to-delete functionality on ListView
        lvSouvenirs.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    if (e1.getX() - e2.getX() > 100) { // Swipe left
                        int position = lvSouvenirs.pointToPosition((int) e1.getX(), (int) e1.getY());
                        if (position != ListView.INVALID_POSITION) {
                            Souvenir souvenir = souvenirs.get(position);
                            dbHelper.deleteSouvenir(souvenir.getId());
                            souvenirs.remove(position);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this, "Souvenir removed!", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                    return false;
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        lvSouvenirs.setOnItemClickListener((adapterView, view, position, id) -> {
            Souvenir souvenir = souvenirs.get(position);
            dbHelper.deleteSouvenir(souvenir.getId());
            souvenirs.remove(position);
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Souvenir removed!", Toast.LENGTH_SHORT).show();
        });

        recalculateTotalRevenue();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String name = data.getStringExtra("name");
            double price = data.getDoubleExtra("price", 0.0);
            int ordered = data.getIntExtra("ordered", 0);

            Souvenir souvenir = new Souvenir(name, price, ordered, 0);
            dbHelper.addSouvenir(souvenir);
            souvenirs.add(souvenir);
            adapter.notifyDataSetChanged();
        }
    }

    private void filterSouvenirs(String query) {
        ArrayList<Souvenir> filteredSouvenirs = new ArrayList<>();

        if (query.isEmpty()) {
            filteredSouvenirs.addAll(dbHelper.getAllSouvenirs());
            Log.d("SouvenirApp", "Query is empty. Showing all items." + souvenirs);
        } else {
            for (Souvenir souvenir : souvenirs) {
                if (souvenir.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredSouvenirs.add(souvenir);
                }
            }
            Log.d("SouvenirApp", "Filtered items based on query: " + query);
        }

        // Sort alphabetically by name
        sortSouvenirsAlphabetically(filteredSouvenirs);

        adapter.clear();
        adapter.addAll(filteredSouvenirs);
        adapter.notifyDataSetChanged();
    }

    private void sortSouvenirsAlphabetically() {
        Collections.sort(souvenirs, (souvenir1, souvenir2) -> souvenir1.getName().compareToIgnoreCase(souvenir2.getName()));
    }

    private void sortSouvenirsAlphabetically(ArrayList<Souvenir> souvenirsToSort) {
        Collections.sort(souvenirsToSort, (souvenir1, souvenir2) -> souvenir1.getName().compareToIgnoreCase(souvenir2.getName()));
    }

    public void updateSoldPieces(int position, int newSoldPieces) {
        Souvenir souvenir = souvenirs.get(position);
        souvenir.setSoldPieces(newSoldPieces);

        dbHelper.updateSouvenir(souvenir);

        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Sold pieces updated!", Toast.LENGTH_SHORT);

        recalculateTotalRevenue();
    }

    public void recalculateTotalRevenue() {
        double totalRevenue = 0.0;
        for (Souvenir souvenir : souvenirs) {
            totalRevenue += souvenir.getPrice() * souvenir.getSoldPieces();
        }

        TextView tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvTotalRevenue.setText("Total revenue: €" + totalRevenue);
    }
}
