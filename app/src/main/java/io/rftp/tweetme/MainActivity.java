/**
 * Copyright (c) 2016-present, RFTP Technologies Ltd.
 * All rights reserved.
 * <p/>
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package io.rftp.tweetme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bolts.Continuation;
import bolts.Task;
import io.rftp.RTException;
import io.rftp.RTObject;
import io.rftp.RTQuery;
import io.rftp.RTRapid;
import io.rftp.RTRapidCallback;
import io.rftp.RTUser;
import io.rftp.RooftopFacebookUtils;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

  private ListView mTwittyListView;
  private MyAdapter mListViewAdapter;
  private SwipeRefreshLayout mSwipeRefreshLayout;

  private boolean mIsAlreadyLoggedIn = false;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    getMenuState();

    initializeUI();

    requestForData();
  }

  /**
   * Steps of requestForData() method:
   * - Create task for local data
   * - Get last update date
   * - Create task for request all posts after last update date
   * - Pin objects from response to local data storage
   * - Compare lists of adapter's data and remote duplicated objs
   * - Remove duplicated objs
   * - Add all objs from response
   */
  private void requestForData() {
    //todo: use parseQuery for request existed objects
    Task<Void> task = Task.forResult(null);

    task.onSuccessTask(new FindLocalTwittiesContinuation())
        .onSuccessTask(new FindNewRemoteTwittiesContinuation())
        .continueWith(new Continuation<Void, Void>() {
          @Override
          public Void then(Task<Void> task) throws Exception {
            if (mSwipeRefreshLayout != null) {
              mSwipeRefreshLayout.setRefreshing(false);
            }
            if (task.isFaulted()) {
              task.getError().printStackTrace();
            }
            return null;
          }
        });
    //-end
  }

  private void resetListViewAdapter(List<RTObject> data) {
    mListViewAdapter = new MyAdapter(this, data);
    mTwittyListView.setAdapter(mListViewAdapter);
  }

  private class MyAdapter extends ArrayAdapter<RTObject> {

    final Context mContext;
    final int mResource;

    MyAdapter(Context context, List<RTObject> objects) {
      super(context, android.R.layout.simple_list_item_1, objects);
      mContext = context;
      mResource = android.R.layout.simple_list_item_1;
    }

    @Override
    public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
      if (convertView == null) {
        convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
      }

      final TextView text = (TextView) convertView;
      final RTObject item = getItem(position);
      String msg = item != null ? item.getString("msg") : null;
      text.setText(msg);
      return convertView;
    }

    /*local*/ void insertAllToHead(@NonNull List<Tweet> collection) {
      setNotifyOnChange(false);
      for (int idx = 0; idx < collection.size(); ++idx) {
        insert(collection.get(idx), idx);
      }
      setNotifyOnChange(true);
      notifyDataSetChanged();
    }

    Date getLastDate() {

      if (getCount() > 0) {
        RTObject firsItem = getItem(0);
        if (firsItem != null) return firsItem.getUpdatedAt();
      }
      return new Date(0);
    }
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    callCloudCodeFunction();
  }

  private void callCloudCodeFunction() {
    //todo
    //any test params
    RTRapid.invokeInBackground("Slogan", new HashMap<String, Object>(), new RTRapidCallback<String>() {
      @Override
      public void done(String s, RTException e) {
        if (e == null) {
          showDialogWithMessage(s);
        } else {
          e.printStackTrace();
          Toast.makeText(MainActivity.this, "Oops, try again. " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
      }
    });
    //-end
  }

  private void showDialogWithMessage(String message) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    builder.setMessage(message)
        .setTitle("Rooftop");

    builder.create().show();
  }

  private void showDialogWithMessageCustom(String message) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    TextView title = new TextView(this);
    title.setText("Rooftop");
    title.setGravity(Gravity.CENTER_HORIZONTAL);
    title.setTextColor(Color.parseColor("#de000000"));
    title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
    title.setPadding(24, 24, 24, 0);

    builder.setMessage(message)
        .setCustomTitle(title)
        .setPositiveButton("Ok", null);

    AlertDialog dialog = builder.create();

    dialog.show();
    TextView messageTV = (TextView)dialog.findViewById(android.R.id.message);
    if (messageTV != null) messageTV.setGravity(Gravity.CENTER_HORIZONTAL);
  }

  private void setActionBarMenuState(Menu menu) {
    MenuItem loginItem = menu.findItem(R.id.action_login);
    MenuItem logoutItem = menu.findItem(R.id.action_logout);
    MenuItem createPostItem = menu.findItem(R.id.action_create_post);
    loginItem.setVisible(!mIsAlreadyLoggedIn);
    logoutItem.setVisible(mIsAlreadyLoggedIn);
    createPostItem.setVisible(mIsAlreadyLoggedIn);
  }

  private void getMenuState() {
    //todo: get it from current rooftop user
    mIsAlreadyLoggedIn = RTUser.getCurrentUser() != null;
    //-end
  }

  private void initializeUI() {
    mTwittyListView = (ListView)findViewById(R.id.twittyListView);

    mTwittyListView.setOnItemClickListener(this);

    mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.twittyListViewSwipeRefreshLayout);
    mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        requestForDataUpdates();
        mSwipeRefreshLayout.setRefreshing(true);
      }
    });

    resetListViewAdapter(new ArrayList<RTObject>());
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);

    setActionBarMenuState(menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_login) {
      startLoginActivity();
      return true;
    } else if (item.getItemId() == R.id.action_create_post)  {
      startCreatePostActivity();
      return true;
    } else if (item.getItemId() == R.id.action_logout) {
      /*todo SDK*/RTUser.logOutInBackground();
      /*todo SDK*/RooftopFacebookUtils.logOut();
      startLoginActivity();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void startCreatePostActivity() {
    Intent intent = new Intent(this, CreatePostActivity.class);
    startActivity(intent);
  }

  private void startLoginActivity() {
    Intent intent = new Intent(this, LoginActivity.class);
    startActivity(intent);
  }

  private class FindLocalTwittiesContinuation implements Continuation<Void, Task<Date>> {
    @Override
    public Task<Date> then(Task<Void> task) throws Exception {
      //todo SDK usage:
      RTQuery<Tweet> queryPosts = RTQuery.getQuery(Tweet.class);
      queryPosts.fromLocalDatastore();
      queryPosts.orderByDescending("updatedAt");
      return queryPosts.findInBackground()
          .onSuccessTask(new Continuation<List<Tweet>, Task<Date>>() {
            @Override
            public Task<Date> then(Task<List<Tweet>> task) throws Exception {
              List<Tweet> localData = task.getResult();
              mListViewAdapter.insertAllToHead(localData);
              Date lastTwittyDate = localData.isEmpty() ? new Date(0) : localData.get(0).getUpdatedAt();

              return Task.forResult(lastTwittyDate);
            }}, Task.UI_THREAD_EXECUTOR);
      //-end
    }
  }

  private class FindNewRemoteTwittiesContinuation implements Continuation<Date, Task<Void>> {
    @Override
    public Task<Void> then(Task<Date> task) throws Exception {
      Date lastTwittyDate = task.getResult();
      //todo SDK usage:
      RTQuery<Tweet> queryPosts = RTQuery.getQuery(Tweet.class);
      if (lastTwittyDate != null) {
        queryPosts.whereGreaterThan("updatedAt", lastTwittyDate);
      }
      return queryPosts.findInBackground()
          .onSuccessTask(new Continuation<List<Tweet>, Task<Void>>() {
            @Override
            public Task<Void> then(Task<List<Tweet>> task) throws Exception {
              final List<Tweet> remoteData = task.getResult();
              mListViewAdapter.insertAllToHead(remoteData);

              return RTObject.pinAllInBackground(remoteData);
            }
          }, Task.UI_THREAD_EXECUTOR);
      //-end
    }
  }

  private void requestForDataUpdates() {
    //todo: use fb bolt tasks for request existed objects
    Task<Date> task = Task.forResult(mListViewAdapter.getLastDate());

    task.onSuccessTask(new FindNewRemoteTwittiesContinuation())
        .continueWith(new Continuation<Void, Void>() {
          @Override
          public Void then(Task<Void> task) throws Exception {
            if (mSwipeRefreshLayout != null) {
              mSwipeRefreshLayout.setRefreshing(false);
            }
            if (task.isFaulted()) {
              task.getError().printStackTrace();
            }
            return null;
          }
        }, Task.UI_THREAD_EXECUTOR);

    //-end
  }

}
