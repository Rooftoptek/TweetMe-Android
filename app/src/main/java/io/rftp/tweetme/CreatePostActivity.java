/**
 * Copyright (c) 2016-present, RFTP Technologies Ltd.
 * All rights reserved.
 * <p/>
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package io.rftp.tweetme;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import io.rftp.RTException;
import io.rftp.RTSaveCallback;
import io.rftp.RTUser;

public class CreatePostActivity extends AppCompatActivity {
  private EditText mPostBodyEditTextField;
  private ProgressBar mLoginProgressBar;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_post);

    initializeUI();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_post_send) {
      String message = mPostBodyEditTextField.getText().toString();

      sendPost(message);

      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void sendPost(String message) {
    //todo: create RTObject
    turnProgressBar(true);
    Tweet post = new Tweet();
    post.put(Tweet.MESSAGE_KEY, message);
    post.put(Tweet.OWNER_KEY, RTUser.getCurrentUser());
    post.saveInBackground(new RTSaveCallback() {
      @Override
      public void done(RTException e) {
        //handle server response
        turnProgressBar(false);
        if (e == null) {
          startMainActivity();
        } else {
          e.printStackTrace();
          Toast.makeText(CreatePostActivity.this, "Oops, retry to send post", Toast.LENGTH_LONG).show();
        }
      }
    });
    //-end
  }

  private void startMainActivity() {
    Intent intent = new Intent(this, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_create_post, menu);
    return true;
  }

  private void initializeUI() {
    mPostBodyEditTextField = (EditText)findViewById(R.id.createPostEditText);
    mLoginProgressBar = (ProgressBar)findViewById(R.id.sendPostProgressBr);

    MainActivity.setActionBarStyle(this);
  }

  private void turnProgressBar(boolean isOn) {
//    int visibility = isOn ? View.VISIBLE : View.INVISIBLE;
//    mLoginProgressBar.setVisibility(visibility);
    mLoginProgressBar.setIndeterminate(isOn);
  }
}
