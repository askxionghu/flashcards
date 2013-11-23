package edu.cmu.hcii.ssui.flashcards;

public class Card {
    private static final String TAG = Card.class.getSimpleName();

    private final long mId;
    private long mDeckId;
    private String mFront;
    private String mBack;

    public Card(long id, long deckId, String front, String back) {
       mId = id;
       mDeckId = deckId;
       mFront = front;
       mBack = back;
    }

    public long getId() {
        return mId;
    }

    public long getDeckId() {
        return mDeckId;
    }

    public String getFront() {
        return mFront;
    }

    public String getBack() {
        return mBack;
    }

}
