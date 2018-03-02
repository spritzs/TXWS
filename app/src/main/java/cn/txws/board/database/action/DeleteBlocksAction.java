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

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

import cn.robotpen.utils.FileUtils;
import cn.txws.board.database.BlockContentProvider;
import cn.txws.board.database.BugleDatabaseOperations;
import cn.txws.board.database.DatabaseWrapper;
import cn.txws.board.database.data.DataModel;
import cn.txws.board.util.AppUtil;


/**
 * Created by zqs on 2018/2/26 .
 * Action used to delete a single message.
 */
public class DeleteBlocksAction extends Action implements Parcelable {
    private static final String TAG = "DeleteLotBlockAction";

    public static void deleteBlocks(String[] blockids) {
        final DeleteBlocksAction action = new DeleteBlocksAction(blockids);
        action.start();
    }

    public static void deleteBlock(String blockid) {
        final DeleteBlocksAction action = new DeleteBlocksAction(new String[]{blockid});
        action.start();
    }


    private static final String KEY_BLOCK_ID = "block_id";

    private DeleteBlocksAction(String[] blockids) {
        super();
        actionParameters.putStringArray(KEY_BLOCK_ID,blockids);
    }

    // Doing this work in the background so that we're not competing with sync
    // which could bring the deleted message back to life between the time we deleted
    // it locally and deleted it in telephony (sync is also done on doBackgroundWork).
    //
    // Previously this block of code deleted from telephony first but that can be very
    // slow (on the order of seconds) so this was modified to first delete locally, trigger
    // the UI update, then delete from telephony.
    @Override
    protected Bundle doBackgroundWork() {

        return null;
    }

    /**
     * Delete the blocks.
     */
    @Override
    protected Object executeAction() {
    	  final DatabaseWrapper db = DataModel.get().getDatabase();

          // First find the thread id for this conversation.
          final String[] blockids = actionParameters.getStringArray(KEY_BLOCK_ID);

          if (blockids!=null&&blockids.length>0) {
              // delete blocks
              BugleDatabaseOperations.deleteBlocks(db,blockids);
              BlockContentProvider.notifyAllBlocksChanged();
              for(int i=0;i<blockids.length;i++) {
                  File file = new File(AppUtil.SAVEDIR + blockids[i] + ".jpg");
                  if (file.exists()) {
                      file.delete();
                  }
              }
          }
        return null;
    }

    private DeleteBlocksAction(final Parcel in) {
        super(in);
    }

    public static final Creator<DeleteBlocksAction> CREATOR
            = new Creator<DeleteBlocksAction>() {
        @Override
        public DeleteBlocksAction createFromParcel(final Parcel in) {
            return new DeleteBlocksAction(in);
        }

        @Override
        public DeleteBlocksAction[] newArray(final int size) {
            return new DeleteBlocksAction[size];
        }
    };

    @Override
    public void writeToParcel(final Parcel parcel, final int flags) {
        writeActionToParcel(parcel, flags);
    }
}
