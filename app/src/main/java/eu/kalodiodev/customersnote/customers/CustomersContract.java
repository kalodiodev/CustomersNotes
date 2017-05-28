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
package eu.kalodiodev.customersnote.customers;

import android.database.Cursor;

import eu.kalodiodev.customersnote.data.Customer;

/**
 * Customers MVP Contract
 *
 * @author Athanasios Raptodimos
 */
public interface CustomersContract {

    /**
     * View Contract
     */
    interface View {

        /**
         * Show Customers list
         *
         * @param customers cursor of customers
         */
        void showCustomers(Cursor customers);

        /**
         * Start Edit Customer
         *
         * @param customer customer to be edited
         */
        void showEditCustomer(Customer customer);

        /**
         * Start Add Customer
         */
        void showAddCustomer();
    }

    /**
     * Presenter Contract
     */
    interface Presenter {

        /**
         * Load customers list
         *
         * @param searchTerm search term to filter customers
         */
        void loadCustomers(String searchTerm);

        /**
         * Set Query Search term to used for filtering customers list
         *
         * @param searchTerm search term
         */
        void setQueryText(String searchTerm);

        /**
         * Open Customer edit
         *
         * @param customer customer to open
         */
        void editCustomer(Customer customer);

        /**
         * Open Add Customer
         */
        void addCustomer();
    }
}
