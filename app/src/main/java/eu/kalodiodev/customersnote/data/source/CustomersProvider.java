/*
 * Copyright (c) 2017 Athanasios Raptodimos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.kalodiodev.customersnote.data.source;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Customers Content Provider
 *
 * @author Raptodimos Athanasios
 */
public class CustomersProvider extends ContentProvider {

    private static final String TAG = "CustomersProvider";

    private DbHelper dbHelper;

    public static final UriMatcher sUriMatcher = buildUriMather();

    public static final String CONTENT_AUTHORITY = "eu.kalodiodev.customersnote.provider";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final int CUSTOMERS = 100;
    private static final int CUSTOMERS_ID = 101;


    private static UriMatcher buildUriMather() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        // eg. content://eu.kalodiodev.customersnote.provider/Customers
        matcher.addURI(CONTENT_AUTHORITY, CustomersContract.TABLE_NAME, CUSTOMERS);
        // eg. content://eu.kalodiodev.customersnote.provider/Customers/8
        matcher.addURI(CONTENT_AUTHORITY, CustomersContract.TABLE_NAME + "/#", CUSTOMERS_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = DbHelper.getInstance(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Log.d(TAG, "query: called with URI " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "query: match is " + match);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (match) {
            case CUSTOMERS:
                queryBuilder.setTables(CustomersContract.TABLE_NAME);
                break;
            case CUSTOMERS_ID:
                queryBuilder.setTables(CustomersContract.TABLE_NAME);
                long customerId = CustomersContract.getCustomerId(uri);
                queryBuilder.appendWhere(CustomersContract.Columns._ID + " = " + customerId);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        // Set Notification Uri to Cursor, needed to notify changed
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CUSTOMERS:
                return CustomersContract.CONTENT_TYPE;
            case CUSTOMERS_ID:
                return CustomersContract.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("unknown Uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Log.d(TAG, "Insert record, called with uri: " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "match is " + match);

        final SQLiteDatabase db;
        Uri returnUri;
        long recordId;

        switch (match) {
            case CUSTOMERS:
                db = dbHelper.getWritableDatabase();
                recordId = db.insert(CustomersContract.TABLE_NAME, null, contentValues);
                if(recordId >= 0) {
                    returnUri = CustomersContract.buildCustomerUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown uri:" + uri);
        }

        // Notify insertion
        if(recordId >= 0) {
            Log.d(TAG, "insert: setting notifyChanged with " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Log.d(TAG, "insert: nothing inserted");
        }

        Log.d(TAG, "Exiting insert, returning " + returnUri);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        Log.d(TAG, "delete called with uri:" + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "match id " + match);

        final SQLiteDatabase db;
        int count;

        String selectionCriteria;

        switch (match) {
            case CUSTOMERS:
                db = dbHelper.getWritableDatabase();
                count = db.delete(CustomersContract.TABLE_NAME, selection, selectionArgs);
                break;
            case CUSTOMERS_ID:
                db = dbHelper.getWritableDatabase();
                long customerId = CustomersContract.getCustomerId(uri);
                selectionCriteria = CustomersContract.Columns._ID + " = " + customerId;
                if((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(CustomersContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri:" + uri);
        }

        // notify deletion
        if(count > 0) {
            // something was deleted
            Log.d(TAG, "delete: Setting notifyChange with " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Log.d(TAG, "delete: nothing deleted");
        }

        Log.d(TAG, "Exiting delete, returning " + count);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {

        Log.d(TAG, "update with uri:" + uri);
        int match = sUriMatcher.match(uri);
        Log.d(TAG, "match is " + match);

        int count;
        SQLiteDatabase db;
        String selectionCriteria;

        switch (match) {
            case CUSTOMERS:
                db = dbHelper.getWritableDatabase();
                count = db.update(CustomersContract.TABLE_NAME, contentValues,
                        selection, selectionArgs);
                break;
            case CUSTOMERS_ID:
                db = dbHelper.getWritableDatabase();
                long customerId = CustomersContract.getCustomerId(uri);
                selectionCriteria = CustomersContract.Columns._ID + " = " + customerId;
                if((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.update(CustomersContract.TABLE_NAME, contentValues,
                        selectionCriteria, selectionArgs);

                break;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }

        // notify of update
        if(count > 0) {
            // something was updated
            Log.d(TAG, "update: Setting notifyChange with " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Log.d(TAG, "update: nothing updated");
        }

        Log.d(TAG, "Exiting update, returning " + count);
        return count;
    }
}
