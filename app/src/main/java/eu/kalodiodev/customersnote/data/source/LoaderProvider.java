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
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import java.security.InvalidParameterException;
import java.util.ArrayList;


/**
 * Loader Content Provider
 *
 * @author Athanasios Raptodimos
 */
public class LoaderProvider {

    public static final int LOADER_ID = 1;

    @NonNull
    private final Context mContext;

    /**
     * Content Provider Loader Constructor
     *
     * @param context activity context
     */
    public LoaderProvider(@NonNull Context context) {
        this.mContext = context;
    }

    public Loader<Cursor> createCustomerLoader(int id, String searchQuery) {
        String selection = "";
        String[] selectionArgs = {};

        if((searchQuery != null) && (!searchQuery.isEmpty())) {

            String[] terms = searchQuery.split(" ");
            ArrayList<String> args = new ArrayList<>();

            for (int i = 0; i < terms.length; i++) {
                selection += "(" + CustomersContract.Columns.CUSTOMERS_FIRST_NAME + " LIKE ? OR " +
                        CustomersContract.Columns.CUSTOMERS_LAST_NAME + " LIKE ? OR " +
                        CustomersContract.Columns.CUSTOMERS_PROFESSION + " LIKE ? OR " +
                        CustomersContract.Columns.CUSTOMERS_COMPANY_NAME + " LIKE ? OR " +
                        CustomersContract.Columns.CUSTOMERS_PHONE_NUMBER + " LIKE ? ) ";

                if(i < terms.length - 1) {
                    selection += " AND ";
                }

                for(int j=0; j<=4; j++){
                    String term = terms[i];
                    args.add(term + "%");
                }
            }

            selectionArgs = new String[args.size()];
            selectionArgs = args.toArray(selectionArgs);
        }

        String[] projection = {
                CustomersContract.Columns._ID,
                CustomersContract.Columns.CUSTOMERS_FIRST_NAME,
                CustomersContract.Columns.CUSTOMERS_LAST_NAME,
                CustomersContract.Columns.CUSTOMERS_PROFESSION,
                CustomersContract.Columns.CUSTOMERS_COMPANY_NAME,
                CustomersContract.Columns.CUSTOMERS_PHONE_NUMBER,
                CustomersContract.Columns.CUSTOMERS_NOTES
        };

        String sortOrder = CustomersContract.Columns.CUSTOMERS_FIRST_NAME + " COLLATE NOCASE";

        switch (id) {
            case LOADER_ID:
                return new CursorLoader(mContext,
                        CustomersContract.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder);
            default:
                throw new InvalidParameterException("onCreateLoader called with invalid loader id:" + id);
        }
    }
}