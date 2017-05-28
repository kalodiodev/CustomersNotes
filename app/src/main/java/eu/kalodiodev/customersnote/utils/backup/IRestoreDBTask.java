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
 * Restore Database Task Contract
 *
 * @author Athanasios Raptodimos
 */
public interface IRestoreDBTask {

    /**
     * Restore Events Interface
     */
    interface RestoreEvents {

        /**
         * Action On Restore database backup file not found
         */
        void onRestoreFileNotFound(int taskId);

        /**
         * Action on Restore database backup file version mismatch current database version
         */
        void onRestoreDatabaseVersionMismatch(int taskId);

        /**
         * Action on Restore problem, medial not found
         */
        void onRestoreProblem(int taskId);

        /**
         * Action on restore completed
         */
        void onRestoreComplete(int taskId);

        /**
         * Action on restore overwrite required to be enabled
         */
        void onRestoreOverwriteRequired(int taskId);
    }


    /**
     * Init Restore Backup Task
     *
     * @param taskId task id
     * @param sourceDatabase database source
     * @param backupFolder database backup folder
     * @param filename database backup file
     * @param restoreEvents restore database callback events
     */
    void initTask(int taskId, String sourceDatabase,
                  String backupFolder, String filename,
                  RestoreEvents restoreEvents);

    /**
     * Set overwrite status
     *
     * <p>If overwrite set true then current database in use will be overwrite</p>
     *
     * @param status overwrite status
     */
    void setOverwrite(boolean status);

    /**
     * Perform database restore
     */
    void perform();
}
