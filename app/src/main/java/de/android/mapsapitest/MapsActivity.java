package de.android.mapsapitest;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class MapsActivity extends AbstractMapActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (readyToGo()) {
            setContentView(R.layout.activity_main);
        }
    }
}
