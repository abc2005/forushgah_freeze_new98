package com.persiandesigners.freeze.Util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler {
    private final static String TABLE_NAME = "persian-designers_ir";
    private final static int VERSION =9;
    private Context context;
    private SQLiteDatabase myDB;
    private OpenHelper helper;
    public static final String[] SABADKEY = new String[]{"_id", "name", "img", "price", "num", "max_count", "price2", "property", "catId", "omde_num", "omde_price", "shopId"};
    public static final String[] LIKEKEY = new String[]{"_id", "price", "name", "img"};
    public static final String[] CATKEYS = new String[]{"_id", "name", "img", "parrent_id", "orders"};

    public static final String[] CITY = new String[]{"_id", "name", "province"};
    public static final String[] OSTAN = new String[]{"_id", "name"};
    public static final String[] OFFLINE = new String[]{"_id", "part", "value", "part_id"};

    public DatabaseHandler(Context context) {
        this.context = context;
    }

    public class OpenHelper extends SQLiteOpenHelper {
        Context context;

        public OpenHelper(Context context) {
            super(context, TABLE_NAME, null, VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // Category table create query
            String CREATE_CATEGORIES_TABLE;
            CREATE_CATEGORIES_TABLE = "CREATE TABLE IF NOT EXISTS cats(_id INTEGER ,name TEXT,img TEXT,"
                    + "parrent_id INTEGER ,orders INTEGER)";
            db.execSQL(CREATE_CATEGORIES_TABLE);

            CREATE_CATEGORIES_TABLE = "CREATE TABLE IF NOT EXISTS offline(_id INTEGER" +
                    " ,part TEXT,value TEXT," + "part_id INTEGER )";
            db.execSQL(CREATE_CATEGORIES_TABLE);

            CREATE_CATEGORIES_TABLE = "CREATE TABLE IF NOT EXISTS  sub_cats(_id INTEGERY,name TEXT,img TEXT,cat_id INTEGER)";
            db.execSQL(CREATE_CATEGORIES_TABLE);

            CREATE_CATEGORIES_TABLE = "CREATE TABLE IF NOT EXISTS  sabadkharid(_id INTEGER ,name TEXT,img TEXT"
                    + ",price TEXT,num INTEGER,max_count INTEGER,price2 INTEGER,property TEXT,catId INTEGER,omde_num INTEGER, omde_price TEXT,shopId TEXT)";
            db.execSQL(CREATE_CATEGORIES_TABLE);

            CREATE_CATEGORIES_TABLE = "CREATE TABLE IF NOT EXISTS  likes(_id INTEGER ,name TEXT"
                    + ",price TEXT,img TEXT)";
            db.execSQL(CREATE_CATEGORIES_TABLE);

            CREATE_CATEGORIES_TABLE = "CREATE TABLE IF NOT EXISTS  last5(_id INTEGER ,name TEXT"
                    + ",price TEXT,img TEXT,priceOmde TEXT,basteBandiVije TEXT, basteBandiVijePrice TEXT" +
                    ",offUser TEXT,minOmdeOrder TEXT,catid TEXT ,vazn TEXT )";
            db.execSQL(CREATE_CATEGORIES_TABLE);

            CREATE_CATEGORIES_TABLE = "CREATE TABLE IF NOT EXISTS city(_id INTEGER ,name TEXT,province INTEGER)";
            db.execSQL(CREATE_CATEGORIES_TABLE);

            CREATE_CATEGORIES_TABLE = "CREATE TABLE IF NOT EXISTS ostan(_id INTEGER ,name TEXT)";
            db.execSQL(CREATE_CATEGORIES_TABLE);

            db.execSQL(CREATE_CATEGORIES_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS sabadkharid");
            onCreate(db);
        }
    }

    public DatabaseHandler open() {
        try {
            helper = new OpenHelper(context);
            myDB = helper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public void close() {
        myDB.close();
    }

    public boolean isOpen() {
        if (myDB == null)
            return false;
        else
            return myDB.isOpen();
    }

    // //////////////////////////////////////

    public void insert(int id, String question, String image, String dates, String answer1, String answer2,
                       String answer3, String sahih, String game_emtiaz) {
        myDB.execSQL("insert into game values (" + id + ", '" + question + "','" + image + "','" + dates + "',0 " + ""
                + ",'" + answer1 + "','" + answer2 + "','" + answer3 + "','" + sahih + "','" + game_emtiaz + "'  );");
    }

    public Cursor getAllQuestions() {
        String where = "answered=0";
        Cursor c = myDB.query(true, "game", CATKEYS, where, null, null, null, "_id desC", null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public void removeCats_va_subcats() {
        myDB.delete("cats", null, null);
    }

    public void insertCats(String name, String img, String id, int parent_id, int orders) {
        myDB.execSQL("insert into cats values (" + Integer.parseInt(id) + ", '" + name + "','" + img + "'," + parent_id
                + "," + orders + ");");
    }

    public void insertCats(String name, String img, String id) {
        myDB.execSQL("insert into cats values (" + Integer.parseInt(id) + ", '" + name + "','" + img + "');");
    }

    public boolean sabadkharid(String name, String pimg, int prdId, String price
            , String maxCount, String tedad, String price2, String property, Integer catId, int omde_num, int omde_price, String shopId) {
        if (getNumSabad() > 0 && !shopId.equals("0") && !shopId.equals(getShopId() + ""))
            return false;
        else {
            myDB.execSQL("insert into sabadkharid values (" + prdId + ",'" + name + "'" +
                    ", '" + pimg + "'" + ", '" + price + "', '" + tedad + "','" + maxCount + "','" + price2 + "','" + property + "'," + catId + ",'" + omde_num + "','" + omde_price + "','"+shopId+"');");
            return true;
        }
    }

    public int getShopId() {
        int shopId = 0;
        Cursor c = myDB.query(true, "sabadkharid", SABADKEY, null, null, null, null, null, null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            shopId = c.getInt(c.getColumnIndex("shopId"));
        }
        c.close();
        return shopId;
    }

    public Cursor getCats() {
        Cursor c = myDB.query(true, "cats", CATKEYS, "parrent_id=0", null, null, null, "orders", null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public int getsub(Integer catid) {
        Cursor c;
        c = myDB.rawQuery("select _id from sub_cats WHERE cat_id='" + catid + "' order by orders", null);
        return c.getCount();
    }

    public int getNumProd(String id) {
        int s = 0;
        Cursor c = myDB.rawQuery("SELECT num FROM sabadkharid where _id= " + id, null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            s = c.getInt(0);
        }
        c.close();
        ;
        return s;
    }


    public int checkAddedBefore(int prdId) {
        Cursor c = myDB.query(true, "sabadkharid", SABADKEY, "_id=" + prdId, null, null, null, null, null);
        return c.getCount();
    }


    public boolean isAddedSabad(String id) {
        if (id.length() == 0)
            return false;

        Cursor c = myDB.query(true, "sabadkharid", SABADKEY, "_id=" + id, null, null, null, null, null);
        if (c.getCount() > 0)
            return true;
        else
            return false;
    }


    public boolean isLiked(int id) {
        Cursor c = myDB.query(true, "likes", LIKEKEY, "_id=" + id, null, null, null, null, "1");
        if (c.getCount() > 0)
            return true;
        else
            return false;
    }

    public void dolike(boolean b, String string, String name, String price, String img) {// string
        // -->id
        Log.v("this", "db " + b + " " + string);
        if (b == false)// remove like
            myDB.delete("likes", "_id=" + Integer.parseInt(string), null);
        else
            // insert like
            myDB.execSQL("insert into likes values (" + Integer.parseInt(string) + ", '" + name + "'" + ", '" + price
                    + "', '" + img + "');");
    }

    public int countLikes(int uid) {
        Cursor c = myDB.query(true, "likes", LIKEKEY, null, null, null, null, null, "1");
        return c.getCount();
    }

    public Cursor getFavs() {
        Cursor c = myDB.rawQuery("SELECT * FROM likes ", null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public void check() {
        Cursor c = myDB.rawQuery("SELECT num,_id FROM sabadkharid", null);
        if (c != null) {
            c.moveToFirst();
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                if (c.getInt(0) == 0) {
                    updateNum(c.getInt(0), 1);
                }
            }
        }
    }


    public Cursor getProdctsSabad() {
        Cursor c = myDB.rawQuery("SELECT _id,num,property,price FROM sabadkharid ", null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getSabadkharid() {
        Cursor c = myDB.query(true, "sabadkharid", SABADKEY, null, null, null, null, "catId asc", null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Boolean HasOffline(String part, int partId) {
        Cursor c = myDB.query(true, "offline", OFFLINE, "part='" + part + "'" + " and part_id=" + partId, null, null, null, null, null);
        if (c != null && c.getCount() > 0)
            return true;
        else
            return false;
    }

    public String getOffline(String part, Integer partId) {
        Cursor c = myDB.query(true, "offline", OFFLINE, "part='" + part + "'" + " and part_id=" + partId, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c.getString(2);
    }

    public void insertOffline(String offlineValue, String part, Integer partId) {
        myDB.delete("offline", "part='" + part + "'" + " and part_id=" + partId, null);
        myDB.execSQL("insert into offline values (null, '" + part + "','" + offlineValue.replaceAll("'", "") + "'," + partId + ");");
    }

    public void deleteSabad(String id) {
        myDB.delete("sabadkharid", "_id=" + id, null);
    }


    public void updateTedad(int num, String id) {
        myDB.execSQL("update sabadkharid set num='" + num + "' where _id=" + id);
    }


    public int getcountsabad() {
        try {
            Cursor c = myDB.query(true, "sabadkharid", SABADKEY, null, null, null, null, null, null);
            return c.getCount();
        } catch (Exception e) {
            return 0;
        }
    }

    public String getSabadMablaghKolWithTakhfif() {
        Integer sum = 0;
        Cursor c = myDB.query(true, "sabadkharid", SABADKEY, null, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
            //{ "_id", "name", "img", "price", "num","max_count","price2","property","catId","omde_num","omde_price"};
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                int num = c.getInt(4);
                int numOmde = c.getInt(9);
                String price = c.getString(3);
                if (numOmde > 0 && num >= numOmde) {
                    price = c.getString(10);
                }
                sum += c.getInt(4) * Integer.parseInt(price);
            }
        }
        c.close();
        return sum + "";
    }


    public String getSabadMablaghKol() {
        Integer sum = 0;
        Cursor c = myDB.query(true, "sabadkharid", SABADKEY, null, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                if (c.getInt(c.getColumnIndex("price2")) > 3)//4 -->tedad ** 3-->price
                    sum += c.getInt(4) * c.getInt(c.getColumnIndex("price2"));
                else
                    sum += c.getInt(4) * Integer.parseInt(c.getString(3));
            }
        }
        c.close();
        return sum + "";
    }

    /*public Integer getJamTakhfifha() {
        Integer sum = 0;
        Cursor c = myDB.query(true, "sabadkharid", SABADKEY, null, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                if(c.getInt(c.getColumnIndex("price2"))>0) {//4 -->tedad ** 3-->price
                    int price=Integer.parseInt(c.getString(3));
                    int num=c.getInt(4);
                    if(num>=c.getInt(c.getColumnIndex("omde_num"))
                            && c.getInt(c.getColumnIndex("omde_num"))>0){//gheymat omde
                        price=c.getInt(c.getColumnIndex("omde_price"));
                    }
                    int tafzol=c.getInt(c.getColumnIndex("price2"))-price;
                    sum += c.getInt(4) * tafzol;
                }
            }
        }
        c.close();
        return sum;
    }*/
    public Integer getJamTakhfifha() {
        Integer sum = 0;
        Cursor c = myDB.query(true, "sabadkharid", SABADKEY, null, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                int num = c.getInt(4);
                int omdeNum = c.getInt(c.getColumnIndex("omde_num"));
                int price = Integer.parseInt(c.getString(3));
                int tafzol = 0;
                if (num >= omdeNum && omdeNum > 0 &&
                        c.getInt(c.getColumnIndex("omde_price")) > 0) {
                    tafzol = price - c.getInt(c.getColumnIndex("omde_price"));
                    sum += c.getInt(4) * tafzol;
                } else if (c.getInt(c.getColumnIndex("price2")) > 0) {//4 -->tedad ** 3-->price
                    tafzol = c.getInt(c.getColumnIndex("price2")) - price;
                    sum += c.getInt(4) * tafzol;
                }
            }
        }
        c.close();
        return sum;
    }

    public int getNumSabad() {
        Cursor c = myDB.query(true, "sabadkharid", SABADKEY, null, null, null, null, null, null);
        int s = c.getCount();
        c.close();
        return s;
    }

    public String getSumVazn() {
        Cursor c = myDB.rawQuery("SELECT sum(weight)as val FROM sabadkharid ", null);
        if (c != null) {
            c.moveToFirst();
        }
        int s = c.getInt(0);
        c.close();
        return Integer.toString(s);
    }

    public void updateNum(int id, int i) {
        if (i == 0)
            i = 1;
        if (i >= 0)
            myDB.execSQL("update sabadkharid set num=" + i + " where _id=" + id);
    }

    public void updatePrice(String id, String gheymat) {
        myDB.execSQL("update sabadkharid set price='" + gheymat + "' where _id=" + id);
    }

    public String calKolBonTakhfif() {
        Cursor c = myDB.rawQuery("SELECT sum(takhfif)as val FROM sabadkharid ", null);
        if (c != null) {
            c.moveToFirst();
        }
        String s = c.getString(0);
        c.close();
        return s;
    }

    public void removeFromSabad(String id) {
        myDB.delete("sabadkharid", "_id=" + id, null);
    }

    public void clearSabadKharid() {
        myDB.delete("sabadkharid", null, null);
    }

}
