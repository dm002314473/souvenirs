package com.example.souvenirapp;

public class Souvenir {
    private int id;
    private String name;
    private double price;
    private int orderedPieces;
    private int soldPieces;

    public Souvenir(String name, double price, int orderedPieces, int soldPieces) {
        this.name = name;
        this.price = price;
        this.orderedPieces = orderedPieces;
        this.soldPieces = soldPieces;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getOrderedPieces() {
        return orderedPieces;
    }

    public int getSoldPieces() {
        return soldPieces;
    }

    public void setSoldPieces(int soldPieces) {
        this.soldPieces = soldPieces;
    }

    public double getIndividualRevenue() {
        return soldPieces * price;
    }
}
