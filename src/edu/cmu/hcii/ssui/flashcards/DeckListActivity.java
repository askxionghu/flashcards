package edu.cmu.hcii.ssui.flashcards;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;

import edu.cmu.hcii.ssui.flashcards.Card.CardMutator;
import edu.cmu.hcii.ssui.flashcards.db.CardDbHelper;
import edu.cmu.hcii.ssui.flashcards.db.CardDbHelper.CardTable;
import edu.cmu.hcii.ssui.flashcards.db.CardDbHelper.DeckTable;
import edu.cmu.hcii.ssui.flashcards.db.CardDbHelper.Queries;
import edu.cmu.hcii.ssui.flashcards.db.CardDbHelper.Tables;
import edu.cmu.hcii.ssui.flashcards.dialogs.NewCardDialog;
import edu.cmu.hcii.ssui.flashcards.dialogs.NewDeckDialog;
import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Loader;
import android.database.Cursor;

public class DeckListActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>, CardMutator {

    private static final int CARDS_LOADER_ID = 1;

    private static final int DECK_LOADER_ID = 2;

    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;

    private SimpleCursorAdapter mCardsAdapter;
    private SQLiteCursorLoader mCardsLoader;

    private SQLiteCursorLoader mDeckLoader;

    private long mDeckId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();

        // Sets the ActionBar title to be that of the Bundle
        CharSequence title = bundle.getCharSequence(MainActivity.ARG_DECK_NAME);
        CharSequence subtitle = bundle.getCharSequence(MainActivity.ARG_DECK_DESCRIPTION);
        setActionBarTitle(title, subtitle);

        mDeckId = bundle.getLong(MainActivity.ARG_DECK_ID);

        String[] dataColumns = { CardTable.FRONT, CardTable.BACK };
        int[] viewIds = { R.id.label_deck_name, R.id.label_deck_description };

        mCardsAdapter = new SimpleCursorAdapter(this, R.layout.deck_list_item, null, dataColumns,
                viewIds, 0);
        setListAdapter(mCardsAdapter);
        mCallbacks = this;

        LoaderManager lm = getLoaderManager();
        lm.initLoader(CARDS_LOADER_ID, bundle, mCallbacks);
        lm.initLoader(DECK_LOADER_ID, bundle, mCallbacks);
    }

    private void setActionBarTitle(CharSequence title, CharSequence subtitle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar bar = getActionBar();
            bar.setTitle(title);
            bar.setSubtitle(subtitle);
        }
    }

    /* --- ACTION BAR --- */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.deck, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
        case R.id.action_new_card:
            DialogFragment dialog = NewCardDialog.newInstance(mDeckId);
            dialog.show(getFragmentManager(), NewCardDialog.class.getSimpleName());
            return true;
        case R.id.action_settings:
            // TODO: 'Settings' menu button.
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /* --- LOADER CALLBACKS --- */

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
        case CARDS_LOADER_ID:
            mCardsLoader = new SQLiteCursorLoader(this, new CardDbHelper(this), Queries.GET_CARDS_BY_DECK,
                    new String[] { String.valueOf(mDeckId) });
            return mCardsLoader;
        case DECK_LOADER_ID:
            mDeckLoader = new SQLiteCursorLoader(this, new CardDbHelper(this), Queries.GET_DECK,
                    new String[] { String.valueOf(mDeckId) });
            return mDeckLoader;
        }
        return null; // Should never happen.
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
        case CARDS_LOADER_ID:
            mCardsLoader = (SQLiteCursorLoader) loader;
            mCardsAdapter.swapCursor(cursor);
            break;
        case DECK_LOADER_ID:
            cursor.moveToFirst();
            String name = cursor.getString(1); // Deck Name
            String description = cursor.getString(2); // Deck Description
            setActionBarTitle(name, description);
            break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
        case CARDS_LOADER_ID:
            mCardsLoader = (SQLiteCursorLoader) loader;
            mCardsAdapter.swapCursor(null);
            break;
        case DECK_LOADER_ID:
            mDeckLoader = (SQLiteCursorLoader) loader;
            break;
        }
    }

    /* --- CARD MUTATOR METHODS --- */

    @Override
    public void insertCard(long deckId, String front, String back) {
        ContentValues values = new ContentValues();
        values.put(CardTable.DECK_ID, deckId);
        values.put(CardTable.FRONT, front);
        values.put(CardTable.BACK, back);

        mCardsLoader.insert(Tables.CARDS, null, values);
    }

}
