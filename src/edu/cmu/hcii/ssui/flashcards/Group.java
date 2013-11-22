package edu.cmu.hcii.ssui.flashcards;

public class Group {
    private static final String TAG = Group.class.getSimpleName();

    private final long mId;
    private String mName;
    private String mDescription;

   public Group(long id, String name, String description) {
       mId = id;
       mName = name;
       mDescription = description;
   }

   public long getId() {
       return mId;
   }

   public String getName() {
       return mName;
   }

   public String getDescription() {
       return mDescription;
   }

}
