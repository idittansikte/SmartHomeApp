package se.aleer.smarthome;


public class Switch {

    private int mId;
    private int mController;
    private String mName;
    private int on;

    public Switch(){
        super();
    }

    public Switch(int id, int controller, String name)
    {
        this.mId = id;
        this.mController = controller;
        this.mName = name;
        this.on = -1;
    }

    public int getState()
    {
        return on;
    }

    public void setState(int state)
    {
        on = state;
    }
    public int getId(){
        return mId;
    }

    public int getController(){
        return mController;
    }

    public String getName(){
        return mName;
    }

    public void setId(int id){
        this.mId = id;
    }

    public void setController(int controller){
        this.mController = controller;
    }

    public void setName(String name){
        this.mName = name;
    }

    @Override
    public String toString() {
        return "Switch [id=" + mId + ", controller=" + mController + ", name=" + mName + "]";
    }

}
