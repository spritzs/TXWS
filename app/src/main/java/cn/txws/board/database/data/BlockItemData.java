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

import android.database.Cursor;
import android.graphics.Bitmap;

import cn.txws.board.database.DatabaseHelper;


/**
 * Data model object used to power BlockItemData, which may be displayed either in
 * our list.
 */
public class BlockItemData {
    // Keeps the contact data in the form of RecipientEntry that RecipientEditTextView can
    // directly use.
    private String mName;
    private String mBlockID;
    private String mImageResource;
    private long mTime;

    public final static int INDEX_ID=0;
    public final static int INDEX_NAME=1;
    public final static int INDEX_BLOCKID=2;
    public final static int INDEX_IMAGE=3;
    public final static int INDEX_SORTTIME=4;

    public static final String[] PROJECTION = new String[] {
            DatabaseHelper.BlockColumns._ID,    //0
            DatabaseHelper.BlockColumns.NAME,   //1
            DatabaseHelper.BlockColumns.BLOCK_ID,         // 2
            DatabaseHelper.BlockColumns.IMAGE,          // 3
            DatabaseHelper.BlockColumns.SORT_TIMESTAMP,                       // 4
    };

    /**
     * Bind to a contact cursor in the contact list.
     */
    public synchronized void bind(final Cursor cursor) {
        try {
            mName = cursor.getString(INDEX_NAME);
            mBlockID = cursor.getString(INDEX_BLOCKID);
            mImageResource = cursor.getString(INDEX_IMAGE);
            mTime=cursor.getLong(INDEX_SORTTIME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * Bind to a produced by the text
     */
    public void bind(String name, final String blockid,
                     final String imageresource, final long time) {
        mName = name;
        mBlockID = blockid;
        mImageResource = imageresource;
        mTime=time;
    }

    public String getName() {
        return mName;
    }
    public String getBlockID() {
        return mBlockID;
    }
    public String getImage() {
        return mImageResource;
    }
    public long getTime() {
        return mTime;
    }



}
