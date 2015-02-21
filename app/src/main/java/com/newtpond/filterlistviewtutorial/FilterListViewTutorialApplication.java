package com.newtpond.filterlistviewtutorial;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;

//import com.deploygate.sdk.DeployGate;

/**
 * TestNavDrawer
 */
public class FilterListViewTutorialApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, getString(R.string.parse_application_id), getString(R.string.parse_client_key));

        // Initialize Facebook
        String appId = getString(R.string.facebook_app_id);
        ParseFacebookUtils.initialize(appId);

        // Set up Push TODO
        //PushService.setDefaultPushCallback(this, AlertsActivity.class);
        //ParseInstallation.getCurrentInstallation().saveInBackground();

        // DeployGate.install(this);
    }
}
