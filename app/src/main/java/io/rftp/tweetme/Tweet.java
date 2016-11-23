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

@RTClassName("Tweetme")
class Tweet extends RTObject {
  private static final String MESSAGE_KEY = "msg";

  public void setMessage(String msg) {
    this.put(MESSAGE_KEY, msg);
  }

  public String getMessage() {
    return this.getString(MESSAGE_KEY);
  }
}
