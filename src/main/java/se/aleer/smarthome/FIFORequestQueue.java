package se.aleer.smarthome;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by alex on 2015-10-08.
 */
public class FIFORequestQueue {

    /** FIFO queue for switch status change */
    private Queue<requestItem> mRequestQueue;

    public FIFORequestQueue(){
        mRequestQueue = new LinkedList<>();
    }

    public class requestItem{
        public int id;
        public int type;
        requestItem(int id, int type){
            this.id = id;
            this.type = type;
        }
    }

    public boolean isEmpty(){
        return mRequestQueue.isEmpty();
    }

    public void put(int id, int type){
        mRequestQueue.add(new requestItem(id, type));
    }

    public requestItem pop(){
        return mRequestQueue.poll();
    }
}
