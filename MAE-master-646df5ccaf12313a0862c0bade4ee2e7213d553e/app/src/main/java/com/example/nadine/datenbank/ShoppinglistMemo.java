package com.example.nadine.datenbank;

/**
 * Created by Cai on 01.02.18.
 */

public class ShoppinglistMemo {
    private long id;
    private String name;
    private String imagePath;


    public ShoppinglistMemo( long id,String name, String imagePath ){
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "ShoppinglistMemo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


}

