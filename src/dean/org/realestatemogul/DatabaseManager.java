package dean.org.realestatemogul;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dean on 14/01/2018.
 */

public class DatabaseManager extends SQLiteOpenHelper {

    /**
     * Constructor for the database manager class
     * @param context The context of the application
     */
    public DatabaseManager(final Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates the database table for saving player details
     * @param database The SQLiteDatabase instance for our database
     */
    @Override
    public void onCreate(final SQLiteDatabase database)
    {
        database.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_MONEY + " INTEGER," +
                COLUMN_TENT + " INTEGER," +
                COLUMN_CARAVAN + " INTEGER," +
                COLUMN_FLAT + " INTEGER," +
                COLUMN_HOUSE + " INTEGER," +
                COLUMN_MANSION + " INTEGER," +
                COLUMN_CASTLE + " INTEGER)");
    }

    /**
     * Deletes the old database and creates a new one based on a new version number
     * Version changing is currently unused in this application as it's not needed
     * @param database The SQLiteDatabase instance of the current database
     * @param oldVersion The old version of the database
     * @param newVersion The new version of the database.
     */
    @Override
    public void onUpgrade(final SQLiteDatabase database, final int oldVersion, final int newVersion)
    {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    /**
     * Inserts an entry into the database.
     * @param data The data to fill the columns of the database
     * @return true if the row could be added into the database
     */
    public boolean insertEntry(final int... data)
    {
        if(data.length != 7)
            return false;
        final SQLiteDatabase database = this.getWritableDatabase();
        final ContentValues values = new ContentValues();
        values.put(COLUMN_ID, 0);
        values.put(COLUMN_MONEY, data[0]);
        values.put(COLUMN_TENT, data[1]);
        values.put(COLUMN_CARAVAN, data[2]);
        values.put(COLUMN_FLAT, data[3]);
        values.put(COLUMN_HOUSE, data[4]);
        values.put(COLUMN_MANSION, data[5]);
        values.put(COLUMN_CASTLE, data[6]);
        return database.insert(TABLE_NAME, null, values) != -1;
    }

    /**
     * Updates an entry in the database, as player data is a single entry
     * the ID column is hardcoded to be 0.
     * @param data The data to update the row with.
     * @return true if the row could be updated
     */
    public boolean updateEntry(final int... data)
    {
        if(data.length != 7)
            return false;
        final SQLiteDatabase database = this.getWritableDatabase();
        final ContentValues values = new ContentValues();
        values.put(COLUMN_ID, 0);
        values.put(COLUMN_MONEY, data[0]);
        values.put(COLUMN_TENT, data[1]);
        values.put(COLUMN_CARAVAN, data[2]);
        values.put(COLUMN_FLAT, data[3]);
        values.put(COLUMN_HOUSE, data[4]);
        values.put(COLUMN_MANSION, data[5]);
        values.put(COLUMN_CASTLE, data[6]);
        return database.update(TABLE_NAME, values, "ID = ?", new String[] { "0" }) != -1;
    }

    /**
     * Gets all entries from the database
     * @return The result set in the form of a Cursor object
     */
    public Cursor getAllEntries()
    {
        final SQLiteDatabase database = this.getWritableDatabase();
        final Cursor results = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return results;
    }

    /**
     * Database information such as column names and database name.
     */
    public static final String DATABASE_NAME = "GameSave.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "player_save";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_MONEY = "MONEY";
    public static final String COLUMN_TENT = "TENTS";
    public static final String COLUMN_CARAVAN = "CARAVANS";
    public static final String COLUMN_FLAT = "FLATS";
    public static final String COLUMN_HOUSE = "HOUSES";
    public static final String COLUMN_MANSION = "MANSIONS";
    public static final String COLUMN_CASTLE = "CASTLES";

}
