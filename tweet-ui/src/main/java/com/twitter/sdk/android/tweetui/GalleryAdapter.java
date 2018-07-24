/*
 * Copyright (C) 2015 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.twitter.sdk.android.tweetui;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.tweetui.internal.GalleryImageView;
import com.twitter.sdk.android.tweetui.internal.SwipeToDismissTouchListener;

import java.util.ArrayList;
import java.util.List;

class GalleryAdapter extends PagerAdapter {
    final List<MediaEntity> items = new ArrayList<>();
    final Context context;
    final SwipeToDismissTouchListener.Callback callback;
    int defaultItemIndex;

    GalleryAdapter(Context context, SwipeToDismissTouchListener.Callback callback) {
        this.context = context;
        this.callback = callback;
    }

    GalleryAdapter(Context context, SwipeToDismissTouchListener.Callback callback, int defaultItemIndex) {
        this(context,callback);
        this.defaultItemIndex = defaultItemIndex;
    }

    void addAll(List<MediaEntity> entities) {
        items.addAll(entities);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final GalleryImageView root = new GalleryImageView(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(defaultItemIndex == position){
                root.setTransitionName("image");
                root.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        root.getViewTreeObserver().removeOnPreDrawListener(this);
                        if (context instanceof Activity) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                ((Activity) context).startPostponedEnterTransition();
                            }
                        }
                        return true;
                    }
                });
            }
        }

        root.setSwipeToDismissCallback(callback);

        container.addView(root);

        final MediaEntity entity = items.get(position);
        Picasso.with(context).load(entity.mediaUrlHttps).into(root);

        return root;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
