/**
 * Copyright (c) 2016-present, RFTP Technologies Ltd.
 * All rights reserved.
 * <p>
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package io.rftp.tweetme;

import io.rftp.RTClassName;
import io.rftp.RTObject;
import io.rftp.RTUser;

@RTClassName("Tweetme")
class Tweet extends RTObject {
  public static final String MESSAGE_KEY = "msg";
  public static final String OWNER_KEY = "owner";

  public Tweet() {
  }

  public void setMessage(String msg) {
    this.put(MESSAGE_KEY, msg);
  }

  public String getMessage() {
    return this.getString(MESSAGE_KEY);
  }

  public void setOwner(RTUser owner) {
    this.put(OWNER_KEY, owner);
  }

  public RTUser getOwner() {
    return this.getRTUser(OWNER_KEY);
  }
}
