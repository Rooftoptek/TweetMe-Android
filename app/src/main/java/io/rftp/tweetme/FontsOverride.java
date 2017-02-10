/**
 * Copyright (c) 2016-present, RFTP Technologies Ltd.
 * All rights reserved.
 * <p>
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package io.rftp.tweetme;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;

class FontsOverride {

  public static void setDefaultFont(Context context,
                                    String staticTypefaceFieldName, String fontAssetName) {
    final Typeface regular = Typeface.createFromAsset(context.getAssets(),
        fontAssetName);
    replaceFont(staticTypefaceFieldName, regular);
  }

  private static void replaceFont(String staticTypefaceFieldName,
                                  final Typeface newTypeface) {
    try {
      final Field staticField = Typeface.class
          .getDeclaredField(staticTypefaceFieldName);
      staticField.setAccessible(true);
      staticField.set(null, newTypeface);
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

}
