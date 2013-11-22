package edu.cmu.hcii.ssui.flashcards.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import edu.cmu.hcii.ssui.flashcards.Card;
import edu.cmu.hcii.ssui.flashcards.Group;
import edu.cmu.hcii.ssui.flashcards.db.CardDbHelper.CardTable;
import edu.cmu.hcii.ssui.flashcards.db.CardDbHelper.GroupTable;
import edu.cmu.hcii.ssui.flashcards.db.CardDbHelper.Tables;

public class CardDatabase {
    private static final String TAG = CardDatabase.class.getSimpleName();

    private CardDbHelper mDbHelper;
    private SQLiteDatabase mDb;

    public CardDatabase(Context context) {
        mDbHelper = new CardDbHelper(context);
    }

    /**
     * Opens the database for access.
     *
     * @throws SQLException
     */
    public void open() throws SQLException {
        mDb = mDbHelper.getWritableDatabase();
    }

    /**
     * Closes the connection to the database.
     */
    public void close() {
        if (mDb != null) {
            mDb.close();
        }
    }

    public void insertCard(Card card) {
        ContentValues values = new ContentValues();
        values.put(CardTable.GROUP_ID, card.getGroupId());
        values.put(CardTable.FRONT, card.getFront());
        values.put(CardTable.BACK, card.getBack());

        mDb.insert(Tables.CARDS, null, values);
    }

    public void insertGroup(Group group) {
        ContentValues values = new ContentValues();
        values.put(GroupTable.NAME, group.getName());
        values.put(GroupTable.DESCRIPTION, group.getDescription());

        mDb.insert(Tables.GROUPS, null, values);
    }

    public List<Group> getGroups() {
        Cursor cursor = mDb.query(true, Tables.GROUPS, null, null, null, null, null, null, null);

        List<Group> cards = new ArrayList<Group>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Group group = cursorToGroup(cursor);
            cards.add(group);
            cursor.moveToNext();
        }
        cursor.close();
        return cards;
    }

    public List<Card> getCardsByGroup(Group group) {
        Cursor cursor = mDb.query(true, Tables.CARDS, null, CardTable.GROUP_ID + " = ?",
                new String[] { String.valueOf(group.getId()) }, null, null, null, null);

        List<Card> cards = new ArrayList<Card>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Card card = cursorToCard(cursor);
            cards.add(card);
            cursor.moveToNext();
        }
        cursor.close();

        return cards;
    }

    public void deleteCard(long id) {
        mDb.delete(Tables.CARDS, CardTable._ID + " = ?", new String[] { String.valueOf(id) });
    }

    public void deleteCard(Card card) {
        long id = card.getId();
        deleteCard(id);
    }

    public void deleteGroup(long id) {
        mDb.delete(Tables.GROUPS, GroupTable._ID + " = ?", new String[] { String.valueOf(id) });
    }

    public void deleteGroup(Group group) {
        long id = group.getId();
        deleteCard(id);
    }

    private Card cursorToCard(Cursor cursor) {
        long id = cursor.getLong(0);
        long groupId = cursor.getLong(1);
        String front = cursor.getString(2);
        String back = cursor.getString(3);
        return new Card(id, groupId, front, back);
    }

    private Group cursorToGroup(Cursor cursor) {
        long id = cursor.getLong(0);
        String name = cursor.getString(1);
        String description = cursor.getString(2);
        return new Group(id, name, description);
    }

}
