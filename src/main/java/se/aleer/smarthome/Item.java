package se.aleer.smarthome;

import java.util.List;
import java.util.TreeSet;

/**
 * Created by alex on 2015-10-12.
 */
public abstract class Item {

    private int mId;
    private String mName;

    public Item(int id){
        this(id, "");
    }

    public Item(int id, String name){
        this.mId = id;
        this.mName = name;
    }

    public int getId(){ return mId; }
    public void setId(int id){ this.mId = id;}

    public String getName(){ return mName; }
    public void setName(String name){ this.mName = name; }

    public String toString(){
        return mId + ":";
    }

    public static int generate_unique_id(List<? extends Item> mList) {
        int id = mList.size() + 10; // A good starting point
        TreeSet<Integer> takenIds = new TreeSet<>();
        for (Item timer : mList) {
            takenIds.add(timer.getId());
        }
        while (true) {
            if (!takenIds.contains(id)) {
                return id;
            }
            ++id;
        }
    }
}
