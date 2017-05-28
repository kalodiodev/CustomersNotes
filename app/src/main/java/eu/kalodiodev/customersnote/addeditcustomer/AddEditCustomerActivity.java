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
package eu.kalodiodev.customersnote.addeditcustomer;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import eu.kalodiodev.customersnote.R;
import eu.kalodiodev.customersnote.data.Customer;
import eu.kalodiodev.customersnote.data.CustomersRepository;
import eu.kalodiodev.customersnote.utils.AppDialog;

/**
 * Add/Edit Customer Activity
 *
 * @author Athanasios Raptodimos
 */
public class AddEditCustomerActivity extends AppCompatActivity implements AppDialog.DialogEvents,
        AddEditCustomerFragment.OnAction {

    private static final String TAG = "AddEditCustomerActivity";

    public static final int DIALOG_ID_DELETE = 1;
    public static final int DIALOG_ID_CANCEL_EDIT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Arguments Bundle
        Bundle arguments = getIntent().getExtras();

        // Setup AddEditCustomer Fragment
        AddEditCustomerFragment addEditFragment = setupAddEditCustomerFragment();

        Customer customer = selectedCustomer(arguments);
        createPresenter(customer, addEditFragment);
    }

    private Customer selectedCustomer(Bundle arguments) {
        if(arguments != null) {
            return  (Customer) arguments.getSerializable(Customer.class.getSimpleName());
        }
        return null;
    }

    private AddEditCustomerFragment setupAddEditCustomerFragment() {
        AddEditCustomerFragment addEditFragment = (AddEditCustomerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment);

        if(addEditFragment == null) {
            addEditFragment = new AddEditCustomerFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment, addEditFragment);
            fragmentTransaction.commit();
        }

        return addEditFragment;
    }

    private void createPresenter(Customer customer, AddEditCustomerFragment fragment) {
        AddEditCustomerPresenter mAddEditCustomerPresenter = new AddEditCustomerPresenter(customer,
                fragment, CustomersRepository.getInstance(getContentResolver()));

        fragment.setPresenter(mAddEditCustomerPresenter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
          switch (item.getItemId()) {
            case android.R.id.home:
                AddEditCustomerFragment fragment = getFragment();
                if(fragment != null) {
                    fragment.closeView();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private AddEditCustomerFragment getFragment() {
        return (AddEditCustomerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);
    }

    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onPositiveDialogResult: with dialogID:" + dialogId);

        switch (dialogId) {
            case DIALOG_ID_DELETE:
                Log.d(TAG, "onPositiveDialogResult: Delete");
                AddEditCustomerFragment fragment = getFragment();
                fragment.confirmedCustomerDeletion();
            case DIALOG_ID_CANCEL_EDIT:
                // Nothing to do
                break;
        }
    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onNegativeDialogResult: with dialogID:" + dialogId);

        switch (dialogId) {
            case DIALOG_ID_DELETE:
                // Nothing to do
                break;
            case DIALOG_ID_CANCEL_EDIT:
                finish();
        }
    }

    @Override
    public void onDialogCancelled(int dialogId) {
        // Nothing to be done
    }

    @Override
    public void onClose() {
        finish();
    }

    @Override
    public void onBackPressed() {
        AddEditCustomerFragment fragment = getFragment();

        if(fragment == null) {
            super.onBackPressed();
        } else {
            fragment.closeView();
        }
    }
}