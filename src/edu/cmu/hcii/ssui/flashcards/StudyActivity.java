package edu.cmu.hcii.ssui.flashcards;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class StudyActivity extends Activity {
    private static final String TAG = StudyActivity.class.getSimpleName();

    private long mDeckId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        Bundle bundle = getIntent().getExtras();
        mDeckId = bundle.getLong(MainActivity.ARG_DECK_ID);
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

}
