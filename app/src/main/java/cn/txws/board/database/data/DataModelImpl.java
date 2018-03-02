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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import cn.txws.board.database.DatabaseHelper;
import cn.txws.board.database.DatabaseWrapper;
import cn.txws.board.database.action.ActionService;
import cn.txws.board.database.action.BackgroundWorker;

/**
 * Created by zqs on 2018/2/26 .
 * 数据模型实现
 */
public class DataModelImpl extends DataModel {
    private final String TAG="DataModelImpl";
    private final Context mContext;
    private final ActionService mActionService;
    private final BackgroundWorker mDataModelWorker;
    private final DatabaseHelper mDatabaseHelper;
    private boolean isAppOnCreate=false;//add by JXH 2016.12.27

    public DataModelImpl(final Context context) {
        super();
        mContext = context;
        mActionService = new ActionService();
        mDataModelWorker = new BackgroundWorker();
        mDatabaseHelper = DatabaseHelper.getInstance(context);
    }


    @Override
    public BlockItemData createBlockItemData() {
        return new BlockItemData();
    }


    @Override
    public BlockLoaderData createBlockLoaderData(final Context context, final BlockLoaderData.BlockLoaderDataListener listener) {
        return new BlockLoaderData(context,listener);
    }


    @Override
    public ActionService getActionService() {
        // We need to allow access to this on the UI thread since it's used to start actions.
        return mActionService;
    }

    @Override
    public BackgroundWorker getBackgroundWorkerForActionService() {
        return mDataModelWorker;
    }

    @Override
    public DatabaseWrapper getDatabase() {
        // We prevent the main UI thread from accessing the database since we have to allow
        // public access to this class to enable sub-packages to access data.
        return mDatabaseHelper.getDatabase();
    }


    @Override
    void onCreateTables(final SQLiteDatabase db) {
        Log.w(TAG, "Rebuilt databases: reseting related state");
        // Clear other things that implicitly reference the DB
    }

    @Override
    public void onActivityResume() {

    }

    @Override
    public void onApplicationCreated() {
    	isAppOnCreate=true;
    }



}
