package edu.cmu.hcii.ssui.flashcards;

import java.util.ArrayList;
import java.util.List;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
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
     * The {@link Card}s contained in this {@link Deck}.
     */
    private final List<Card> mCards = new ArrayList<Card>();

    /**
     * The {@link Deck} we're examining at the moment.
     */
    private long mDeckId;

    private boolean mShowingBack;

    private FrameLayout mCardFront;

    private FrameLayout mCardBack;

    private TextView mMemorized;

    private Button mYesButton;

    private Button mNoButton;

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
        mPagerAdapter = new StudyPagerAdapter();
        mPager.setAdapter(mPagerAdapter);

        // Hides the Action Bar when device is in landscape.
        Configuration config = getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getActionBar().hide();
        } else {
            getActionBar().show();
        }
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
        case R.id.action_next:
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
            mCards.clear();

            // Iterate through the cards and build a list.
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Card card = cursorToCard(cursor);
                mCards.add(card);
                cursor.moveToNext();
            }

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

    private static Card cursorToCard(Cursor cursor) {
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
            backIn.setTarget(mCardBack);
            AnimatorSet frontOut = (AnimatorSet) AnimatorInflater.loadAnimator(this,
                    R.anim.card_flip_right_out);
            frontOut.setTarget(mCardFront);

            // Combines and plays the animation.
            AnimatorSet flip = new AnimatorSet();
            flip.play(backIn).with(frontOut);
            flip.start();

            mShowingBack = true;
            mCardBack.setClickable(true);
            mCardFront.setClickable(false);
        } else {
            AnimatorSet backIn = (AnimatorSet) AnimatorInflater.loadAnimator(this,
                    R.anim.card_flip_left_in);
            backIn.setTarget(mCardFront);
            AnimatorSet frontOut = (AnimatorSet) AnimatorInflater.loadAnimator(this,
                    R.anim.card_flip_left_out);
            frontOut.setTarget(mCardBack);

            AnimatorSet flip = new AnimatorSet();
            flip.play(backIn).with(frontOut);
            flip.start();

            mShowingBack = false;
            mCardBack.setClickable(false);
            mCardFront.setClickable(true);
        }
    }

    /* --- PAGER ADAPTER --- */

    /**
     * A simple pager adapter that represents 5 {@link ScreenSlidePageFragment}
     * objects, in
     * sequence.
     */
    private class StudyPagerAdapter extends PagerAdapter {

        private final View.OnClickListener mFlipListener = new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
            @Override
            public void onClick(View v) {
                flipCard();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                    mYesButton.animate().alpha(1.0f);
                    mNoButton.animate().alpha(1.0f);
                    mMemorized.animate().alpha(1.0f);
                }
            }
        };

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = View.inflate(getApplicationContext(), R.layout.study_page, null);
            // Introduction Page
            if (position == 0) {
                mCardFront = (FrameLayout) v.findViewById(R.id.card_front);
                TextView frontText = (TextView) v.findViewById(R.id.card_front_text);
                frontText.setText(getString(R.string.instruction_card));
                mCardFront.setOnClickListener(mFlipListener);

                mCardBack = (FrameLayout) v.findViewById(R.id.card_back);
                TextView backText = (TextView) v.findViewById(R.id.card_back_text);
                backText.setText(getString(R.string.instruction_card_back));
                mCardBack.setOnClickListener(mFlipListener);
                mCardBack.setClickable(false);

                if (mCardFront.getAlpha() == 0.0) {
                    mShowingBack = true;
                } else {
                    mShowingBack = false;
                }

                container.addView(v, 0);
                return v;
            }
            if (position < mCards.size() + 1) {
                Card card = mCards.get(position - 1);

                mCardFront = (FrameLayout) v.findViewById(R.id.card_front);
                TextView frontText = (TextView) v.findViewById(R.id.card_front_text);
                frontText.setText(card.getFront());
                mCardFront.setOnClickListener(mFlipListener);

                mCardBack = (FrameLayout) v.findViewById(R.id.card_back);
                TextView backText = (TextView) v.findViewById(R.id.card_back_text);
                backText.setText(card.getBack());
                mCardBack.setOnClickListener(mFlipListener);
                mCardBack.setClickable(false);

                if (mCardFront.getAlpha() == 0.0) {
                    mShowingBack = true;
                } else {
                    mShowingBack = false;
                }
            }
            container.addView(v, 0);
            return v;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (position == 0) {
                super.setPrimaryItem(container, position, object);;
            }
            View v = (View) object;
            if (position < mCards.size() + 1) {
                mCardFront = (FrameLayout) v.findViewById(R.id.card_front);
                mCardBack = (FrameLayout) v.findViewById(R.id.card_back);

                mMemorized = (TextView) v.findViewById(R.id.memorized);
                mYesButton = (Button) v.findViewById(R.id.yes_button);
                mNoButton = (Button) v.findViewById(R.id.no_button);

                if (mCardFront.getAlpha() == 0.0) {
                    mShowingBack = true;
                } else {
                    mShowingBack = false;
                }
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object view) {
            container.removeView((View) view);
        }

        @Override
        public int getCount() {
            return mCards.size() + 1;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

}
