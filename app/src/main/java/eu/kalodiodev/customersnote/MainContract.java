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

/**
 * Main Contract
 *
 * @author Athanasios Raptodimos
 */
interface MainContract {

    /**
     * Warning messages enum
     */
    enum WarningMessage {
        BACKUP_READ_ONLY,
        BACKUP_STORAGE_MISSING,
        RESTORE_FILE_NOT_FOUND,
        RESTORE_VERSION_MISMATCH,
        RESTORE_PROBLEM
    }

    /**
     * View Contract
     */
    interface View {

        /**
         * Show Write Permission Request
         */
        void requestWritePermission();

        /**
         * Show Read Permission Request
         */
        void requestReadPermission();

        /**
         * Show Confirmation to overwrite backup file
         */
        void showOverwriteBackupFileConfirmation();

        /**
         * Show Warning for problem occurred
         *
         * @param message type of warning message {@link WarningMessage}
         */
        void showProblemWarning(WarningMessage message);

        /**
         * Show Message that backup completed
         */
        void showBackupCompletedMessage();

        /**
         * Show message that database restore completed
         */
        void showRestoreCompletedMessage();

        /**
         * Show Confirmation to overwrite database on restore
         */
        void showRestoreOverwriteDatabaseConfirmation();

        /**
         * Force customers list refresh
         */
        void refreshCustomersList();
    }

    /**
     * Presenter Contract
     */
    interface Presenter {

        /**
         * Backup database to external storage
         *
         * @param hasPermission true if it has permission to write on external storage,
         *                      otherwise false
         * @param overwrite true if it can overwrite current backup file, otherwise false
         */
        void backupDatabase(boolean hasPermission, boolean overwrite);

        /**
         * Restore database from external storage backup file
         *
         * @param hasPermission true if it has permission to read external storage,
         *                      otherwise false
         * @param overwrite true if it can overwrite current database with the one from backup file,
         *                  otherwise false
         */
        void restoreDatabase(boolean hasPermission, boolean overwrite);
    }
}
