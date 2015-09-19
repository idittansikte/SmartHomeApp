package se.aleer.smarthome;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class SwitchManagerFragment extends Fragment {

    final private String TAG = "SwitchManagerFragment";
    public static final String ARG_ITEM_ID = "SWITCH_MANAGER";
    private Switch mSwitch;
    OnManagedSwitchListener mCallback;
    ViewHolder mViewHolder;

    private class ViewHolder
    {
        RadioButton radioButton_single;
        RadioButton radioButton_multi;
        Spinner spinner;
        EditText nameView;
    }

    public interface OnManagedSwitchListener {
        void onManagedSwitch(Switch swtch, boolean remove);
    }
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnManagedSwitchListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnEditSwitchListener");
        }
    }

    // This is used by SwitchListFragment to set a switch to edit.
    public void setSwitch(Switch swtch)
    {
        mSwitch = swtch;
    }

    public SwitchManagerFragment()
    {
        mViewHolder = new ViewHolder();
        mSwitch = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((RemoteControl)getActivity()).showUpButton();
    }

    @Override
    public void onPause(){
        super.onPause();
        /*getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        super.onDestroy();
        mSwitch = null;*/
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_switch_manager, menu);
        if(mSwitch == null) {
            menu.findItem(R.id.action_remove_switch).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_remove_switch:
                mCallback.onManagedSwitch(mSwitch, true);
                return true;
            case R.id.action_save_switch:
                if(mSwitch == null){
                    mSwitch = new Switch();
                }
                populateSwitch();
                mCallback.onManagedSwitch(mSwitch, false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_switch_manager, container,
                false);

        setupProtocolSpinner(view);
        setupRadioButtons(view);

        mViewHolder.nameView = (EditText) view.findViewById(R.id.S_M_F_name_edit);

        // If switch is set, set the rest of the fields
        if(mSwitch != null)
        {
            Log.d(TAG, "Switch in not null");
            // Set name
            mViewHolder.nameView.setText(mSwitch.getName());
        }
        return view;
    }

    private void setupProtocolSpinner(View view)
    {
        // ### Populate spinner ###
        mViewHolder.spinner = (Spinner) view.findViewById(R.id.S_M_F_protocol_spinner);
        // Create a spinner adapter using protocol array ad default spinner layout (android)
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.protocol_array, R.layout.spinner);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        //spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply adapter to spinner
        mViewHolder.spinner.setAdapter(spinnerAdapter);
        // ### Spinner item selected listener ###
        mViewHolder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get selected item text
                String selectedItemText = parent.getItemAtPosition(position).toString();
                Toast toastSpinnerSelection = Toast.makeText(getActivity(), selectedItemText, Toast.LENGTH_SHORT);
                //display the toast notification on user interface
                //set the toast display location
                toastSpinnerSelection.setGravity(Gravity.LEFT | Gravity.BOTTOM, 20, 150);
                toastSpinnerSelection.show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // If edit-mode, set switch protocol
        if(mSwitch != null)
        {
            int protocol = mSwitch.getProtocol();
            Resources res = getResources();
            String protocolName = res.getString(R.string.protocol_proove);
            if ( protocol == res.getInteger(R.integer.protocol_proove)){
                protocolName = res.getString(R.string.protocol_proove);
            } else if (protocol == res.getInteger(R.integer.protocol_nexa)){
                protocolName = res.getString(R.string.protocol_nexa);
            }
            Log.d(TAG, "Switch have protocol: " + protocolName + " set");
            mViewHolder.spinner.setSelection(spinnerAdapter.getPosition(protocolName));
        }

    } // ### End of spinner setup ###

    private void setupRadioButtons(View view)
    {

        // ### Make radioButtons for switch-type work ###
        mViewHolder.radioButton_single = (RadioButton) view.findViewById(R.id.radioButton_single);
        mViewHolder.radioButton_multi = (RadioButton) view.findViewById(R.id.radioButton_multi);
        final RelativeLayout singleLayout = ((RelativeLayout) view.findViewById(R.id.S_M_F_type_single));
        final RelativeLayout multiLayout = ((RelativeLayout) view.findViewById(R.id.S_M_F_type_multi));
        // Initialize them
        mViewHolder.radioButton_multi.setChecked(false);
        mViewHolder.radioButton_single.setChecked(false);
        // If add/new switch
        if(mSwitch == null) {
            // Enable both multi and single (start with single)
            mViewHolder.radioButton_single.setEnabled(true);
            mViewHolder.radioButton_multi.setEnabled(true);
            mViewHolder.radioButton_single.setChecked(true);
            // Set on click listeners
            mViewHolder.radioButton_single.setOnClickListener(new RadioButton.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Disable multi and enable this
                    mViewHolder.radioButton_multi.setChecked(false);
                    mViewHolder.radioButton_single.setChecked(true);
                    singleLayout.setVisibility(View.VISIBLE);
                    multiLayout.setVisibility(View.GONE);

                }
            });
            mViewHolder.radioButton_multi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Disable single and enable this
                    mViewHolder.radioButton_single.setChecked(false);
                    mViewHolder.radioButton_multi.setChecked(true);
                    multiLayout.setVisibility(View.VISIBLE);
                    singleLayout.setVisibility(View.GONE);
                    //
                }
            });
        }else { // If switch is set (edit-mode)
            // If single
            if(mSwitch.isSingle()){
                // Disable multi
                mViewHolder.radioButton_single.setChecked(true);
                mViewHolder.radioButton_multi.setEnabled(false);
                mViewHolder.radioButton_multi.setActivated(false);
            }
            else{ // Multi
                // Disable single
                mViewHolder.radioButton_multi.setChecked(true);
                mViewHolder.radioButton_single.setEnabled(false);
                mViewHolder.radioButton_single.setActivated(false);
            }

        }
    }

    void populateSwitch(){
        mSwitch.setName(mViewHolder.nameView.getText().toString());
        // If single-switch
        if(mViewHolder.radioButton_single.isChecked())
        {
            mSwitch.setSingle(true);
            String selectedItem = (mViewHolder.spinner.getSelectedItem()).toString();
            Resources res = getResources();
            if (selectedItem.equals(res.getString(R.string.protocol_proove))) {
                mSwitch.setProtocol(res.getInteger(R.integer.protocol_proove));
            } else if (selectedItem.equals(res.getString(R.string.protocol_nexa))){
                mSwitch.setProtocol(res.getInteger(R.integer.protocol_nexa));
            } else{
                mSwitch.setProtocol(-1); // Something is wrong.
                Log.e(TAG, "Protocol in spinner not defined...");
            }
        }
        else{ // If multi-switch
            mSwitch.setSingle(false);
        }
    }
}