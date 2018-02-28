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

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import com.google.common.annotations.VisibleForTesting;

import java.util.List;

import cn.txws.board.MyApplication;
import cn.txws.board.database.data.DataModel;
import cn.txws.board.database.data.DataModelException;

/**
 * Background worker service is an initial example of a background work queue handler
 * Used to actually "send" messages which may take some time and should not block ActionService
 * or UI
 */
public class BackgroundWorkerService extends IntentService {
    private static final String TAG = "BackgroundWorkerService";
    private static final boolean VERBOSE = false;

    private static final String WAKELOCK_ID = "bugle_background_worker_wakelock";
    @VisibleForTesting

    private final ActionService mHost;

    public BackgroundWorkerService() {
        super("BackgroundWorker");
        mHost = DataModel.get().getActionService();
    }

    /**
     * Queue a list of requests from action service to this worker
     */
    public static void queueBackgroundWork(final List<Action> actions) {
        for (final Action action : actions) {
            startServiceWithAction(action, 0);
        }
    }

    // ops
    @VisibleForTesting
    protected static final int OP_PROCESS_REQUEST = 400;

    // extras
    @VisibleForTesting
    protected static final String EXTRA_OP_CODE = "op";
    @VisibleForTesting
    protected static final String EXTRA_ACTION = "action";
    @VisibleForTesting
    protected static final String EXTRA_ATTEMPT = "retry_attempt";

    /**
     * Queue action intent to the BackgroundWorkerService after acquiring wake lock
     */
    private static void startServiceWithAction(final Action action,
            final int retryCount) {
        final Intent intent = new Intent();
        intent.putExtra(EXTRA_ACTION, action);
        intent.putExtra(EXTRA_ATTEMPT, retryCount);
        startServiceWithIntent(OP_PROCESS_REQUEST, intent);
    }

    /**
     * Queue intent to the BackgroundWorkerService after acquiring wake lock
     */
    private static void startServiceWithIntent(final int opcode, final Intent intent) {
        final Context context = MyApplication.getInstance();

        intent.setClass(context, BackgroundWorkerService.class);
        intent.putExtra(EXTRA_OP_CODE, opcode);
        if (VERBOSE) {
            Log.v(TAG, "acquiring wakelock for opcode " + opcode);
        }

        if (context.startService(intent) == null) {
            Log.e(TAG,
                    "BackgroundWorkerService.startServiceWithAction: failed to start service for "
                    + opcode);
        }
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        if (intent == null) {
            // Shouldn't happen but sometimes does following another crash.
            Log.w(TAG, "BackgroundWorkerService.onHandleIntent: Called with null intent");
            return;
        }
        final int opcode = intent.getIntExtra(EXTRA_OP_CODE, 0);

        try {
            switch(opcode) {
                case OP_PROCESS_REQUEST: {
                    final Action action = intent.getParcelableExtra(EXTRA_ACTION);
                    final int attempt = intent.getIntExtra(EXTRA_ATTEMPT, -1);
                    doBackgroundWork(action, attempt);
                    break;
                }

                default:
                    throw new RuntimeException("Unrecognized opcode in BackgroundWorkerService");
            }
        } finally {
        }
    }

    /**
     * Local execution of background work for action on ActionService thread
     */
    private void doBackgroundWork(final Action action, final int attempt) {
        action.markBackgroundWorkStarting();
        Bundle response = null;
        try {

            response = action.doBackgroundWork();

            action.markBackgroundCompletionQueued();
            mHost.handleResponseFromBackgroundWorker(action, response);
        } catch (final Exception exception) {
            final boolean retry = false;
            Log.e(TAG, "Error in background worker", exception);
            if (!(exception instanceof DataModelException)) {
                // DataModelException is expected (sort-of) and handled in handleFailureFromWorker
                // below, but other exceptions should crash ENG builds
            	exception.printStackTrace();
//                Assert.fail("Unexpected error in background worker - abort");
            }
            if (retry) {
                action.markBackgroundWorkQueued();
                startServiceWithAction(action, attempt + 1);
            } else {
                action.markBackgroundCompletionQueued();
                mHost.handleFailureFromBackgroundWorker(action, exception);
            }
        }
    }
}
