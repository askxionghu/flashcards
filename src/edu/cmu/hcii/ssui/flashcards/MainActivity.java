package edu.cmu.hcii.ssui.flashcards;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import edu.cmu.hcii.ssui.flashcards.dialogs.NewDeckDialog;

public class MainActivity extends Activity implements OnClickListener {

    private Button mManageCards, mStudy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mManageCards = (Button) findViewById(R.id.manage_cards);
        mStudy = (Button) findViewById(R.id.study);

        mManageCards.setOnClickListener(this);
        mStudy.setOnClickListener(this);
    }

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

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
        case R.id.manage_cards:
            Intent intent = new Intent(this, ManageCardsListActivity.class);
            startActivity(intent);
            break;
        case R.id.study:
            // TODO: Navigate to Study activity
            break;
        }
    }

}
