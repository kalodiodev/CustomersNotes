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
package eu.kalodiodev.customersnote.utils.backup;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;


/**
 * Backup Database Task
 *
 * @author Raptodimos Athanasios
 */
public class BackupDBTask implements IBackupDBTask {

    enum BackupStatus {
        COMPLETED,
        REQUIRES_OVERWRITE,
        STORAGE_READ_ONLY
    }

    private String backupFolder;
    private String filename;
    private String sourceDatabase;
    private int taskId;
    private boolean overwriteStatus = false;

    private IBackupDBTask.BackupEvents mBackupEvents;

    public BackupDBTask() {
    }

    @Override
    public void initTask(int taskId, String sourceDatabase, String backupFolder, String filename, IBackupDBTask.BackupEvents backupEvents) {
        this.taskId = taskId;
        this.sourceDatabase = sourceDatabase;
        this.backupFolder = backupFolder;
        this.filename = filename;
        this.mBackupEvents = backupEvents;
    }

    @Override
    public void setOverwrite(boolean status) {
        this.overwriteStatus = status;
    }

    @Override
    public void perform() {
        // Storage state
        String state = Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            new BackupTask(taskId, mBackupEvents, overwriteStatus)
                    .execute(sourceDatabase, backupFolder, filename);

        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mBackupEvents.onBackupReadOnly(taskId);

        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mBackupEvents.onBackupProblem(taskId);
        }
    }

    private class BackupTask extends AsyncTask<String, Void, BackupStatus> {

        private IBackupDBTask.BackupEvents backupEvents;
        private int taskId;
        private DbBackup mDbBackup;
        private boolean overwriteStatus;

        BackupTask(int taskId, IBackupDBTask.BackupEvents backupEvents, boolean overwriteStatus) {
            this.taskId = taskId;
            this.backupEvents = backupEvents;
            this.overwriteStatus = overwriteStatus;
            this.mDbBackup = new LocalDdBackup();
        }

        @Override
        protected BackupStatus doInBackground(String... params) {

            //Destination file
            File Directory = new File(Environment.getExternalStorageDirectory(), params[1]);
            final File file = new File(Directory.getPath(), params[2]);

            if(file.exists() && !overwriteStatus) {
                // File exists and overwrite is not permitted
                return BackupStatus.REQUIRES_OVERWRITE;
            } else if(file.exists()) {
                // File exists and overwrite is permitted
                if(file.delete()) {
                    mDbBackup.backup(params[0], params[1], params[2]);
                    return BackupStatus.COMPLETED;
                }
                return BackupStatus.STORAGE_READ_ONLY;
            } else {
                // File does not exist
                mDbBackup.backup(params[0], params[1], params[2]);
                return BackupStatus.COMPLETED;
            }
        }

        @Override
        protected void onPostExecute(BackupStatus status) {
            switch (status) {
                case COMPLETED:
                    backupEvents.onBackupComplete(taskId);
                    break;
                case REQUIRES_OVERWRITE:
                    mBackupEvents.onBackupFileAlreadyExists(taskId);
                    break;
                case STORAGE_READ_ONLY:
                    mBackupEvents.onBackupReadOnly(taskId);
                    break;
            }

            super.onPostExecute(status);
        }
    }
}
