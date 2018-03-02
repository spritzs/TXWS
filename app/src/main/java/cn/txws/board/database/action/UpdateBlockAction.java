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

package cn.txws.board.database.action;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import cn.txws.board.database.BlockContentProvider;
import cn.txws.board.database.BugleDatabaseOperations;
import cn.txws.board.database.DatabaseWrapper;
import cn.txws.board.database.data.BlockItemData;
import cn.txws.board.database.data.DataModel;

/**
 * Created by zqs on 2018/2/26 .
 * UpdateBlockAction
 */
public class UpdateBlockAction extends Action implements Parcelable {
    private static final String TAG = "UpdateBlockAction";

    private static final String KEY_NAME = "block_name";
    private static final String KEY_BLOCKID= "block_blockid";
    private static final String KEY_IMAGE= "block_img";
    private static final String KEY_SORTTIME= "block_sorttime";

    /**
     * update block (no listener)
     */
    public static void updateBlock(final BlockItemData data) {
        final UpdateBlockAction action = new UpdateBlockAction(data);
        action.start();
    }

    public static void updateBlock(String name,String blockid,String image,long time) {
        final UpdateBlockAction action = new UpdateBlockAction(name,blockid,image,time);
        action.start();
    }


    private UpdateBlockAction(final BlockItemData data) {
        super();
        actionParameters.putString(KEY_NAME,data.getName());
        actionParameters.putString(KEY_BLOCKID,data.getBlockID());
        actionParameters.putString(KEY_IMAGE,data.getImage());
        actionParameters.putLong(KEY_SORTTIME, data.getTime());
    }

    private UpdateBlockAction(String name, String blockid, String image, long time) {
        super();
        actionParameters.putString(KEY_NAME,name);
        actionParameters.putString(KEY_BLOCKID,blockid);
        actionParameters.putString(KEY_IMAGE,image);
        actionParameters.putLong(KEY_SORTTIME, time);
    }

    /**
     * update block
     */
    @Override
    protected Object executeAction() {
        Log.i(TAG, "updateBlockAction: update block");
        final String name=actionParameters.getString(KEY_NAME);
        final String blockid=actionParameters.getString(KEY_BLOCKID);
        final String image=actionParameters.getString(KEY_IMAGE);
        final long timestamp=actionParameters.getLong(KEY_SORTTIME);

        final DatabaseWrapper db = DataModel.get().getDatabase();
        BugleDatabaseOperations.updateBlock(db,blockid,name,image,timestamp);
        BlockContentProvider.notifyAllBlocksChanged();
        return null;
    }



    private UpdateBlockAction(final Parcel in) {
        super(in);
    }

    public static final Creator<UpdateBlockAction> CREATOR
            = new Creator<UpdateBlockAction>() {
        @Override
        public UpdateBlockAction createFromParcel(final Parcel in) {
            return new UpdateBlockAction(in);
        }

        @Override
        public UpdateBlockAction[] newArray(final int size) {
            return new UpdateBlockAction[size];
        }
    };

    @Override
    public void writeToParcel(final Parcel parcel, final int flags) {
        writeActionToParcel(parcel, flags);
    }
}
