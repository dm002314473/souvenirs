package com.example.souvenirapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.souvenirapp.Souvenir;

import java.util.ArrayList;
import java.util.List;

public class SouvenirDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "souvenirs.db";
    private static final int DATABASE_VERSION = 1;

    // Table and Column Names
    private static final String TABLE_SOUVENIRS = "souvenirs";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_ORDERED = "ordered_pieces";
    private static final String COLUMN_SOLD = "sold_pieces";

    public SouvenirDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_SOUVENIRS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_PRICE + " REAL, " +
                COLUMN_ORDERED + " INTEGER, " +
                COLUMN_SOLD + " INTEGER)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SOUVENIRS);
        onCreate(db);
    }

    // CRUD Operations

    // Add Souvenir
    public void addSouvenir(Souvenir souvenir) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, souvenir.getName());
        values.put(COLUMN_PRICE, souvenir.getPrice());
        values.put(COLUMN_ORDERED, souvenir.getOrderedPieces());
        values.put(COLUMN_SOLD, souvenir.getSoldPieces());
        db.insert(TABLE_SOUVENIRS, null, values);
        db.close();
    }

    // Get All Souvenirs
    public ArrayList<Souvenir> getAllSouvenirs() {
        ArrayList<Souvenir> souvenirs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SOUVENIRS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Souvenir souvenir = new Souvenir(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDERED)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SOLD))
                );
                souvenir.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                souvenirs.add(souvenir);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return souvenirs;
    }


    // Update Souvenir
    public void updateSouvenir(Souvenir souvenir) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, souvenir.getName());
        values.put(COLUMN_PRICE, souvenir.getPrice());
        values.put(COLUMN_ORDERED, souvenir.getOrderedPieces());
        values.put(COLUMN_SOLD, souvenir.getSoldPieces());
        db.update(TABLE_SOUVENIRS, values, COLUMN_ID + "=?", new String[]{String.valueOf(souvenir.getId())});
        db.close();
    }

    // Delete Souvenir
    public void deleteSouvenir(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SOUVENIRS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
}
