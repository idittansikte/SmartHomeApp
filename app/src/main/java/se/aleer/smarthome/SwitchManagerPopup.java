package se.aleer.smarthome;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

/**
 * Created by alex on 2015-08-12.
 */
public class SwitchManagerPopup {

    private SwitchListFragment mSwitchListFragment;
    private Activity mActivity;
    private Switch mExistingSwitch;
    private int mPosition;
    private PopupWindow mPopupWindow;
    SwitchManagerPopup(Activity activity, SwitchListFragment switchListFragment){
        mSwitchListFragment = switchListFragment;
        mActivity = activity;
    }

    public void setSwitch(Switch swtch, int position){
        mExistingSwitch = swtch;
        mPosition = position;
    }

    public void initiatePopup(){
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.add_switch_popup, (ViewGroup) mActivity.findViewById(R.id.add_switch_popup));
        mPopupWindow = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);

        Button buttonCancel = (Button) layout.findViewById(R.id.add_switch_popup_cancel_button);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        Button buttonAdd =(Button) layout.findViewById(R.id.add_switch_popup_add_button);
        Button buttonDel = (Button) layout.findViewById(R.id.add_switch_popup_delete_button);
        final EditText mEdit = (EditText) layout.findViewById(R.id.add_switch_popup_edittext);
        if (mExistingSwitch != null){
            buttonAdd.setText("Edit");
            mEdit.setText(mExistingSwitch.getName());
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mExistingSwitch.setName(mEdit.getText().toString());
                    //mSwitchListFragment.updateSwitch(mExistingSwitch, mPosition);
                    mPopupWindow.dismiss();
                }
            });
            buttonDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSwitchListFragment.deleteSwitch(mExistingSwitch);
                    mPopupWindow.dismiss();
                }
            });
        }
        else {
            buttonDel.setVisibility(View.INVISIBLE);
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSwitchListFragment.add(mEdit.getText().toString());
                    mPopupWindow.dismiss();
                }
            });
        }
    }

    void close(){
        if (mPopupWindow.isShowing())
            mPopupWindow.dismiss();
    }
}
