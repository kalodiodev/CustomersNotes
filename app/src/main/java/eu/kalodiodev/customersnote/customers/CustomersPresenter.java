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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import eu.kalodiodev.customersnote.data.Customer;
import eu.kalodiodev.customersnote.data.ICustomersRepository;
import eu.kalodiodev.customersnote.data.source.LoaderProvider;

/**
 * Customers Presenter
 *
 * @author Athanasios Raptodimos
 */
public class CustomersPresenter implements CustomersContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor>, ICustomersRepository.GetCustomersCallBack {

    /**
     * Customers Loader ID
     */
    private final static int CUSTOMERS_LOADER = 1;

    @NonNull
    private final LoaderProvider mLoaderProvider;

    @NonNull
    private final LoaderManager mLoaderManager;

    @NonNull
    private final CustomersContract.View view;

    @NonNull
    private final ICustomersRepository mCustomersRepository;

    private String searchTerm = "";

    /**
     * Customers Presenter
     *
     * @param loaderProvider content provider loader
     * @param loaderManager loader manager
     * @param view activity or fragment that implements {@link CustomersContract.View}
     */
    public CustomersPresenter(@NonNull LoaderProvider loaderProvider,
                              @NonNull LoaderManager loaderManager,
                              @NonNull CustomersContract.View view,
                              @NonNull ICustomersRepository customersRepository) {

        this.mLoaderProvider = loaderProvider;
        this.mLoaderManager = loaderManager;
        this.mCustomersRepository = customersRepository;
        this.view = view;
    }

    @Override
    public void loadCustomers(String searchTerm) {
        mCustomersRepository.getCustomers(this, searchTerm);
    }

    @Override
    public void onLoadCustomers(String searchTerm) {
        // Set search term
        this.searchTerm = searchTerm;

        if(mLoaderManager.getLoader(CUSTOMERS_LOADER) == null) {
            // Loader does not exist, start new loader
            mLoaderManager.initLoader(CUSTOMERS_LOADER, new Bundle(), this);
        } else {
            // Loader exists, restart
            mLoaderManager.restartLoader(CUSTOMERS_LOADER, new Bundle(), this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mLoaderProvider.createCustomerLoader(LoaderProvider.LOADER_ID, searchTerm);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        view.showCustomers(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        view.showCustomers(null);
    }

    @Override
    public void setQueryText(String searchTerm) {
        this.searchTerm = searchTerm;

        loadCustomers(searchTerm);
    }

    @Override
    public void editCustomer(Customer customer) {
        view.showEditCustomer(customer);
    }

    @Override
    public void addCustomer() {
        view.showAddCustomer();
    }
}
