/*
 * Copyright (C) 2015 The Android Open Source Project
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
 */

package cn.txws.board.database.data;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;


import cn.txws.board.database.BlockContentProvider;
import cn.txws.board.database.BoundCursorLoader;
import cn.txws.board.database.BugleDatabaseOperations;
import cn.txws.board.database.binding.BindableData;
import cn.txws.board.database.binding.BindingBase;

/**
 * Created by zqs on 2018/2/26 .
 * Services data needs for BlockLoaderData.
 */
public class BlockLoaderData extends BindableData implements
        LoaderManager.LoaderCallbacks<Cursor> {
    public interface BlockLoaderDataListener {
        void onBlockUpdated(BlockLoaderData data, Cursor cursor);
    }

    private final String TAG = "BlockLoaderData";
    private static final String BINDING_ID = "bindingId";
    private final Context mContext;
    private LoaderManager mLoaderManager;
    private BlockLoaderDataListener mListener;

    public BlockLoaderData(final Context context,
                           final BlockLoaderDataListener listener) {
        mListener = listener;
        mContext = context;
    }

    private static final int BLOCKS_LOADER = 1;

    @Override
    public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        final String bindingId = args.getString(BINDING_ID);
        // Check if data still bound to the requesting ui element
        if (isBound(bindingId)) {
            switch (id) {
                case BLOCKS_LOADER: {
                    return new BoundCursorLoader(bindingId, mContext, BlockContentProvider.BLOCKS_URI,
                            BlockItemData.PROJECTION, null, null, BlockItemData.PROJECTION[BlockItemData.INDEX_SORTTIME]+" desc");
                }
                default:
                    break;
            }
        } else {
            Log.w(TAG, "Loader created after unbinding MainActivity");
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
        final BoundCursorLoader cursorLoader = (BoundCursorLoader) loader;
        if (isBound(cursorLoader.getBindingId())) {
            switch (loader.getId()) {
                case BLOCKS_LOADER:
                    mListener.onBlockUpdated(this, data);
                    break;

                default:
                    break;
            }
        } else {
            Log.w(TAG,
                    "Loader finished after unbinding MainActivity");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLoaderReset(final Loader<Cursor> loader) {
        final BoundCursorLoader cursorLoader = (BoundCursorLoader) loader;
        if (isBound(cursorLoader.getBindingId())) {
            switch (loader.getId()) {
                case BLOCKS_LOADER:
                    if(mListener!=null) {
                        mListener.onBlockUpdated(this, null);
                    }
                    break;

                default:
                    break;
            }
        } else {
            Log.w(TAG, "Loader reset after unbinding MainActivity");
        }
    }

    public void init(final LoaderManager loaderManager,
            final BindingBase<BlockLoaderData> binding) {
        final Bundle args = new Bundle();
        args.putString(BINDING_ID, binding.getBindingId());
        mLoaderManager = loaderManager;
        mLoaderManager.initLoader(BLOCKS_LOADER, args, this);
    }

    @Override
    protected void unregisterListeners() {
        mListener = null;

        // This could be null if we bind but the caller doesn't init the BindableData
        if (mLoaderManager != null) {
            mLoaderManager.destroyLoader(BLOCKS_LOADER);
            mLoaderManager = null;
        }
    }

}
