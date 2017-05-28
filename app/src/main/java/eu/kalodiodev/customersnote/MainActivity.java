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

package eu.kalodiodev.customersnote;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import eu.kalodiodev.customersnote.addeditcustomer.AddEditCustomerActivity;
import eu.kalodiodev.customersnote.addeditcustomer.AddEditCustomerPresenter;
import eu.kalodiodev.customersnote.customers.CustomersContract;
import eu.kalodiodev.customersnote.customers.CustomersFragment;
import eu.kalodiodev.customersnote.customers.CustomersPresenter;
import eu.kalodiodev.customersnote.customers.CursorRecyclerViewAdapter;
import eu.kalodiodev.customersnote.data.CustomersRepository;
import eu.kalodiodev.customersnote.addeditcustomer.AddEditCustomerFragment;
import eu.kalodiodev.customersnote.data.Customer;
import eu.kalodiodev.customersnote.data.source.LoaderProvider;
import eu.kalodiodev.customersnote.utils.AppDialog;
import eu.kalodiodev.customersnote.utils.DialogHelper;
import eu.kalodiodev.customersnote.utils.backup.BackupDBTask;
import eu.kalodiodev.customersnote.utils.backup.RestoreDBTask;

/**
 * Main Activity
 *
 * @author Athanasios Raptodimos
 */
public class MainActivity extends AppCompatActivity implements MainContract.View, CustomersFragment.OnAddEditCustomer,
        CursorRecyclerViewAdapter.OnCustomerClickListener, AddEditCustomerFragment.OnAction,
        AppDialog.DialogEvents {

    private static final String TAG = "MainActivity";

    // Whether or not the activity is in 2-pane mode
    // i.e. running in landscape on a tablet
    private boolean mTwoPane = false;

    // Dialog Codes
    private static final int DIALOG_ID_CANCEL_EDIT = 2;
    private static final int DIALOG_ID_BACKUP_OVERWRITE = 3;
    private static final int DIALOG_ID_RESTORE_OVERWRITE = 4;

    // Request Codes
    private static final int REQUEST_WRITE_TO_SD = 100;
    private static final int REQUEST_READ_FROM_SD = 101;

    // Module scope because we need to dismiss it in onStop,
    // e.g. when orientation changes to avoid memory leaks.
    private AlertDialog mDialog = null;

    private CustomersContract.Presenter mCustomersPresenter;
    private MainContract.Presenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(findViewById(R.id.customer_details_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // If this view is present, then the activity should be in two-pane mode.
            mTwoPane = true;
        }

        CustomersFragment customersFragment = setupCustomersFragment();
        // Main Presenter
        mainPresenter = createMainPresenter();
        // Customers Presenter
        LoaderProvider loaderProvider = new LoaderProvider(this);
        mCustomersPresenter = createCustomersPresenter(loaderProvider, customersFragment);
    }

    private CustomersContract.Presenter createCustomersPresenter(LoaderProvider loaderProvider,
                                                                 CustomersFragment customersFragment) {
        CustomersContract.Presenter customersPresenter = new CustomersPresenter(loaderProvider,
                this.getSupportLoaderManager(),
                customersFragment,
                CustomersRepository.getInstance(getContentResolver()));

        customersFragment.setPresenter(customersPresenter);

        return customersPresenter;
    }

    private MainContract.Presenter createMainPresenter() {
        return new MainPresenter(this, new BackupDBTask(), new RestoreDBTask());
    }

    private CustomersFragment setupCustomersFragment() {

        CustomersFragment customersFragment = (CustomersFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);

        if(customersFragment == null) {
            customersFragment = new CustomersFragment();

            replaceFragment(R.id.fragment, customersFragment);
        }
        return customersFragment;
    }

    private void replaceFragment(int containerViewId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment);
        fragmentTransaction.commit();
    }

    private void removeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .remove(fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                // About Activity
                showAboutDialog();
                return true;
            case R.id.action_backup:
                mainPresenter.backupDatabase(hasPermissionToWriteExternalStorage(), false);
                return true;
            case R.id.action_restore:
                mainPresenter.restoreDatabase(hasPermissionToReadExternalStorage(), false);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean hasPermissionToWriteExternalStorage() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasPermissionToReadExternalStorage() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_WRITE_TO_SD: // Write to external storage request
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mainPresenter.backupDatabase(hasPermissionToWriteExternalStorage(), false);
                }
                break;
            case REQUEST_READ_FROM_SD: // Read from external storage request
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mainPresenter.restoreDatabase(hasPermissionToReadExternalStorage(), false);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onEditClick(Customer customer) {
        Log.d(TAG, "onEditClick: starts");

        mCustomersPresenter.editCustomer(customer);
    }

    @Override
    public void onClose() {
        // Close called, remove fragment
        Fragment fragment = getSupportFragmentManager()
                .findFragmentById(R.id.customer_details_container);

        if(fragment != null) {
            removeFragment(fragment);
        }
    }

    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {
        switch (dialogId) {
            case AddEditCustomerActivity.DIALOG_ID_DELETE:
                AddEditCustomerFragment fragment = (AddEditCustomerFragment)
                        getSupportFragmentManager().findFragmentById(R.id.customer_details_container);

                if(fragment != null) {
                    // Delete customer
                    fragment.confirmedCustomerDeletion();
                }
                break;
            case DIALOG_ID_CANCEL_EDIT:
                // Nothing to do
                break;
            case DIALOG_ID_BACKUP_OVERWRITE:
                // Overwrite database backup file
                mainPresenter.backupDatabase(hasPermissionToWriteExternalStorage(), true);
                break;
            case DIALOG_ID_RESTORE_OVERWRITE:
                // Overwrite with database backup file
                mainPresenter.restoreDatabase(hasPermissionToReadExternalStorage(), true);
                break;
        }
    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {
        switch (dialogId) {
            case AddEditCustomerActivity.DIALOG_ID_DELETE:
                // Nothing to do
                break;
            case DIALOG_ID_CANCEL_EDIT:
                finish();
                break;
            case DIALOG_ID_BACKUP_OVERWRITE:
                // Overwrite database backup file dialog
                // Nothing to do
                break;
            case DIALOG_ID_RESTORE_OVERWRITE:
                // Nothing to do
                break;
        }
    }

    @Override
    public void onDialogCancelled(int dialogId) {
        // Nothing to do
    }

    private void showAboutDialog() {
        @SuppressLint("InflateParams") View messageView = getLayoutInflater()
                .inflate(R.layout.about, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setView(messageView);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });

        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(true);

        TextView versionTextView = (TextView) messageView.findViewById(R.id.version_textView);
        versionTextView.setText(getString(R.string.about_version, BuildConfig.VERSION_NAME));

        mDialog.show();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called");
        AddEditCustomerFragment fragment = (AddEditCustomerFragment)
                getSupportFragmentManager().findFragmentById(R.id.customer_details_container);

        if(fragment == null) {
            super.onBackPressed();
        } else {
            fragment.close();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onEditCustomer(Customer customer) {
        if(mTwoPane) {
            createAddEditCustomerFragment(customer);

        } else {
            // in single-pane mode, start then detail activity for the selected item id
            startAddEditCustomerActivity(customer);
        }
    }

    @Override
    public void onAddCustomer() {
        if(mTwoPane) {
            Log.d(TAG, "onClick: Fab click, two pane (tablet)");
            createAddEditCustomerFragment(null);
        } else {
            Log.d(TAG, "onClick: Fab click, single pane");
            startAddEditCustomerActivity(null);
        }
    }

    private void startAddEditCustomerActivity(Customer customer) {
        Intent intent = new Intent(this, AddEditCustomerActivity.class);
        if (customer != null) {
            // Editing customer1
            intent.putExtra(Customer.class.getSimpleName(), customer);
        }
        startActivity(intent);
    }

    private void createAddEditCustomerFragment(Customer customer) {

        AddEditCustomerFragment addEditFragment = new AddEditCustomerFragment();

        if (customer != null) {
            Bundle arguments = new Bundle();
            arguments.putSerializable(Customer.class.getSimpleName(), customer);
            addEditFragment.setArguments(arguments);
        }

        replaceFragment(R.id.customer_details_container, addEditFragment);

        AddEditCustomerPresenter mAddEditCustomerPresenter = new AddEditCustomerPresenter(
                customer, addEditFragment, CustomersRepository.getInstance(getContentResolver()));

        addEditFragment.setPresenter(mAddEditCustomerPresenter);
    }

    @Override
    public void requestWritePermission() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            // Show an explanation to the user asynchronously
            Snackbar.make(findViewById(R.id.main_layout),
                    R.string.backup_grant_access_explanation, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.grant_access, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_WRITE_TO_SD);
                        }
                    }).show();
        } else {
            Log.d(TAG, "requestExternalStorageWritePermission: User has permanently denied the permission");
            // The user has permanently denied the permission, so take them to the settings
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
            intent.setData(uri);
            MainActivity.this.startActivity(intent);
        }
    }

    @Override
    public void requestReadPermission() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            // Show an explanation to the user asynchronously
            Snackbar.make(findViewById(R.id.main_layout),
                    R.string.restore_grant_access_explanation, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.grant_access, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    REQUEST_READ_FROM_SD);
                        }
                    }).show();
        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_FROM_SD);
        }
    }

    @Override
    public void showOverwriteBackupFileConfirmation() {
        // Show dialogue to get confirmation to overwrite backup file
        DialogHelper.showConfirmDialog(getSupportFragmentManager(),
                DIALOG_ID_BACKUP_OVERWRITE,
                getString(R.string.backup_overwrite_confirm_text),
                R.string.overwrite,
                R.string.cancel);
    }

    @Override
    public void showProblemWarning(MainContract.WarningMessage readOnly) {
        String message;

        switch (readOnly) {
            case BACKUP_READ_ONLY:
                // External Storage is read only
                message = getString(R.string.external_storage_readonly);
                break;
            case BACKUP_STORAGE_MISSING:
                // External Storage is missing
                message = getString(R.string.external_storage_missing);
                break;
            case RESTORE_FILE_NOT_FOUND:
                // Database backup file not found
                message = getString(R.string.dialog_database_backup_not_found);
                break;
            case RESTORE_VERSION_MISMATCH:
                // Database backup file version mismatch
                message = getString(R.string.dialog_database_version_mismatch);
                break;
            case RESTORE_PROBLEM:
                // External storage missing
                message = getString(R.string.external_storage_missing);
                break;
            default:
                throw new IllegalArgumentException("Unknown warning message argument");
        }

        DialogHelper.showWarningDialog(this, getString(R.string.dialog_problem_title), message);
    }

    @Override
    public void showBackupCompletedMessage() {
        Toast.makeText(this, R.string.backup_done, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showRestoreCompletedMessage() {
        Toast.makeText(this, R.string.restore_complete, Toast.LENGTH_LONG).show();
    }

    @Override
    public void refreshCustomersList() {
        ((CustomersFragment) getSupportFragmentManager().findFragmentById(R.id.fragment)).refresh();
    }

    @Override
    public void showRestoreOverwriteDatabaseConfirmation() {
        // Show dialogue to get confirmation to overwrite database on Restore
        DialogHelper.showConfirmDialog(getSupportFragmentManager(),
                DIALOG_ID_RESTORE_OVERWRITE,
                getString(R.string.dialog_restore_overwrite_confirmation),
                R.string.overwrite,
                R.string.cancel);
    }
}