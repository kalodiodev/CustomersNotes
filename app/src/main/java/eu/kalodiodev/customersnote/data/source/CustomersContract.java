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

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Customers Contract
 *
 * <p>Contains constant definitions for the Uris and column names</p>
 *
 * @author Raptodimos Athanasios
 */
public class CustomersContract {

    public static final String TABLE_NAME = "Customers";

    // Customer fields
    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String CUSTOMERS_FIRST_NAME = "FirstName";
        public static final String CUSTOMERS_LAST_NAME = "LastName";
        public static final String CUSTOMERS_PROFESSION = "Profession";
        public static final String CUSTOMERS_COMPANY_NAME = "CompanyName";
        public static final String CUSTOMERS_PHONE_NUMBER = "PhoneNumber";
        public static final String CUSTOMERS_NOTES = "Notes";

        private Columns() {
            // private constructor to prevent instantiation
        }
    }

    /**
     * The URI to access the Customers table
     */
    public static final Uri CONTENT_URI = Uri.withAppendedPath(CustomersProvider
            .CONTENT_AUTHORITY_URI, TABLE_NAME);

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." +
            CustomersProvider.CONTENT_AUTHORITY + "." + TABLE_NAME;

    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." +
            CustomersProvider.CONTENT_AUTHORITY + "." + TABLE_NAME;

    /**
     * Build Customer Uri
     *
     * @param customerId customer's id
     * @return uri
     */
    public static Uri buildCustomerUri(long customerId) {
        return ContentUris.withAppendedId(CONTENT_URI, customerId);
    }

    /**
     * Get customer id in uri
     *
     * @param uri uri contains customer id
     * @return customer id
     */
    public static long getCustomerId(Uri uri) {
        return ContentUris.parseId(uri);
    }
}