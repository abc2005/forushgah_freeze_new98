package com.persiandesigners.freeze;

/**
 * Created by navid on 8/31/2015.
 */
public class FeedItem {
    private String name;
    private String id;
    private String price1;
    private String img;
    private String price2;
    private String num;
    private String maxCount;
    private String tedad;
    private String majazi ;
    private String vahed;

    Boolean forushVije;
    int catId;
    int omde_num;
    int omde_price;
    String shopId;
    Boolean isOpen;//baraye chand forushande

    public String getVahed() {
        return vahed;
    }

    public void setVahed(String vahed) {
        this.vahed = vahed;
    }

    public Boolean getOpen() {
        return isOpen;
    }

    public void setOpen(Boolean open) {
        isOpen = open;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public int getOmde_num() {
        return omde_num;
    }

    public void setOmde_num(int omde_num) {
        this.omde_num = omde_num;
    }

    public int getOmde_price() {
        return omde_price;
    }

    public void setOmde_price(int omde_price) {
        this.omde_price = omde_price;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public Boolean getForushVije() {
        return forushVije;
    }

    public void setForushVije(Boolean forushVije) {
        this.forushVije = forushVije;
    }

    public String getMajazi() {
        return majazi;
    }

    public void setMajazi(String majazi) {
        this.majazi = majazi;
    }

    public String getnum() {
        return num;
    }

    public void setnum(String num) {
        this.num = num;
    }

    public String getimg() {
        return img;
    }

    public void setimg(String img) {
        this.img = img;
    }

    public String getprice() {
        return price1;
    }

    public void setPrice(String price1) {
        this.price1 = price1;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public String getid() {
        return id;
    }

    public void setid(String id) {
        this.id = id;
    }

    public String getprice2() {
        return price2;
    }

    public void setprice2(String price2) {
        this.price2 = price2;
    }

    public String getTedad() {
        return tedad;
    }

    public void setTedad(String tedad) {
        this.tedad = tedad;
    }
}

