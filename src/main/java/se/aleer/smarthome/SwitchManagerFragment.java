package se.aleer.smarthome;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

public class SwitchManagerFragment extends DialogFragment {
    final static public int REQUEST_CODE_ADD = 1;
    final static public int REQUEST_CODE_DELETE = 2;
    final static public String SWITCH_BUNDLE_KEY = "KEY_SWITCH";
    final private String TAG = "SwitchManagerFragment";
    public static final String ARG_ITEM_ID = "SWITCH_MANAGER";
    private Switch mSwitch;
    //OnManagedSwitchListener mCallback;
    ViewHolder mViewHolder;

    private SwitchManagerListener mCallback;

    public interface SwitchManagerListener{
        void onFinishSwitchManaging(Timer timer);
    }

    public void setManagerListener(SwitchManagerListener listener) {
        this.mCallback = listener;
    }

    private View.OnFocusChangeListener mFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                hideKeyboard(v);
            }
        }
    };

    private class ViewHolder
    {
        RadioButton radioButton_single;
        RadioButton radioButton_multi;
        Spinner spinner;
        EditText nameView;
        Button save;
        Button remove;

        ViewHolder(View view){
            this.radioButton_single = (RadioButton) view.findViewById(R.id.radioButton_single);
            this.radioButton_multi = (RadioButton) view.findViewById(R.id.radioButton_multi);
            this.spinner = (Spinner) view.findViewById(R.id.S_M_F_protocol_spinner);
            this.nameView = (EditText) view.findViewById(R.id.S_M_F_name_edit);
            this.save = (Button) view.findViewById(R.id.save_button);
            this.remove = (Button) view.findViewById(R.id.remove_button);
        }
    }

    // This is used by SwitchListFragment to set a switch to edit.
    public void setSwitch(Switch swtch)
    {
        mSwitch = swtch;
    }

    public SwitchManagerFragment()
    {
        //mViewHolder = new ViewHolder();
        mSwitch = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((RemoteControl)getActivity()).showUpButton();
        Bundle b = getArguments();
        if (b != null){
            String gsonString = b.getString("Switch");
            if (gsonString != null){
                Gson gson = new Gson();
                setSwitch(gson.fromJson(gsonString, Switch.class));
            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        // Hide keyboard
        View view = getActivity().getCurrentFocus();
        hideKeyboard(view);
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
                sendResult(REQUEST_CODE_DELETE);
                return true;
            case R.id.action_save_switch:
                if(mSwitch == null){
                    mSwitch = new Switch();
                }
                populateSwitch();
                sendResult(REQUEST_CODE_DELETE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendResult(int REQUEST_CODE) {
        String jsonString = "";
        if (mSwitch != null) {
            Gson gson = new Gson();
            jsonString = gson.toJson(mSwitch);
        }
        Intent intent = new Intent();
        intent.putExtra(SWITCH_BUNDLE_KEY, jsonString);
        getTargetFragment().onActivityResult(getTargetRequestCode(), REQUEST_CODE, intent);
        dismiss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_switch_manager, container,
                false);
        mViewHolder = new ViewHolder(view);
        setupProtocolSpinner(view);
        setupRadioButtons(view);

        // Hide keyboard when focus lost TODO: Remember to add this when more is added to app
        mViewHolder.nameView.setOnFocusChangeListener(mFocusListener);
        // If switch is set, set the rest of the fields
        if(mSwitch != null)
        {
            Log.d(TAG, "Switch in not null");
            // Set name
            mViewHolder.nameView.setText(mSwitch.getName());
        }

        mViewHolder.remove.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendResult(REQUEST_CODE_DELETE);
            }
        });

        mViewHolder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSwitch == null) {
                    mSwitch = new Switch();
                }
                populateSwitch();
                sendResult(REQUEST_CODE_ADD);
            }
        });

        return view;
    }

    private void setupProtocolSpinner(View view)
    {
        // ### Populate spinner ###
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

    public void hideKeyboard(View view) {
        if (view != null)
        {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}