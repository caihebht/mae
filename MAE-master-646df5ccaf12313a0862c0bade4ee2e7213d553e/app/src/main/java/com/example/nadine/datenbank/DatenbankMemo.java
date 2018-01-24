package com.example.nadine.datenbank;

/**
 * Created by nadine on 18.12.17.
 */

public class DatenbankMemo {

    private String product;
    private int quantity;
    private long id;
    private byte[] image;


    public DatenbankMemo(String product, int quantity, long id, byte[] image) {
        this.product = product;
        this.quantity = quantity;
        this.id = id;
        this.image = image;
    }


    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte[] getImage (){
        return image;
    }

    @Override
    public String toString() {
        String output = quantity + " x " + product;

        return output;
    }
}
