package in.dasilvacont.hearthstonecards.app.data;

/**
 * Created by dasilvacontin on 16/03/15.
 */

import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import in.dasilvacont.hearthstonecards.app.data.CardContract.CardEntry;

/**
 * Manages a local database for weather data.
 */
public class CardDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 5;

    static final String DATABASE_NAME = "card.db";

    public CardDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_CARD_TABLE = "CREATE TABLE " + CardEntry.TABLE_NAME + " (" +
                CardEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CardEntry.COLUMN_CARD_ID + " TEXT UNIQUE NOT NULL, " +
                CardEntry.COLUMN_CARD_NAME + " TEXT NOT NULL, " +
                CardEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                CardEntry.COLUMN_RARITY + " TEXT NOT NULL, " +
                CardEntry.COLUMN_COST + " TEXT NOT NULL, " +
                CardEntry.COLUMN_ATTACK + " TEXT NOT NULL, " +
                CardEntry.COLUMN_HEALTH + " TEXT NOT NULL, " +
                CardEntry.COLUMN_TEXT + " TEXT NOT NULL, " +
                CardEntry.COLUMN_PLAYER_CLASS + " TEXT NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_CARD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CardEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
