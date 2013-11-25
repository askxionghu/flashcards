package edu.cmu.hcii.ssui.flashcards;

import java.util.ArrayList;
import java.util.List;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;

import edu.cmu.hcii.ssui.flashcards.db.CardContract.Queries;
import edu.cmu.hcii.ssui.flashcards.db.CardDbHelper;

public class StudyActivity extends FragmentActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = StudyActivity.class.getSimpleName();

    private static final int LOADER_ID = 1;

    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
    private SQLiteCursorLoader mLoader;

    /**
     * The order in which we study the {@link Card}s.
     */
    private List<Integer> mStudyOrder;

    /**
     * The {@link Card}s contained in this {@link Deck}.
     */
    private List<Card> mCards;

    /**
     * The {@link Deck} we're examining at the moment.
     */
    private long mDeckId;

    private boolean mShowingBack;

    private Button mFlipButton;

    private Button mBackFlipButton;

    /**
     * The pager widget which handles the animation to the next {@link Card}.
     */
    private ViewPager mPager;

    /**
     * Provides the pages to the view pager.
     */
    private StudyPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        Bundle bundle = getIntent().getExtras();
        mDeckId = bundle.getLong(MainActivity.ARG_DECK_ID);

        mCallbacks = this;

        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID, bundle, mCallbacks);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new StudyPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "Current Page: " + position);
            }
        });

        findViewById(R.id.pager_container).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClickListener");
                flipCard();
            }
        });

        mCards = new ArrayList<Card>();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.study, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
        case R.id.action_end_session:
            // TODO: End study session.
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /* --- LOADER CALLBACKS --- */

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mLoader = new SQLiteCursorLoader(this, CardDbHelper.getInstance(this),
                Queries.GET_CARDS_BY_DECK, new String[] { String.valueOf(mDeckId) });
        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
        case LOADER_ID:
            mLoader = (SQLiteCursorLoader) loader;
            List<Card> cards = new ArrayList<Card>();

            // Iterate through the cards and build a list.
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Card card = cursorToCard(cursor);
                cards.add(card);
                cursor.moveToNext();
            }

            mCards = cards;
            mPagerAdapter.notifyDataSetChanged();
            break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mLoader = (SQLiteCursorLoader) loader;
        mPagerAdapter.notifyDataSetChanged();
    }

    /* --- PRIVATE HELPER METHODS --- */

    private Card cursorToCard(Cursor cursor) {
        long id = cursor.getLong(0);
        long deckId = cursor.getLong(1);
        String front = cursor.getString(2);
        String back = cursor.getString(3);
        return new Card(id, deckId, front, back);
    }

    private void flipCard() {
        if (!mShowingBack) {
            // Loads the animation for flipping the cards.
            AnimatorSet backIn = (AnimatorSet) AnimatorInflater.loadAnimator(this,
                    R.anim.card_flip_right_in);
            backIn.setTarget(mBackFlipButton);
            AnimatorSet frontOut = (AnimatorSet) AnimatorInflater.loadAnimator(this,
                    R.anim.card_flip_right_out);
            frontOut.setTarget(mFlipButton);

            // Combines and plays the animation.
            AnimatorSet flip = new AnimatorSet();
            flip.play(backIn).with(frontOut);
            flip.start();

            mShowingBack = true;
        } else {
            AnimatorSet backIn = (AnimatorSet) AnimatorInflater.loadAnimator(this,
                    R.anim.card_flip_left_in);
            backIn.setTarget(mFlipButton);
            AnimatorSet frontOut = (AnimatorSet) AnimatorInflater.loadAnimator(this,
                    R.anim.card_flip_left_out);
            frontOut.setTarget(mBackFlipButton);

            AnimatorSet flip = new AnimatorSet();
            flip.play(backIn).with(frontOut);
            flip.start();

            mShowingBack = false;
        }
    }

    /* --- PAGER ADAPTER --- */

    /**
     * A simple pager adapter that represents 5 {@link ScreenSlidePageFragment}
     * objects, in
     * sequence.
     */
    private class StudyPagerAdapter extends FragmentStatePagerAdapter {

        public StudyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);

            if (object != null) {
                StudyPageFragment fragment = (StudyPageFragment) object;
                mFlipButton = fragment.getCardFront();
                mBackFlipButton = fragment.getCardBack();

                mFlipButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        flipCard();
                    }
                });

                mBackFlipButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        flipCard();
                    }
                });

                if (mFlipButton != null && mFlipButton.getAlpha() == 0.0f) {
                    mShowingBack = true;
                } else {
                    mShowingBack = false;
                }
            }
        }

        @Override
        public Fragment getItem(int position) {
            Card card = mCards.get(position);

            String front = card == null ? null : card.getFront();
            String back = card == null ? null : card.getBack();

            return StudyPageFragment.newInstance(front, back);
        }

        @Override
        public int getCount() {
            return mCards == null ? 0 : mCards.size();
        }

    }

}
