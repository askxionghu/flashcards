package edu.cmu.hcii.ssui.flashcards;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;

import edu.cmu.hcii.ssui.flashcards.db.CardContract.DeckTable;
import edu.cmu.hcii.ssui.flashcards.db.CardContract.Queries;
import edu.cmu.hcii.ssui.flashcards.db.CardDbHelper;

public class StudyListActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = StudyListActivity.class.getSimpleName();

    private static final int LOADER_ID = 1;

    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
    private SimpleCursorAdapter mAdapter;
    private SQLiteCursorLoader mLoader;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar bar = getActionBar();
            bar.setSubtitle(R.string.study_session_subtitle);
            bar.setDisplayHomeAsUpEnabled(true);
        }

        String[] dataColumns = { DeckTable.NAME, DeckTable.DESCRIPTION };
        int[] viewIds = { R.id.label_deck_name, R.id.label_deck_description };

        mAdapter = new SimpleCursorAdapter(this, R.layout.deck_list_item, null, dataColumns,
                viewIds, 0);
        setListAdapter(mAdapter);
        mCallbacks = this;

        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID, null, mCallbacks);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        v.setEnabled(false);

        String name = ((TextView) v.findViewById(R.id.label_deck_name)).getText().toString();
        String description = ((TextView) v.findViewById(R.id.label_deck_description)).getText()
                .toString();

        Intent intent = new Intent(this, StudyActivity.class);
        intent.putExtra(MainActivity.ARG_DECK_ID, id);
        intent.putExtra(MainActivity.ARG_DECK_NAME, name);
        intent.putExtra(MainActivity.ARG_DECK_DESCRIPTION, description);
        startActivity(intent);
    }

    /* --- ACTION BAR --- */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /* --- LOADER CALLBACKS --- */

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mLoader = new SQLiteCursorLoader(this, CardDbHelper.getInstance(this), Queries.GET_ALL_DECKS, null);
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
    public void onLoaderReset(Loader<Cursor> loader) {
        mLoader = (SQLiteCursorLoader) loader;
        mAdapter.swapCursor(null);
    }

}
