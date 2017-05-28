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

import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Customers Repository Contract
 *
 * @author Athanasios Raptodimos
 */
public interface ICustomersRepository {

    /**
     * Get Customers Callback
     *
     * Presenters using this repository to get all customers must implement it's callback
     * in order to init Loader Manager
     */
    interface GetCustomersCallBack {

        void onLoadCustomers(String searchTerm);

    }
    /**
     * Get Customers
     *
     * @param callback Getting customers callback
     * @param searchTerm search term to filter customers
     */
    void getCustomers(@NonNull GetCustomersCallBack callback, String searchTerm);

    /**
     * Save Customer
     *
     * @param customer customer to save
     */
    Uri save(Customer customer);

    /**
     * Update Customer
     *
     * @param customer customer to be updated
     * @return number of rows affected
     */
    int update(Customer customer);

    /**
     * Delete Customer
     *
     * @param customer customer to be deleted
     * @return number of rows affected
     */
    int delete(Customer customer);
}