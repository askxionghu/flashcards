package edu.cmu.hcii.ssui.flashcards;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;

import edu.cmu.hcii.ssui.flashcards.Deck.DeckMutator;
import edu.cmu.hcii.ssui.flashcards.db.CardDbHelper;
import edu.cmu.hcii.ssui.flashcards.db.CardDbHelper.DeckTable;
import edu.cmu.hcii.ssui.flashcards.db.CardDbHelper.Queries;
import edu.cmu.hcii.ssui.flashcards.db.CardDbHelper.Tables;
import edu.cmu.hcii.ssui.flashcards.dialogs.NewDeckDialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Loader;
import android.database.Cursor;

public class ManageCardsListActivity extends ListActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, DeckMutator {

    private static final int LOADER_ID = 1;

    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
    private SimpleCursorAdapter mAdapter;
    private SQLiteCursorLoader mLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] dataColumns = { DeckTable.NAME, DeckTable.DESCRIPTION };
        int[] viewIds = { R.id.label_deck_name, R.id.label_deck_description };

        mAdapter = new SimpleCursorAdapter(this, R.layout.deck_list_item, null, dataColumns,
                viewIds, 0);
        setListAdapter(mAdapter);
        mCallbacks = this;

        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID, null, mCallbacks);
    }

    /* --- ACTION BAR --- */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
        case R.id.action_new_deck:
            DialogFragment dialog = NewDeckDialog.newInstance();
            dialog.show(getFragmentManager(), NewDeckDialog.class.getSimpleName());
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
        mLoader = new SQLiteCursorLoader(this, new CardDbHelper(this), Queries.GET_DECKS,
                Queries.GET_DECKS_ARGS);
        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mLoader = (SQLiteCursorLoader) loader;

        switch (loader.getId()) {
        case LOADER_ID:
            mAdapter.swapCursor(cursor);
            break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mAdapter.swapCursor(null);
    }

    /* --- DECK MUTATOR METHODS --- */

    @Override
    public void insertDeck(String name, String description) {
        ContentValues values = new ContentValues();
        values.put(DeckTable.NAME, name);
        values.put(DeckTable.DESCRIPTION, description);

        mLoader.insert(Tables.DECKS, null, values);
    }

}
