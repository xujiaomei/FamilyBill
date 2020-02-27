package com.example.mylittlebill;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String COST_MONEY = "cost_money";
    public static final String COST_DATE = "cost_date";
    public static final String COST_TITLE = "cost_title";
    public static final String MYLITTLEBILL = "mylittlebill";

    public DatabaseHelper(@Nullable Context context) {
        super(context, "mylittlebill", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists mylittlebill(" +
                "id integer primary key, " +
                "cost_title varchar, "+
                "cost_date varchar, "+
                "cost_money varchar)");
    }

    public void insertCost(CostBean costBean){
        SQLiteDatabase database = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COST_TITLE,costBean.costTitle);  //String key,Boolean value;
        cv.put(COST_DATE,costBean.costDate);
        cv.put(COST_MONEY,costBean.costMoney);
        database.insert(MYLITTLEBILL,null,cv);
    }

    public Cursor getAllCostData(){
        SQLiteDatabase database = getWritableDatabase();
        return database.query(MYLITTLEBILL,null,null,null,null,null,COST_DATE + " ASC");
    }

    public void deleteAllData(){
        SQLiteDatabase database = getWritableDatabase();
        database.delete(MYLITTLEBILL,null,null);
    }

    public void deleteOne(CostBean costBean)
    {
        SQLiteDatabase database=getWritableDatabase();
        database.delete(MYLITTLEBILL,"COST_TITLE = ? and COST_MONEY = ? and COST_DATE = ?", new String[]{""+costBean.costTitle,""+costBean.costMoney,""+costBean.costDate});
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
