package edu.cmu.hcii.ssui.flashcards;

public class Card {
    private static final String TAG = Card.class.getSimpleName();

    private final long mId;
    private long mGroupId;
    private String mFront;
    private String mBack;

    public Card(long id, long groupId, String front, String back) {
       mId = id;
       mGroupId = groupId;
       mFront = front;
       mBack = back;
    }

    public long getId() {
        return mId;
    }

    public long getGroupId() {
        return mGroupId;
    }

    public String getFront() {
        return mFront;
    }

    public String getBack() {
        return mBack;
    }

}
