package se.aleer.smarthome;


public class LightSensor extends ItemList {

    private int mOn;
    private int mOff;

    public LightSensor(int id){
        this(id, "");
    }

    public LightSensor(int id, String name){
        super(id, name);
    }

    public void setValueOn(int v){
        mOn = v;
    }

    public int getValueOn(){
        return mOn;
    }

    public void setValueOff(int v){
        mOff = v;
    }

    public int getValueOff(){
        return mOff;
    }

    public String toString(){
        return getId() + ":" + this.getValueOn() + ":" + this.getValueOff() + ":" + switchListToString();
    }

    @Override
    public boolean equals(Object rhs){
        return (rhs instanceof LightSensor && this.getId() == ((LightSensor)rhs).getId());
    }
}
