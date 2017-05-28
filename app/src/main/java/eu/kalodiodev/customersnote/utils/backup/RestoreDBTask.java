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
import android.util.Log;

import java.io.File;

/**
 * Restore Database Task
 *
 * @author Raptodimos Athanasios
 */
public class RestoreDBTask implements IRestoreDBTask {

    enum RestoreStatus {
        COMPLETED,
        VERSION_MISMATCH,
        REQUIRES_OVERWRITE,
        FILE_NOT_FOUND
    }

    private static final String TAG = "RestoreDBTask";

    private String backupFolder;
    private String filename;
    private String sourceDatabase;
    private int taskId;
    private boolean overwriteStatus = false;

    private IRestoreDBTask.RestoreEvents mRestoreEvents;

    public RestoreDBTask() {
    }

    @Override
    public void initTask(int taskId, String sourceDatabase,
                         String backupFolder, String filename,
                         IRestoreDBTask.RestoreEvents restoreEvents) {

        this.backupFolder = backupFolder;
        this.filename = filename;
        this.sourceDatabase = sourceDatabase;
        this.taskId = taskId;

        mRestoreEvents = restoreEvents;
    }

    @Override
    public void setOverwrite(boolean status) {
        this.overwriteStatus = status;
    }

    @Override
    public void perform() {
        // Storage state
        String state = Environment.getExternalStorageState();

        if((Environment.MEDIA_MOUNTED.equals(state)) ||
                (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))) {

            Log.d(TAG, "perform: Media mounted");
            // We can read the media
            new RestoreTask(taskId, mRestoreEvents, overwriteStatus)
                    .execute(sourceDatabase, backupFolder, filename);
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mRestoreEvents.onRestoreProblem(taskId);
        }
    }

    private class RestoreTask extends AsyncTask<String, Void, RestoreStatus> {

        private static final String TAG = "RestoreTask";

        private final DbBackup databaseBackup;
        private IRestoreDBTask.RestoreEvents mRestoreEvents;
        private int taskId;
        private boolean mOverwriteStatus;

        RestoreTask(int taskId, IRestoreDBTask.RestoreEvents restoreEvents, boolean overwriteStatus) {
            this.taskId = taskId;
            mRestoreEvents = restoreEvents;
            this.databaseBackup = new LocalDdBackup();
            this.mOverwriteStatus = overwriteStatus;
        }

        @Override
        protected RestoreStatus doInBackground(String... params) {

            File Directory = new File(Environment.getExternalStorageDirectory(), params[1]);
            final File file = new File(Directory.getPath(), params[2]);

            if(!file.exists()) {
                Log.d(TAG, "doInBackground: Backup file not found");
                // File not found
                return RestoreStatus.FILE_NOT_FOUND;
            }

            return restore(params[0], params[1], params[2]);
        }

        private RestoreStatus restore(String databaseSource, String backupFolder, String filename) {
            // Check database version
            if(databaseBackup.checkDbVersion(databaseSource, backupFolder, filename)) {

                if(mOverwriteStatus) {
                    // Overwrite status is enabled
                    Log.d(TAG, "restore: Will overwrite database");
                    // Restore database
                    databaseBackup.restore(databaseSource, backupFolder, filename);
                    return RestoreStatus.COMPLETED;
                } else {
                    Log.d(TAG, "restore: Overwrite database NOT enabled");
                    // Overwrite status must be enabled
                    return RestoreStatus.REQUIRES_OVERWRITE;
                }

            } else {
                // Database version mismatch
                return RestoreStatus.VERSION_MISMATCH;
            }
        }

        @Override
        protected void onPostExecute(RestoreStatus status) {

            switch (status) {
                case COMPLETED:
                    mRestoreEvents.onRestoreComplete(taskId);
                    break;
                case FILE_NOT_FOUND:
                    mRestoreEvents.onRestoreFileNotFound(taskId);
                    break;
                case REQUIRES_OVERWRITE:
                    mRestoreEvents.onRestoreOverwriteRequired(taskId);
                    break;
                case VERSION_MISMATCH:
                    mRestoreEvents.onRestoreDatabaseVersionMismatch(taskId);
                    break;
            }

            super.onPostExecute(status);
        }
    }
}
