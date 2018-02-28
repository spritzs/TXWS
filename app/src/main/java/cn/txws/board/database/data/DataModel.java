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

import cn.txws.board.Factory;
import cn.txws.board.database.DatabaseWrapper;
import cn.txws.board.database.action.Action;
import cn.txws.board.database.action.ActionService;
import cn.txws.board.database.action.BackgroundWorker;


public abstract class DataModel {

    public static DataModel get() {
        return Factory.get().getDataModel();
    }

    public static final void startActionService(final Action action) {
        get().getActionService().startAction(action);
    }

    public static final void scheduleAction(final Action action,
            final int code, final long delayMs) {
        get().getActionService().scheduleAction(action, code, delayMs);
    }


    public abstract BlockItemData createBlockItemData();
    public abstract BlockLoaderData createBlockLoaderData(final Context context, final BlockLoaderData.BlockLoaderDataListener listener);

    public abstract ActionService getActionService();

    public abstract BackgroundWorker getBackgroundWorkerForActionService();

    public abstract DatabaseWrapper getDatabase();

    // Allow DataModel to coordinate with activity lifetime events.
    public abstract void onActivityResume();

    abstract void onCreateTables(final SQLiteDatabase db);


    public abstract void onApplicationCreated();

}
