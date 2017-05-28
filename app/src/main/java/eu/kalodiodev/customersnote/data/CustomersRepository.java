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
package eu.kalodiodev.customersnote.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;

import eu.kalodiodev.customersnote.data.source.CustomersContract;

/**
 * Customers Repository
 *
 * @author Athanasios Raptodimos
 */
public class CustomersRepository implements ICustomersRepository {

    private static CustomersRepository INSTANCE = null;
    private final ContentResolver mContentResolver;

    // Prevent direct instantiation
    private CustomersRepository(@NonNull ContentResolver contentResolver) {
        this.mContentResolver = contentResolver;
    }

    public static CustomersRepository getInstance(@NonNull ContentResolver contentResolver) {
        if(INSTANCE == null) {
            INSTANCE = new CustomersRepository(contentResolver);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void getCustomers(@NonNull GetCustomersCallBack callback, String searchTerm) {
        callback.onLoadCustomers(searchTerm);
    }

    @Override
    public Uri save(Customer customer) {
        return mContentResolver.insert(CustomersContract.CONTENT_URI, customer.toContentValues());
    }

    @Override
    public int update(Customer customer) {
        return mContentResolver.update(CustomersContract
                .buildCustomerUri(customer.getId()), customer.toContentValues(), null, null);
    }

    @Override
    public int delete(Customer customer) {
        return mContentResolver.delete(CustomersContract
                .buildCustomerUri(customer.getId()), null, null);
    }
}