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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import eu.kalodiodev.customersnote.data.Customer;
import eu.kalodiodev.customersnote.data.ICustomersRepository;

/**
 * Add/Edit Customer Presenter
 *
 * @author Athanasios Raptodimos
 */
public class AddEditCustomerPresenter implements AddEditCustomerContract.Presenter {

    private ICustomersRepository mCustomersRepository;

    private AddEditCustomerContract.View view;

    private Customer mCustomer;

    public AddEditCustomerPresenter(@Nullable Customer customer,
                                    @NonNull AddEditCustomerContract.View view,
                                    @NonNull ICustomersRepository customersRepository) {

        this.mCustomersRepository = customersRepository;
        this.view = view;
        this.mCustomer = customer;
    }

    @Override
    public void bind(@NonNull AddEditCustomerContract.View view) {
        this.view = view;
    }

    @Override
    public void unbind() {
        this.view = null;
    }

    @Override
    public void initializeFields() {
        if(! isNewCustomer()) {
            view.setFirstName(mCustomer.getFirstName());
            view.setLastName(mCustomer.getLastName());
            view.setProfession(mCustomer.getProfession());
            view.setCompanyName(mCustomer.getCompanyName());
            view.setPhoneNumber(mCustomer.getPhoneNumber());
            view.setNotes(mCustomer.getNotes());
        }
    }

    @Override
    public void save() {
        Customer newCustomer = setupCustomerFromFields();

        if(newCustomer.getFirstName().isEmpty()) {
            // Customer cannot be saved, empty name
            view.showEmptyFirstName();
        } else {
            saveCustomer(newCustomer);
        }
    }

    @Override
    public void delete(boolean showConfirmation) {
        if(! isNewCustomer()) {
            if(showConfirmation) {
                // Requires confirmation
                view.showDeleteConfirmation();
            } else {
                // Confirmation not required or already confirmed
                mCustomersRepository.delete(this.mCustomer);
                // Customer deleted, show message
                view.showDeleteCompletedMessage();
                // Close view
                view.close();
            }
        }
    }

    @Override
    public void closeView() {
        if(canClose()) {
            view.close();
        } else {
            view.showCancelEditConfirmation();
        }
    }

    private boolean canClose() {
        if(isNewCustomer()) {
            // New customer, to close fields must be empty
            return areFieldsEmpty();
        } else {
            // Customer editing, to close fields must be unchanged
            return !changesMade();
        }
    }

    private boolean changesMade() {
        return ! view.getFirstName().equals(mCustomer.getFirstName()) ||
                ! view.getLastName().equals(mCustomer.getLastName()) ||
                ! view.getProfession().equals(mCustomer.getProfession()) ||
                ! view.getCompanyName().equals(mCustomer.getCompanyName()) ||
                ! view.getPhoneNumber().equals(mCustomer.getPhoneNumber()) ||
                ! view.getNotes().equals(mCustomer.getNotes());
    }

    private boolean areFieldsEmpty() {
        return view.getFirstName().isEmpty() &&
                view.getLastName().isEmpty() &&
                view.getProfession().isEmpty() &&
                view.getCompanyName().isEmpty() &&
                view.getPhoneNumber().isEmpty() &&
                view.getNotes().isEmpty();
    }

    private Customer setupCustomerFromFields() {
        Customer newCustomer = new Customer(
                view.getFirstName(),
                view.getLastName(),
                view.getProfession(),
                view.getCompanyName(),
                view.getPhoneNumber(),
                view.getNotes());

        if(!isNewCustomer()) {
            // Not a new customer, setting customer's id for update
            newCustomer.setId(this.mCustomer.getId());
        }

        return newCustomer;
    }

    private void saveCustomer(Customer newCustomer) {
        // customer can be saved
        if(isNewCustomer()) {
            // Add new customer
            mCustomersRepository.save(newCustomer);
        } else {
            // Update customer
            mCustomersRepository.update(newCustomer);
        }
        view.showSaveCompletedMessage();
        view.close();
    }

    private boolean isNewCustomer() {
        return this.mCustomer == null;
    }
}