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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import eu.kalodiodev.customersnote.Constants;

/**
 * Database Helper
 *
 * @author Raptodimos Athanasios
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = "DbHelper";

    // Implement DbHelper as a Singleton
    private static DbHelper instance = null;

    private DbHelper(Context context) {
        super(context, Constants.Database.DATABASE_NAME, null, Constants.Database.DATABASE_VERSION);
        Log.d(TAG, "DbHelper: constructor");
    }

    /**
     * Get an instance of the app's singleton database helper object
     *
     * @param context the content providers context
     * @return a SQLite database helper object
     */
    public static DbHelper getInstance(Context context) {
        if(instance == null) {
            Log.d(TAG, "getInstance: creating new instance");
            instance = new DbHelper(context);
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate: starts");

        // CREATE TABLE Customers (_id INTEGER PRIMARY KEY NOT NULL, FirstName TEXT NOT NULL UNIQUE,
        // LastName TEXT, Profession TEXT, CompanyName TEXT, phone TEXT, notes TEXT);
        String sql = "CREATE TABLE " + CustomersContract.TABLE_NAME + "(" +
                CustomersContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, " +
                CustomersContract.Columns.CUSTOMERS_FIRST_NAME + " TEXT NOT NULL, " +
                CustomersContract.Columns.CUSTOMERS_LAST_NAME + " TEXT, " +
                CustomersContract.Columns.CUSTOMERS_PROFESSION + " TEXT, " +
                CustomersContract.Columns.CUSTOMERS_COMPANY_NAME + " TEXT, " +
                CustomersContract.Columns.CUSTOMERS_PHONE_NUMBER + " TEXT, " +
                CustomersContract.Columns.CUSTOMERS_NOTES + " TEXT" + ");";

        Log.d(TAG, "Create table query: " + sql);

        sqLiteDatabase.execSQL(sql);

        Log.d(TAG, "onCreate: ends");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: starts");
        switch (oldVersion) {
            case 1:
                // upgrade logic from version 1
                break;
            default:
                throw new IllegalStateException("onUpgrade() with unknown new Version: " + newVersion);
        }
        Log.d(TAG, "onUpgrade: ends");
    }
}
