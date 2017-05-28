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

/**
 * Backup Database Task Contract
 */
public interface IBackupDBTask {

    /**
     * Backup Events Interface
     */
    interface BackupEvents {

        /**
         * Action called when media is readonly
         */
        void onBackupReadOnly(int taskId);

        /**
         * Action called on unresolvable media problem, media missing
         */
        void onBackupProblem(int taskId);

        /**
         * Action called when file already exists
         */
        void onBackupFileAlreadyExists(int taskId);

        /**
         * Action called when perform completed
         */
        void onBackupComplete(int taskId);
    }


    /**
     * Init Backup Task
     *
     * @param taskId task id
     * @param sourceDatabase source of database to backup
     * @param backupFolder folder to store backup file
     * @param filename backup filename
     * @param backupEvents backup callback events
     */
    void initTask(int taskId, String sourceDatabase, String backupFolder,
                  String filename, BackupEvents backupEvents);

    /**
     * Perform Backup Task
     */
    void perform();

    /**
     * Set overwrite status for database backup file
     *
     * @param status true to overwrite backup file, otherwise false
     */
    void setOverwrite(boolean status);

}


