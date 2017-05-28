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

import java.io.Serializable;


/**
 * Add/Edit Customer MVP Contract
 *
 * @author Athanasios Raptodimos
 */
interface AddEditCustomerContract {

    /**
     * View Contract
     */
    interface View {

        /**
         * Set Presenter to view
         */
        void setPresenter(Presenter presenter);

        /**
         * Set First Name Field
         *
         * @param firstName customer's first name
         */
        void setFirstName(String firstName);

        /**
         * Get First Name Field text
         *
         * @return customer first name
         */
        String getFirstName();

        /**
         * Set Last Name field
         *
         * @param lastName customer's last name
         */
        void setLastName(String lastName);

        /**
         * Get Last Name field text
         *
         * @return customer's last name
         */
        String getLastName();

        /**
         * Set Profession field
         *
         * @param profession customer's profession
         */
        void setProfession(String profession);

        /**
         * Get Profession field text
         *
         * @return customer's profession
         */
        String getProfession();

        /**
         * Set Company Name field
         *
         * @param companyName customer's company name
         */
        void setCompanyName(String companyName);

        /**
         * Get Company Name field text
         *
         * @return customer's company name
         */
        String getCompanyName();

        /**
         * Set Phone Number Field
         *
         * @param phoneNumber customer's phone number
         */
        void setPhoneNumber(String phoneNumber);

        /**
         * Get Phone Number field text
         *
         * @return customer's phone number text
         */
        String getPhoneNumber();

        /**
         * Set Notes
         *
         * @param notes set customer's notes
         */
        void setNotes(String notes);

        /**
         * Get Notes field text
         *
         * @return customer's notes text
         */
        String getNotes();

        /**
         * Show Empty Name Error
         */
        void showEmptyFirstName();

        /**
         * Show Save completed message
         */
        void showSaveCompletedMessage();

        /**
         * Close view
         */
        void close();

        /**
         * Show Delete Confirmation
         */
        void showDeleteConfirmation();

        /**
         * Show Cancel Edit Confirmation
         */
        void showCancelEditConfirmation();

        /**
         * Show Delete completed message
         */
        void showDeleteCompletedMessage();
    }

    /**
     * Presenter Contract
     */
    interface Presenter extends Serializable {

        /**
         * Bind View with the presenter
         *
         * @param view view to bind with
         */
        void bind(AddEditCustomerContract.View view);

        /**
         * Dereference the view in the presenter
         */
        void unbind();

        /**
         * Load customer to be edited
         */
        void initializeFields();

        /**
         * Save customer
         */
        void save();

        /**
         * Delete customer
         *
         * @param showConfirmation true to request confirmation,
         *                         false to delete without confirmation
         */
        void delete(boolean showConfirmation);

        /**
         * Close View
         */
        void closeView();
    }
}
