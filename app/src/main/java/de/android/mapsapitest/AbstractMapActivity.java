package de.android.mapsapitest;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiActivity;

public class AbstractMapActivity extends Activity{
    static final String TAG_ERROR_DIALOG_FRAGMENT = "errorDialog";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.legal:
                startActivity(new Intent(this, LegalNoticeActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected boolean readyToGo() {
        GoogleApiAvailability checker = GoogleApiAvailability.getInstance();
        int status = checker.isGooglePlayServicesAvailable(this);

        if (status == ConnectionResult.SUCCESS) {
            if (getVersionFromPackageManaget(this) >= 2) {
                return true;
            }else {
                Toast.makeText(this, R.string.no_maps, Toast.LENGTH_SHORT).show();
                finish();
            }
        }else if (checker.isUserResolvableError(status)) {
            ErrorDialogFragment.newInstance(status).show(getFragmentManager(), TAG_ERROR_DIALOG_FRAGMENT);
        }else {
            Toast.makeText(this, R.string.no_maps, Toast.LENGTH_SHORT).show();
            finish();
        }
        return false;
    }

    public static class ErrorDialogFragment extends DialogFragment {
        static final String ARG_ERROR_CODE = "errorCode";

        static ErrorDialogFragment newInstance(int errorCode) {
            Bundle args = new Bundle();
            ErrorDialogFragment result = new ErrorDialogFragment();

            args.putInt(ARG_ERROR_CODE, errorCode);
            result.setArguments(args);

            return result;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle args = getArguments();
            GoogleApiAvailability checker = GoogleApiAvailability.getInstance();

            return checker.getErrorDialog(getActivity(),
                    args.getInt(ARG_ERROR_CODE), 0);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            if (getActivity() != null) {
                getActivity().finish();
            }
        }
    }

    private static int getVersionFromPackageManaget(Context context) {
        PackageManager packageManager = context.getPackageManager();
        FeatureInfo[] featureInfos = packageManager.getSystemAvailableFeatures();

        if (featureInfos != null && featureInfos.length > 0) {
            for (FeatureInfo featureInfo : featureInfos) {
                if (featureInfo.name == null) {
                    if (featureInfo.reqGlEsVersion != FeatureInfo.GL_ES_VERSION_UNDEFINED) {
                        return getMajorVersion(featureInfo.reqGlEsVersion);
                    }else {
                        return 1;
                    }
                }
            }
        }
    }

    private static int getMajorVersion(int glsVersion) {
        return (glsVersion & 0xffff0000) >> 16;
    }
}
