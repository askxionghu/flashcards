package edu.cmu.hcii.ssui.flashcards;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class StudyPageFragment extends Fragment {
    private static final String TAG = StudyPageFragment.class.getSimpleName();

    private Button mCardFront;

    private Button mCardBack;

    private String mFront;

    private String mBack;

    /**
     * Static factory method for this fragment.
     */
    public static Fragment newInstance(String front, String back) {
        StudyPageFragment fragment = new StudyPageFragment();
        Bundle args = new Bundle();
        args.putString(MainActivity.ARG_CARD_FRONT, front);
        args.putString(MainActivity.ARG_CARD_BACK, back);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFront = getArguments().getString(MainActivity.ARG_CARD_FRONT);
        mBack = getArguments().getString(MainActivity.ARG_CARD_BACK);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.study_page, container, false);

        return view;
    }

}
