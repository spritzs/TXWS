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

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.google.common.annotations.VisibleForTesting;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import cn.txws.board.MyApplication;

/**
 * A centralized provider for Uris exposed by Bugle.
 *  */
public class BlockContentProvider extends ContentProvider {
    private static final String TAG = "BUGLE_TAG";
    // bug 478514: Add for MmsFolderView Feature -- Begin
    @VisibleForTesting
    public static final String AUTHORITY =
            "cn.txws.board.database.BlockContentProvider";
    private static final String CONTENT_AUTHORITY = "content://" + AUTHORITY + '/';

    public static final Uri BLOCKS_URI = Uri.parse(CONTENT_AUTHORITY + DatabaseHelper.BLOCK_TABLE);

    private static final String BLOCKS_IMAGE_QUERY = "blocks_image";
    private static final String BLOCKS_QUERY = "blocks";

    public static final Uri BLOCKS_IMAGE_URI = Uri.parse(CONTENT_AUTHORITY +
            BLOCKS_IMAGE_QUERY);


    public static void notifyEverythingChanged() {
        final Uri uri = Uri.parse(CONTENT_AUTHORITY);
        final ContentResolver cr = MyApplication.getInstance().getContentResolver();
        cr.notifyChange(uri, null);
    }



    public static void notifyAllBlocksImageChanged() {
        final ContentResolver cr = MyApplication.getInstance().getContentResolver();
        cr.notifyChange(BLOCKS_IMAGE_URI, null);
    }


    public static void notifyAllBlocksChanged() {
        final ContentResolver cr = MyApplication.getInstance().getContentResolver();
        cr.notifyChange(BLOCKS_URI, null);
    }



    // Default value for unknown dimension of image
    public static final int UNSPECIFIED_SIZE = -1;

    // Internal
    private static final int BLOCKS_QUERY_CODE = 10;

    private static final int BLOCKS_IMAGE_QUERY_CODE = 20;

    // TODO: Move to a better structured URI namespace.
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BLOCKS_QUERY, BLOCKS_QUERY_CODE);
        sURIMatcher.addURI(AUTHORITY, BLOCKS_IMAGE_QUERY + "/*", BLOCKS_IMAGE_QUERY_CODE);
    }


    private DatabaseHelper mDatabaseHelper;
    private DatabaseWrapper mDatabaseWrapper;

    public BlockContentProvider() {
        super();
    }


    private DatabaseWrapper getDatabaseWrapper() {
        if (mDatabaseWrapper == null) {
            mDatabaseWrapper = mDatabaseHelper.getDatabase();
        }
        return mDatabaseWrapper;
    }

    @Override
    public Cursor query(final Uri uri, final String[] projection, String selection,
            final String[] selectionArgs, String sortOrder) {


        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String[] queryArgs = selectionArgs;
        final int match = sURIMatcher.match(uri);
        String groupBy = null;
        String limit = null;
        switch (match) {
            case BLOCKS_QUERY_CODE:
                queryBuilder.setTables(BLOCKS_QUERY);
                queryBuilder.appendWhere(DatabaseHelper.BlockColumns.SORT_TIMESTAMP + " > 0 ");
                break;

            case BLOCKS_IMAGE_QUERY_CODE:
//                queryBuilder.setTables(BLOCKS_QUERY);
//                if (uri.getPathSegments().size() == 2) {
//                    // Draft only.
//                    queryBuilder.appendWhere(
//                            DatabaseHelper.BlockColumns.BLOCK_ID+ " =? ");
//                    // Get the conversation id from the uri
//                    queryArgs = prependArgs(queryArgs, uri.getPathSegments().get(1));
//                } else {
//                    throw new IllegalArgumentException("Malformed URI " + uri);
//                }
                break;
            default: {
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
        }

        final Cursor cursor = getDatabaseWrapper().query(queryBuilder, projection, selection,
                queryArgs, groupBy, null, sortOrder, limit);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Override
    public String getType(final Uri uri) {
        final StringBuilder sb = new
                StringBuilder("cn.txws.board/cn.txws.board.database.");

        switch (sURIMatcher.match(uri)) {
            case BLOCKS_QUERY_CODE: {
                sb.append(BLOCKS_QUERY);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        }
        return sb.toString();
    }

    protected DatabaseHelper getDatabase() {
        return DatabaseHelper.getInstance(getContext());
    }

    @Override
    public ParcelFileDescriptor openFile(final Uri uri, final String fileMode)
            throws FileNotFoundException {
        throw new IllegalArgumentException("openFile not supported: " + uri);
    }

    @Override
    public Uri insert(final Uri uri, final ContentValues values) {
        throw new IllegalStateException("Insert not supported " + uri);
    }

    @Override
    public int delete(final Uri uri, final String selection, final String[] selectionArgs) {
        throw new IllegalArgumentException("Delete not supported: " + uri);
    }

    @Override
    public int update(final Uri uri, final ContentValues values, final String selection,
            final String[] selectionArgs) {
        throw new IllegalArgumentException("Update not supported: " + uri);
    }

    /**
     * Prepends new arguments to the existing argument list.
     *
     * @param oldArgList The current list of arguments. May be {@code null}
     * @param args The new arguments to prepend
     * @return A new argument list with the given arguments prepended
     */
    private String[] prependArgs(final String[] oldArgList, final String... args) {
        if (args == null || args.length == 0) {
            return oldArgList;
        }
        final int oldArgCount = (oldArgList == null ? 0 : oldArgList.length);
        final int newArgCount = args.length;

        final String[] newArgs = new String[oldArgCount + newArgCount];
        System.arraycopy(args, 0, newArgs, 0, newArgCount);
        if (oldArgCount > 0) {
            System.arraycopy(oldArgList, 0, newArgs, newArgCount, oldArgCount);
        }
        return newArgs;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void dump(final FileDescriptor fd, final PrintWriter writer, final String[] args) {
        // First dump out the default SMS app package name

    }

    @Override
    public boolean onCreate() {
        // This is going to wind up calling into createDatabase() below.
        mDatabaseHelper = (DatabaseHelper) getDatabase();
        // We cannot initialize mDatabaseWrapper yet as the Factory may not be initialized
        return true;
    }

}
