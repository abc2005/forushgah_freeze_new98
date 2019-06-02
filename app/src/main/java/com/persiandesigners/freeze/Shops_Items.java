package com.persiandesigners.freeze;

/**
 * Created by Navid on 4/18/2018.
 */

public class Shops_Items {

    String id;
    int isOpen;
    private String name;
    private String about;
    private String logo;
    String catId;
    String minimum_order_amount;
    String zaman_tahvil ;

    public int getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(int isOpen) {
        this.isOpen = isOpen;
    }


    public String getMinimum_order_amount() {
        return minimum_order_amount;
    }

    public void setMinimum_order_amount(String minimum_order_amount) {
        this.minimum_order_amount = minimum_order_amount;
    }

    public String getZaman_tahvil() {
        return zaman_tahvil;
    }

    public void setZaman_tahvil(String zaman_tahvil) {
        this.zaman_tahvil = zaman_tahvil;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public String getabout() {
        return about;
    }

    public void setabout(String about) {
        this.about = about;
    }

    public String getlogo() {
        return logo;
    }

    public void setlogo(String logo) {
        this.logo = logo;
    }
}