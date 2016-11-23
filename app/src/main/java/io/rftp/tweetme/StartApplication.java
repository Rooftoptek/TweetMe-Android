/**
 * Copyright (c) 2016-present, RFTP Technologies Ltd.
 * All rights reserved.
 * <p/>
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package io.rftp.tweetme;

import android.app.Application;

import io.rftp.RTACL;
import io.rftp.RTInstallation;
import io.rftp.RTObject;
import io.rftp.Rooftop;
import io.rftp.RooftopFacebookUtils;

public class StartApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();

    //todo: register custom RTObject class
    RTObject.registerSubclass(Tweet.class);

    //todo: initialize RT, RooftopFacebookUtils and RooftopTwitterUtils
    Rooftop.enableLocalDatastore(this);
    Rooftop.initialize(this);

    RTACL acl = new RTACL();
    acl.setAccess(RTACL.READ_PERMISSION);
    RTACL.setDefaultACL(acl, true);

    RooftopFacebookUtils.initialize(this);
    //-end

    //todo: initialize PUSH Notifications
    RTInstallation installation = RTInstallation.getCurrentInstallation();
    installation.put("ownerString", "Tweet{me}");
    installation.setACL(acl);
    installation.saveInBackground();
    //-end

    //override fonts
    FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/WorkSans-Regular.ttf");
    FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/WorkSans-Bold.ttf");
    FontsOverride.setDefaultFont(this, "SERIF", "fonts/WorkSans-Light.ttf");
  }
}
