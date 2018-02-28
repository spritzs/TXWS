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

package cn.txws.board;

import android.content.Context;

import cn.txws.board.database.data.DataModel;
import cn.txws.board.database.data.DataModelImpl;


class FactoryImpl extends Factory {
    private MyApplication mApplication;
    private DataModel mDataModel;
    private Context mApplicationContext;

    // Cached instance for Pre-L_MR1
    private static final Object PHONEUTILS_INSTANCE_LOCK = new Object();
    // Cached subId->instance for L_MR1 and beyond

    private FactoryImpl() {
    }

    public static Factory register() {
        // This only gets called once (from BugleApplication.onCreate), but its not called in tests.

        final FactoryImpl factory = new FactoryImpl();
        Factory.setInstance(factory);
        sRegistered = true;

        // At this point Factory is published. Services can now get initialized and depend on
        // Factory.get().
        factory.mApplication = MyApplication.getInstance();
        factory.mApplicationContext = MyApplication.getInstance();
        factory.mDataModel = new DataModelImpl(factory.mApplicationContext);
        return factory;
    }

    @Override
    public void onRequiredPermissionsAcquired() {
    }

    @Override
    public Context getApplicationContext() {
        return mApplicationContext;
    }

    @Override
    public DataModel getDataModel() {
        return mDataModel;
    }

    @Override
    public void onActivityResume() {
    }


}
