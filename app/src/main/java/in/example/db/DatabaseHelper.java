package in.example.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import in.example.item.ItemProperty;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "realEstate.db";
    public static final String TABLE_FAVOURITE_NAME = "favourite";

    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_RATE = "rate";
    public static final String KEY_PRICE = "price";
    public static final String KEY_BED = "bed";
    public static final String KEY_BATH = "bath";
    public static final String KEY_AREA = "area";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_PURPOSE = "purpose";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_FAVOURITE_TABLE = "CREATE TABLE " + TABLE_FAVOURITE_NAME + "("
                + KEY_ID + " INTEGER,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_RATE + " TEXT,"
                + KEY_PRICE + " TEXT,"
                + KEY_BED + " TEXT,"
                + KEY_BATH + " TEXT,"
                + KEY_AREA + " TEXT,"
                + KEY_ADDRESS + " TEXT,"
                + KEY_PURPOSE + " TEXT"
                + ")";
        db.execSQL(CREATE_FAVOURITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITE_NAME);
        // Create tables again
        onCreate(db);
    }

    public boolean getFavouriteById(String story_id) {
        boolean count = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = new String[]{story_id};
        Cursor cursor = db.rawQuery("SELECT id FROM favourite WHERE id=? ", args);
        if (cursor.moveToFirst()) {
            count = true;
        }
        cursor.close();
        db.close();
        return count;
    }

    public void removeFavouriteById(String _id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM  favourite " + " WHERE " + KEY_ID + " = " + _id);
        db.close();
    }

    public long addFavourite(String TableName, ContentValues contentvalues, String s1) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(TableName, s1, contentvalues);
    }

    public ArrayList<ItemProperty> getFavourite() {
        ArrayList<ItemProperty> chapterList = new ArrayList<>();
        String selectQuery = "SELECT *  FROM "
                + TABLE_FAVOURITE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ItemProperty contact = new ItemProperty();
                contact.setPId(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID)));
                contact.setPropertyName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)));
                contact.setPropertyThumbnailB(cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE)));
                contact.setRateAvg(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RATE)));
                contact.setPropertyPrice(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRICE)));
                contact.setPropertyBed(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BED)));
                contact.setPropertyBath(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BATH)));
                contact.setPropertyArea(cursor.getString(cursor.getColumnIndexOrThrow(KEY_AREA)));
                contact.setPropertyAddress(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ADDRESS)));
                contact.setPropertyPurpose(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PURPOSE)));

                chapterList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return chapterList;
    }
}
