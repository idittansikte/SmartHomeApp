package se.aleer.smarthome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Created by alex on 2015-09-19.
 */
public class Alerter {

    private Activity mActivity;

    Alerter(Activity activity)
    {
        mActivity = activity;
    }

    public void showAlert(String title, String message) {
        if (mActivity != null && !mActivity.isFinishing()) {
            AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
                    .create();
            alertDialog.setTitle(title);
            alertDialog.setMessage(message);
            alertDialog.setCancelable(false);

            // setting OK Button
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            //getFragmentManager().popBackStackImmediate();
                        }
                    });
            alertDialog.show();
        }
    }
}
