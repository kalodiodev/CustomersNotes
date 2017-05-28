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

import eu.kalodiodev.customersnote.utils.backup.BackupDBTask;
import eu.kalodiodev.customersnote.utils.backup.IBackupDBTask;
import eu.kalodiodev.customersnote.utils.backup.IRestoreDBTask;
import eu.kalodiodev.customersnote.utils.backup.RestoreDBTask;

/**
 * Main Presenter
 *
 * @author Athanasios Raptodimos
 */
class MainPresenter implements MainContract.Presenter,
        BackupDBTask.BackupEvents, RestoreDBTask.RestoreEvents {

    private MainContract.View view;

    private IBackupDBTask mBackupDBTask;
    private IRestoreDBTask mRestoreDBTask;

    // Task Codes
    private static final int TASK_ID_BACKUP = 200;
    private static final int TASK_ID_RESTORE = 201;


    MainPresenter(MainContract.View view, IBackupDBTask backupDBTask,
                         IRestoreDBTask restoreDBTask) {

        this.view = view;
        this.mBackupDBTask = backupDBTask;
        this.mRestoreDBTask = restoreDBTask;

        this.mBackupDBTask.initTask(TASK_ID_BACKUP,
                Constants.Database.SOURCE_DATABASE,
                Constants.Database.BACKUP_FOLDER,
                Constants.Database.BACKUP_FILENAME,
                this);

        this.mRestoreDBTask.initTask(TASK_ID_RESTORE,
                Constants.Database.SOURCE_DATABASE,
                Constants.Database.BACKUP_FOLDER,
                Constants.Database.BACKUP_FILENAME,
                this);
    }

    @Override
    public void backupDatabase(boolean hasPermission, boolean overwrite) {
        if(hasPermission) {
            // Permission to write external storage granted, proceed to backup
            executeBackup(overwrite);
        } else {
            // Permission to write external storage not granted, request permission
            view.requestWritePermission();
        }
    }

    @Override
    public void restoreDatabase(boolean hasPermission, boolean overwrite) {
        if(hasPermission) {
            // Permission to read external storage granted, proceed to restore
            executeRestore(overwrite);
        } else {
            // Permission to read external storage not granted, request permission
            view.requestReadPermission();
        }
    }

    private void executeRestore(boolean overwrite) {
        mRestoreDBTask.setOverwrite(overwrite);
        mRestoreDBTask.perform();
    }

    private void executeBackup(boolean overwrite) {
        mBackupDBTask.setOverwrite(overwrite);
        mBackupDBTask.perform();
    }

    @Override
    public void onBackupReadOnly(int taskId) {
        view.showProblemWarning(MainContract.WarningMessage.BACKUP_READ_ONLY);
    }

    @Override
    public void onBackupProblem(int taskId) {
        view.showProblemWarning(MainContract.WarningMessage.BACKUP_STORAGE_MISSING);
    }

    @Override
    public void onBackupFileAlreadyExists(int taskId) {
        view.showOverwriteBackupFileConfirmation();
    }

    @Override
    public void onBackupComplete(int taskId) {
        view.showBackupCompletedMessage();
    }

    @Override
    public void onRestoreFileNotFound(int taskId) {
        view.showProblemWarning(MainContract.WarningMessage.RESTORE_FILE_NOT_FOUND);
    }

    @Override
    public void onRestoreDatabaseVersionMismatch(int taskId) {
        view.showProblemWarning(MainContract.WarningMessage.RESTORE_VERSION_MISMATCH);
    }

    @Override
    public void onRestoreProblem(int taskId) {
        view.showProblemWarning(MainContract.WarningMessage.RESTORE_PROBLEM);
    }

    @Override
    public void onRestoreComplete(int taskId) {

        view.refreshCustomersList();

        view.showRestoreCompletedMessage();
    }

    @Override
    public void onRestoreOverwriteRequired(int taskId) {
        view.showRestoreOverwriteDatabaseConfirmation();
    }
}
