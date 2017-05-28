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

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import eu.kalodiodev.customersnote.R;
import eu.kalodiodev.customersnote.utils.DialogHelper;

/**
 * Add Edit Activity Fragment
 *
 * <p>Add or edit customer details</p>
 *
 * @author Raptodimos Athanasios
 */
public class AddEditCustomerFragment extends Fragment implements AddEditCustomerContract.View {
    private static final String TAG = "AddEditCustomerFragment";

    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mProfessionEditText;
    private EditText mCompanyNameEditText;
    private EditText mPhoneNumberEditText;
    private EditText mNotesEditText;

    private OnAction mActionListener;

    private AddEditCustomerContract.Presenter mPresenter;


    public AddEditCustomerFragment() {
    }

    /**
     * On Action Clicked Listener Interface
     */
    public interface OnAction {

        /**
         * Close Listener Callback
         */
        void onClose();
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach: starts");
        super.onAttach(context);

        // Activities containing this fragment must implement it's callbacks
        Activity activity = getActivity();
        if(!(activity instanceof OnAction)) {
            throw new ClassCastException(activity.getClass().getSimpleName() +
                    " must implement AddEditCustomerFragment.OnAction interface");
        }
        mActionListener = (OnAction) activity;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: starts");
        super.onDetach();
        mActionListener = null;

        mPresenter = null;
    }

    @Override
    public void setPresenter(AddEditCustomerContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);

        setupViews(view);

        if(savedInstanceState != null) {
            mPresenter = (AddEditCustomerContract.Presenter) savedInstanceState.
                    getSerializable(AddEditCustomerPresenter.class.getSimpleName());
        }

        if(mPresenter != null) {
            mPresenter.bind(this);
            mPresenter.initializeFields();
        }

        setHasOptionsMenu(true);

        return view;
    }

    private void setupViews(View view) {
        mFirstNameEditText = (EditText) view.findViewById(R.id.addedit_first_name);
        mLastNameEditText = (EditText) view.findViewById(R.id.addedit_last_name);
        mProfessionEditText = (EditText) view.findViewById(R.id.addedit_profession);
        mCompanyNameEditText = (EditText) view.findViewById(R.id.addedit_company_name);
        mPhoneNumberEditText = (EditText) view.findViewById(R.id.addedit_phone_number);
        mNotesEditText = (EditText) view.findViewById(R.id.addedit_notes);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_edit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addedit_mnu_save:
                mPresenter.save();
                break;
            case R.id.addedit_mnu_delete:
                // Delete customer, requires confirmation
                mPresenter.delete(true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Delete Customer
     */
    public void confirmedCustomerDeletion() {
        // Delete customer, already confirmed
        mPresenter.delete(false);
    }

    @Override
    public void setFirstName(String firstName) {
        mFirstNameEditText.setText(firstName);
    }

    @Override
    public String getFirstName() {
        return mFirstNameEditText.getText().toString();
    }

    @Override
    public void setLastName(String lastName) {
        mLastNameEditText.setText(lastName);
    }

    @Override
    public String getLastName() {
        return mLastNameEditText.getText().toString();
    }

    @Override
    public void setProfession(String profession) {
        mProfessionEditText.setText(profession);
    }

    @Override
    public String getProfession() {
        return mProfessionEditText.getText().toString();
    }

    @Override
    public void setCompanyName(String companyName) {
        mCompanyNameEditText.setText(companyName);
    }

    @Override
    public String getCompanyName() {
        return mCompanyNameEditText.getText().toString();
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumberEditText.setText(phoneNumber);
    }

    @Override
    public String getPhoneNumber() {
        return mPhoneNumberEditText.getText().toString();
    }

    @Override
    public void setNotes(String notes) {
        mNotesEditText.setText(notes);
    }

    @Override
    public String getNotes() {
        return mNotesEditText.getText().toString();
    }

    @Override
    public void close() {
        if(mActionListener != null) {
            mActionListener.onClose();
        }
    }

    public void closeView() {
        mPresenter.closeView();
    }

    @Override
    public void showDeleteConfirmation() {
        DialogHelper.showConfirmDialog(getFragmentManager(),
                AddEditCustomerActivity.DIALOG_ID_DELETE,
                getString(R.string.dialog_delete_confirmation),
                R.string.ok,
                R.string.cancel);
    }

    @Override
    public void showCancelEditConfirmation() {
        DialogHelper.showConfirmDialog(getFragmentManager(),
                AddEditCustomerActivity.DIALOG_ID_CANCEL_EDIT,
                getString(R.string.cancelEditDiag_message),
                R.string.cancelEditDiag_positive_caption,
                R.string.cancelEditDiag_negative_caption);
    }

    private void showWarningDialog(int message) {
        DialogHelper.showWarningDialog(getContext(), null, getString(message));
    }

    @Override
    public void showEmptyFirstName() {
        // Missing required fields, record cannot be saved
        showWarningDialog(R.string.fill_required_fields_warning);
    }

    @Override
    public void showSaveCompletedMessage() {
        Toast.makeText(getContext(), R.string.save_completed_message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showDeleteCompletedMessage() {
        Toast.makeText(getContext(), R.string.record_deleted_message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putSerializable(AddEditCustomerPresenter.class.getSimpleName(), mPresenter);
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onDestroy() {
        // Dereference the view in the presenter to prevent memory leaks
        mPresenter.unbind();

        super.onDestroy();
    }
}
