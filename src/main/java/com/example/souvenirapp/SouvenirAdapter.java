package com.example.souvenirapp;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;

public class SouvenirAdapter extends ArrayAdapter<Souvenir> {

    private MainActivity mainActivity;
    private SouvenirDatabaseHelper dbHelper;
    private ArrayList<Souvenir> souvenirs;

    public SouvenirAdapter(Context context, ArrayList<Souvenir> souvenirs, SouvenirDatabaseHelper dbHelper) {
        super(context, 0, souvenirs);
        this.mainActivity = (MainActivity) context;
        this.dbHelper = dbHelper;
        this.souvenirs = souvenirs;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Souvenir souvenir = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.souvenir_item, parent, false);
        }

        // Name and price
        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvPrice = convertView.findViewById(R.id.tvPrice);

        // Ordered and sold pieces
        TextView tvOrderedPieces = convertView.findViewById(R.id.tvOrderedPieces);
        EditText etSoldPieces = convertView.findViewById(R.id.etSoldPieces);

        // Revenue
        TextView tvRevenue = convertView.findViewById(R.id.tvRevenue);

        // Save button
        Button btnSaveSoldPieces = convertView.findViewById(R.id.btnSaveSoldPieces);

        Button btnDeleteSouvenir = convertView.findViewById(R.id.btnDeleteSouvenir);
        btnDeleteSouvenir.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this souvenir?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Delete souvenir from the database and list
                        dbHelper.deleteSouvenir(souvenir.getId());
                        souvenirs.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(getContext(), "Souvenir removed!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // Set values
        tvName.setText(souvenir.getName());
        tvPrice.setText(String.format("$%.2f", souvenir.getPrice()));
        tvOrderedPieces.setText(String.format("Ordered: %d", souvenir.getOrderedPieces()));
        etSoldPieces.setText(String.valueOf(souvenir.getSoldPieces()));
        tvRevenue.setText(String.format("$%.2f", souvenir.getIndividualRevenue()));

        // Initially hide the Save button
        btnSaveSoldPieces.setVisibility(View.GONE);

        // Listener for sold pieces input
        etSoldPieces.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Get the value entered by the user
                String enteredText = s.toString();
                int soldPieces = enteredText.isEmpty() ? 0 : Integer.parseInt(enteredText);
                souvenir.setSoldPieces(soldPieces);

                // Update the individual revenue displayed
                tvRevenue.setText(String.format("$%.2f", souvenir.getIndividualRevenue()));

                // Show the Save button if a value is entered
                if (!enteredText.isEmpty()) {
                    btnSaveSoldPieces.setVisibility(View.VISIBLE);
                } else {
                    btnSaveSoldPieces.setVisibility(View.GONE);
                }
            }
        });

        // Save button click listener
        btnSaveSoldPieces.setOnClickListener(v -> {
            int soldPieces = Integer.parseInt(etSoldPieces.getText().toString());
            souvenir.setSoldPieces(soldPieces);
            mainActivity.updateSoldPieces(position, soldPieces); // Update in MainActivity
            mainActivity.recalculateTotalRevenue(); // Recalculate total revenue
            btnSaveSoldPieces.setVisibility(View.GONE); // Hide Save button after saving
            Toast.makeText(getContext(), "Sold pieces saved!", Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }
}
