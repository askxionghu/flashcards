package edu.cmu.hcii.ssui.flashcards.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class CardDbHelper extends SQLiteOpenHelper {
    private static final String TAG = CardDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "flashcards.db";
    private static final int DATABASE_VERSION = 1;

    /**
     * SQLite table names.
     */
    interface Tables {
        String CARDS = "cards";
        String GROUPS = "groups";
    }

    /**
     * Card table columns.
     */
    public interface CardTable extends BaseColumns {
        String GROUP_ID = "group_id";
        String FRONT = "front";
        String BACK = "back";
    }

    /**
     * Group table columns.
     */
    public interface GroupTable extends BaseColumns {
        String NAME = "name";
        String DESCRIPTION = "description";
    }

    /**
     * {@code REFERENCES} clauses.
     * */
    //@formatter:off
    private interface References {
        String GROUP_ID = "REFERENCES " + Tables.GROUPS + "(" + GroupTable._ID + ") ON DELETE CASCADE";
    }
    //@formatter:on

    //@formatter:off
    private static final String CARD_TABLE_CREATE = "CREATE TABLE " + Tables.CARDS + " ("
            + CardTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CardTable.GROUP_ID + " INTEGER NOT NULL " + References.GROUP_ID + ", "
            + CardTable.FRONT + " TEXT, "
            + CardTable.BACK + " TEXT);";
    //@formatter:on

    //@formatter:off
    private static final String GROUP_TABLE_CREATE = "CREATE TABLE " + Tables.GROUPS + " ("
            + GroupTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + GroupTable.NAME + " TEXT, "
            + GroupTable.DESCRIPTION + " TEXT);";
    //@formatter:on

    public CardDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CARD_TABLE_CREATE);
        db.execSQL(GROUP_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + Tables.CARDS + ";");
        db.execSQL("DROP TABLE IF EXISTS " + Tables.GROUPS + ";");

        // Create tables again
        onCreate(db);
    }

}
