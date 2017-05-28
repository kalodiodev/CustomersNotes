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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import eu.kalodiodev.customersnote.R;
import eu.kalodiodev.customersnote.data.Customer;
import eu.kalodiodev.customersnote.customers.CustomersContract.Presenter;

/**
 * Customers Fragment containing customers list recycler view
 *
 * @author Raptodimos Athanasios
 */
public class CustomersFragment extends Fragment implements SearchView.OnQueryTextListener,
        CustomersContract.View {

    /**
     * On Add or Edit Customer Listener
     * All Activities containing this Fragment must implement this interface
     */
    public interface OnAddEditCustomer {

        /**
         * On Edit customer
         *
         * @param customer customer to be edited
         */
        void onEditCustomer(Customer customer);

        /**
         * On Add new customer
         */
        void onAddCustomer();
    }

    private static final String TAG = "CustomersFragment";

    private CursorRecyclerViewAdapter mAdapter;
    private static String searchQuery = "";

    private Presenter customersPresenter;

    // Customer Click Listener
    private CursorRecyclerViewAdapter.OnCustomerClickListener customerClickListener =
            new CursorRecyclerViewAdapter.OnCustomerClickListener() {

        @Override
        public void onEditClick(Customer customer) {
            customersPresenter.editCustomer(customer);
        }
    };

    public CustomersFragment() {
        // Requires empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.customersPresenter.loadCustomers(searchQuery);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        View view = inflater.inflate(R.layout.fragment_customers, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.customers_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new CursorRecyclerViewAdapter(null, customerClickListener);
        recyclerView.setAdapter(mAdapter);

        Log.d(TAG, "onCreateView: returning");
        setHasOptionsMenu(true);

        FloatingActionButton addNewFab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        addNewFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customersPresenter.addCustomer();
            }
        });

        return view;
    }

    /**
     * Set Customer Presenter to Fragment
     *
     * @param customersPresenter customers presenter
     */
    public void setPresenter(@NonNull Presenter customersPresenter) {
        this.customersPresenter = customersPresenter;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_fragment, menu);

        // Get the SearchView and set the searchable configuration
        MenuItem search_item = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search_item);
        searchView.setQueryHint(getString(R.string.menu_search));
        if(!searchQuery.isEmpty()) {
            search_item.expandActionView();
            searchView.setQuery(searchQuery, false);
            searchView.clearFocus();
        }
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d(TAG, "onQueryTextSubmit: " + newText);
        customersPresenter.setQueryText(newText);
        return false;
    }

    /**
     * Force Refresh of list
     */
    public void refresh() {
        customersPresenter.loadCustomers(searchQuery);
    }

    @Override
    public void showCustomers(Cursor customers) {
        mAdapter.swapCursor(customers);
    }

    @Override
    public void showEditCustomer(Customer customer) {
        ((OnAddEditCustomer) getActivity()).onEditCustomer(customer);
    }

    @Override
    public void showAddCustomer() {
        ((OnAddEditCustomer) getActivity()).onAddCustomer();
    }
}
