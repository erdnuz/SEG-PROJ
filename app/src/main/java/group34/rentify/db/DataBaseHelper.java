import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DatabaseHelper class for managing the SQLite database for the Rentify application.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    
    // Database Name and Version
    private static final String DATABASE_NAME = "rentify.db";
    private static final int DATABASE_VERSION = 1;

    // User Table
    private static final String TABLE_USER = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";

    // Rental Item Table
    private static final String TABLE_ITEM = "items";
    private static final String COLUMN_ITEM_ID = "item_id";
    private static final String COLUMN_ITEM_NAME = "item_name";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_RENTAL_FEE = "rental_fee";
    private static final String COLUMN_AVAILABILITY = "availability";

    // SQL Queries
    private static final String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT NOT NULL, " +
            COLUMN_EMAIL + " TEXT NOT NULL UNIQUE);";

    private static final String CREATE_ITEM_TABLE = "CREATE TABLE " + TABLE_ITEM + " (" +
            COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ITEM_NAME + " TEXT NOT NULL, " +
            COLUMN_DESCRIPTION + " TEXT, " +
            COLUMN_CATEGORY + " TEXT NOT NULL, " +
            COLUMN_RENTAL_FEE + " REAL NOT NULL, " +
            COLUMN_AVAILABILITY + " TEXT NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_ITEM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);
        onCreate(db);
    }

    // CRUD operations for User
    public void addUser(String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    public Cursor getUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USER, null, COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
    }

    public void updateUser(int id, String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        db.update(TABLE_USER, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteUser(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // CRUD operations for Rental Items
    public void addItem(String itemName, String description, String category, double rentalFee, String availability) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, itemName);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_RENTAL_FEE, rentalFee);
        values.put(COLUMN_AVAILABILITY, availability);
        db.insert(TABLE_ITEM, null, values);
        db.close();
    }

    public Cursor getItem(int itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_ITEM, null, COLUMN_ITEM_ID + "=?", new String[]{String.valueOf(itemId)}, null, null, null);
    }

    public void updateItem(int itemId, String itemName, String description, String category, double rentalFee, String availability) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, itemName);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_RENTAL_FEE, rentalFee);
        values.put(COLUMN_AVAILABILITY, availability);
        db.update(TABLE_ITEM, values, COLUMN_ITEM_ID + "=?", new String[]{String.valueOf(itemId)});
        db.close();
    }

    public void deleteItem(int itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEM, COLUMN_ITEM_ID + "=?", new String[]{String.valueOf(itemId)});
        db.close();
    }
}
