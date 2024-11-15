import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;


public class ListingDBHandler extends SQLiteOpenHelper {

    // Defining the schema
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "listingDB.db";
    public static final String TABLE_LISTINGS = "listings";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_HOURLY = "hourly";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_LESSOR = "lessor";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_IMAGE = "image";


    // This is the constructor
    public ListingDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Override onCreate() method to create the table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LISTINGS_TABLE = "CREATE TABLE " +
                TABLE_LISTINGS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_HOURLY + " REAL, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_LESSOR + " TEXT, " +
                COLUMN_PRICE + " REAL, " + // The "REAL" data type is used for floating-point numbers
                COLUMN_IMAGE + " BLOB" +
                ");";

        //TODO: Add more columns to the table for Deliverable 4

        db.execSQL(CREATE_LISTINGS_TABLE);
    }


    // Override onUpgrade() method to drop old tables and create new ones
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTINGS);
        onCreate(db);
    }

    // Method to add to a database
    public void addListing(Listing listing) {

        // Check if the user attempting this method is a lessor
        // If user is neither, they are denied access from using this method
        if (!getRole().equals("LESSOR") {
            System.out.println("Access denied. Only lessors can add listings.");
            return false;
        }

        // Check if the user attempting this method is the creator of the same listing,

        // Get the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where the column names are the keys
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, listing.getId());
        values.put(COLUMN_CATEGORY, listing.getCategory());
        values.put(COLUMN_HOURLY, listing.getHourly());
        values.put(COLUMN_DESCRIPTION, listing.getDescription());
        values.put(COLUMN_TITLE, listing.getTitle());
        values.put(COLUMN_LESSOR, listing.getLessor());
        values.put(COLUMN_PRICE, listing.getPrice());
        values.put(COLUMN_IMAGE, listing.getImage());

        //TODO: Add more columns to the table for Deliverable 4

        // Insert
        db.insert(TABLE_LISTINGS, null, values);

        // Close
        db.close();

    }

    // Method to read from a database
    public Listing findListing(String title) {

        // Getting reference to readable database
        SQLiteDatabase db = this.getReadableDatabase();

        // Run your query
        String query = "SELECT * FROM " + TABLE_LISTINGS + " WHERE " + COLUMN_TITLE + " = \"" + title + "\"";
        Cursor cursor = db.rawQuery(query, null);

        // Create object and then to get results
        Listing listing = new Listing();

        if (cursor.moveToFirst()) {
            listing.setID(Integer.parseInt(cursor.getString(0)));
            listing.setCategory(cursor.getString(1));
            listing.setHourly(Double.parseDouble(cursor.getString(2)));
            listing.setDescription(cursor.getString(3));
            listing.setTitle(cursor.getString(4));
            listing.setLessor(cursor.getString(5));
            listing.setPrice(Double.parseDouble(cursor.getString(6)));
            listing.setImage(cursor.getBlob(7));
            //TODO: Add more columns to the table for Deliverable 4

        } else {
            listing = null;
        }
        db.close();
        return listing;
    }

    // Method to delete information from the database
    public boolean deleteListing(Listing listing, String title) {

        // Defining the current user who's using this service/method
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Check if the user attempting this method is the creator of the same listing, or an admin
        // If neither, they are denied access from using this method
        // getLessor() method returns the lessor ID, since "lessor" in Listing.java retrieves the ID
        if (!listing.getLessor().equals(currentUser.getId()) || (!currentUser.getRole().equals("ADMIN"))  {
            System.out.println("Access denied. Only the creator of this listing & admins can delete it.");
            return false;
        }

        // Flag to check if the deletion was successful
        boolean result = false;

        // Getting reference to writable database
        SQLiteDatabase db = this.getWritableDatabase();

        // Run your query and delete
        String query = "SELECT * FROM " + TABLE_LISTINGS + " WHERE " + COLUMN_TITLE + " = \"" + title + "\"";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            String idStr = cursor.getString(0);
            db.delete(TABLE_LISTINGS, COLUMN_ID + " = " + idStr, null);
            cursor.close();
            result = true;
        }

        db.close();
        return result;
    }

    // Method to edit an existing listing
    public String editListing(Listing listing) {

        // Defining the current user who's using this service/method
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Check if the user attempting this method is the creator of the same listing, or an admin
        // If neither, they are denied access from using this method
        // getLessor() method returns the lessor ID, since "lessor" in Listing.java retrieves the ID
        if (!listing.getLessor().equals(currentUser.getId()) || (!currentUser.getRole().equals("ADMIN"))  {
            System.out.println("Access denied. Only the creator of this listing & admins can delete it.");
            return false;
        }

        // Get the database in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where the column names are the keys
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY, listing.getCategory());
        values.put(COLUMN_HOURLY, listing.getHourly());
        values.put(COLUMN_DESCRIPTION, listing.getDescription());
        values.put(COLUMN_TITLE, listing.getTitle());
        values.put(COLUMN_LESSOR, listing.getLessor());
        values.put(COLUMN_PRICE, listing.getPrice());
        values.put(COLUMN_IMAGE, listing.getImage());

        // Update the row, selecting it based on the id of the listing
        int rowsAffected = db.update(TABLE_LISTINGS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(listing.getId())});

        // Close the database
        db.close();

        // Return true if the update was successful (at least one row affected)
        if (rowsAffected > 1) {
            return "Edits were successful";
        }
        else if (rowsAffected == 1) {
            return "Edit was successful";
        }
        else if (rowsAffected == 0) {
            return "Edits were unsuccessful";
        }


    }





}