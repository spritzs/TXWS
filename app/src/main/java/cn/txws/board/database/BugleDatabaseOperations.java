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

package cn.txws.board.database;

import android.content.ContentValues;
import android.database.Cursor;

import android.util.Log;

import com.google.common.base.Joiner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import cn.txws.board.database.data.BlockItemData;


/**
 * This class manages updating our local database
 */
public class BugleDatabaseOperations {

    private static final String TAG = "BugleDatabaseOperations";


    /**
     * Get or create a conversation based on provided participants
     *
     * @param db the database
     * @param data BlockItemData
     * @return a Block id
     */
    public static String getOrCreateBlock(final DatabaseWrapper db, BlockItemData data) {

        // Check to see if this conversation is already in out local db cache
        String exit_id = BugleDatabaseOperations.getExistingBlock(db, data.getBlockID());

        if (exit_id == null) {
            db.beginTransaction();
            try {
                exit_id = BugleDatabaseOperations.createBlockInTransaction(
                        db, data.getName(),data.getBlockID(),data.getImage(),System.currentTimeMillis());
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }else{
            BugleDatabaseOperations.updateBlock(
                    db, data.getName(),data.getBlockID(),data.getImage(),System.currentTimeMillis());
        }
        return exit_id;
    }

    static String createBlockInTransaction(final DatabaseWrapper dbWrapper,String name,String blockid,String image,long time) {
        // We want conversation and participant creation to be atomic
        Log.e(TAG, "BugleDatabaseOperations : add block by createBlockInTransaction");
        final ContentValues values = new ContentValues();
        values.put(DatabaseHelper.BlockColumns.NAME, name);
        values.put(DatabaseHelper.BlockColumns.BLOCK_ID, blockid);
        values.put(DatabaseHelper.BlockColumns.IMAGE, image);
        values.put(DatabaseHelper.BlockColumns.SORT_TIMESTAMP, time);

        final long rowId = dbWrapper.insert(DatabaseHelper.BLOCK_TABLE, null,
                values);

        if (rowId == -1) {
            Log.e(TAG, "BugleDatabaseOperations : failed to insert block into table");
            return null;
        }

        final String mRowId = Long.toString(rowId);

        return mRowId;
    }



    /**
     * Get a Block by the ID.
     *
     * @param dbWrapper     The database
     * @param blockid      Block ID
     */
    public static String getExistingBlock(final DatabaseWrapper dbWrapper,
            final String blockid) {
        String _ID=null;
        Cursor cursor = null;
        try{
        dbWrapper.beginTransaction();
        try {
            // Look for an existing conversation in the db with this thread id
            cursor = dbWrapper.rawQuery("SELECT " + DatabaseHelper.BlockColumns._ID
                            + " FROM " + DatabaseHelper.BLOCK_TABLE
                            + " WHERE " + DatabaseHelper.BlockColumns.BLOCK_ID + "= '" + blockid+"'",
                    null);

            if (cursor.moveToFirst()) {
                _ID = cursor.getString(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        dbWrapper.setTransactionSuccessful();
    } finally {
        dbWrapper.endTransaction();
    }

        return _ID;
    }

    public final static int MAX_BATCH_SIZE=500;
    /**
     * Delete conversation and associated messages/parts
     */
    public static boolean deleteBlocks(final DatabaseWrapper dbWrapper,
            final String[] blockIds) {
        ArrayList<StringBuffer> selectors=new ArrayList<StringBuffer>();
        List<String> BlockIdList=Arrays.asList(blockIds);
        Joiner joiner=Joiner.on("','").skipNulls();
        if(blockIds.length>MAX_BATCH_SIZE){
        	int c=0;
        	for(;c<blockIds.length/MAX_BATCH_SIZE;c++){
        		StringBuffer selector = new StringBuffer(DatabaseHelper.BlockColumns.BLOCK_ID + " in ('");
        		selector.append(joiner.join(BlockIdList.subList(c * MAX_BATCH_SIZE, (c + 1)
        				* MAX_BATCH_SIZE))+"')");
        		selectors.add(selector);
        	}
        	StringBuffer selector = new StringBuffer(DatabaseHelper.BlockColumns.BLOCK_ID + " in ('");
    		selector.append(joiner.join(BlockIdList.subList(c * MAX_BATCH_SIZE,blockIds.length))+"')");
    		selectors.add(selector);
        }else{
        	StringBuffer selector = new StringBuffer(DatabaseHelper.BlockColumns.BLOCK_ID+ " in ('");
    		selector.append(joiner.join(BlockIdList)+"')");
    		selectors.add(selector);
        }
        
        dbWrapper.beginTransaction();
        boolean blockDeleted = false;
        try {
            // Delete existing messages
                // Delete conversation row
        	for(int i=0;i<selectors.size();i++){
                String blockId=selectors.get(i).toString();
        		final int count = dbWrapper.delete(DatabaseHelper.BLOCK_TABLE,blockId, null);
                if(blockDeleted==false)
                    blockDeleted = (count > 0);
        	}
            dbWrapper.setTransactionSuccessful();
        } finally {
            dbWrapper.endTransaction();
        }
        return blockDeleted;
    }

    public static boolean updateRowIfExists(final DatabaseWrapper db, final String table,
                                            final String rowKey, final String rowId, final ContentValues values) {
        final StringBuilder sb = new StringBuilder();
        final ArrayList<String> whereValues = new ArrayList<String>(values.size() + 1);
        whereValues.add(rowId);

        for (final String key : values.keySet()) {
            if (sb.length() > 0) {
                sb.append(" OR ");
            }
            final Object value = values.get(key);
            sb.append(key);
            if (value != null) {
                sb.append(" IS NOT ?");
                whereValues.add(value.toString());
            } else {
                sb.append(" IS NOT NULL");
            }
        }

        final String whereClause = rowKey + "=?" + " AND (" + sb.toString() + ")";
        final String [] whereValuesArray = whereValues.toArray(new String[whereValues.size()]);
        db.beginTransaction();
        int count=-1;
        try{
        count = db.update(table, values, whereClause, whereValuesArray);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        if (count > 1) {
            Log.w(TAG, "Updated more than 1 row " + count + "; " + table +
                    " for " + rowKey + " = " + rowId + " (deleted?)");
        }
        return (count >= 0);
    }

    public static boolean updateBlock(final DatabaseWrapper dbWrapper,String blockid,String name,String image,long time) {
        BlockItemData data=getExistingBlockItem(dbWrapper,blockid);
        if(data==null){
            return false;
        }
        Log.e(TAG, "BugleDatabaseOperations : update block by updateBlock");
        final ContentValues values = new ContentValues();
        values.put(DatabaseHelper.BlockColumns.NAME, name);
        values.put(DatabaseHelper.BlockColumns.BLOCK_ID, blockid);
        if(image!=null)
            values.put(DatabaseHelper.BlockColumns.IMAGE, image);
        values.put(DatabaseHelper.BlockColumns.SORT_TIMESTAMP, time);

        return updateRowIfExists(dbWrapper, DatabaseHelper.BLOCK_TABLE,
                DatabaseHelper.BlockColumns.BLOCK_ID, blockid, values);
    }


    public static BlockItemData getExistingBlockItem(final DatabaseWrapper dbWrapper,
                                                         final String blockid) {
        BlockItemData blockitem = null;
        Cursor cursor = null;
        dbWrapper.beginTransaction();
        try{
        try {
            cursor = dbWrapper.query(DatabaseHelper.BLOCK_TABLE,
                    BlockItemData.PROJECTION,
                    DatabaseHelper.BlockColumns.BLOCK_ID + " =?",
                    new String[] { blockid }, null, null, null);
            if (cursor.moveToFirst()) {
                blockitem=new BlockItemData();
                blockitem.bind(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        dbWrapper.setTransactionSuccessful();
    } finally {
        dbWrapper.endTransaction();
    }

        return blockitem;
    }

}
