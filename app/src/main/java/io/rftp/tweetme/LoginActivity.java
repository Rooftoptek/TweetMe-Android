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
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import io.rftp.RTException;
import io.rftp.RTLogInCallback;
import io.rftp.RTSignUpCallback;
import io.rftp.RTUser;
import io.rftp.RooftopFacebookUtils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

  private EditText mLoginEditTextField;
  private EditText mPasswordEditTextField;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    getSupportActionBar().hide();

    initializeUI();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.signInButton:
        signIn();
        break;
      case R.id.signUpTV:
        signUp();
        break;
      case R.id.loginFacebookButton:
        singInWithFacebook();
        break;
    }
  }

  private void signIn() {
    String login = getLoginFromEditTextField();
    String password = getPasswordFromEditTextField();

    //todo: create RTUser
    RTUser.logInInBackground(login, password, new RTLogInCallback() {
      @Override
      public void done(RTUser rooftopUser, RTException e) {
        if (e == null) {
          startMainActivity();
        } else {
          Toast.makeText(LoginActivity.this, "Oops, retry your attempt. " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
      }
    });
    //-end
  }

  private void signUp() {
    String login = getLoginFromEditTextField();
    String password = getPasswordFromEditTextField();

    //todo: create RTUser
    RTUser newUser = new RTUser();
    newUser.setUsername(login);
    newUser.setPassword(password);
    newUser.signUpInBackground(new RTSignUpCallback() {
      @Override
      public void done(RTException e) {
        if (e == null) {
          startMainActivity();
        } else {
          e.printStackTrace();
          Toast.makeText(LoginActivity.this, "Oops, retry your attempt. " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
      }
    });
    //-end
  }

  private void singInWithFacebook() {
    //todo: use RooftopFacebookUtils
    RooftopFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, null, new RTLogInCallback() {
      @Override
      public void done(RTUser rooftopUser, RTException e) {
        if (e == null) {
          if (rooftopUser != null) {
            startMainActivity();
          } else {
            RooftopFacebookUtils.logOut();
          }
        } else {
          Toast.makeText(LoginActivity.this, "Oops, retry your attempt. " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
      }
    });
    //-end
  }

  //todo: handle result of calling FB auth form
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    RooftopFacebookUtils.onActivityResult(requestCode, resultCode, data);
  }
  //-end

  private String getPasswordFromEditTextField() {
    return mPasswordEditTextField.getText().toString();
  }

  private String getLoginFromEditTextField() {
    return mLoginEditTextField.getText().toString();
  }

  private void startMainActivity() {
    Intent intent = new Intent(this, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
  }

  private void initializeUI() {
    mLoginEditTextField = (EditText)findViewById(R.id.loginEditText);
    mPasswordEditTextField = (EditText)findViewById(R.id.passwordEditText);

    ImageView signInButton = (ImageView)findViewById(R.id.signInButton);
    TextView signUpTV = (TextView)findViewById(R.id.signUpTV);
    ImageView facebookLoginButton = (ImageView)findViewById(R.id.loginFacebookButton);
    TextView nameOne = (TextView)findViewById(R.id.appNameOneTV);
    TextView nameTwo = (TextView)findViewById(R.id.appNameTwoTV);

    //set on click listeners
    signInButton.setOnClickListener(this);
    signUpTV.setOnClickListener(this);
    facebookLoginButton.setOnClickListener(this);

    //set fonts
    mLoginEditTextField.setTypeface(Typeface.SERIF);
    mPasswordEditTextField.setTypeface(Typeface.SERIF);
    signUpTV.setTypeface(Typeface.DEFAULT);
    nameOne.setTypeface(Typeface.MONOSPACE);
    nameTwo.setTypeface(Typeface.MONOSPACE);
  }
}
