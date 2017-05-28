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
 * Database File Backup and Restore Interface
 *
 * @author Raptodimos Athanasios
 */
interface DbBackup {

    /**
     * Backup Database
     *
     * @param sourceDatabase database source
     * @param backupFolder folder to store perform file
     * @param filename database filename
     */
    void backup(String sourceDatabase, String backupFolder, String filename);

    /**
     * Restore Database
     *
     * @param sourceDatabase database source
     * @param backupFolder folder perform file is stored
     * @param filename database filename
     */
    void restore(String sourceDatabase, String backupFolder, String filename);

    /**
     * Check database version match
     *
     * @param sourceDatabase database source
     * @param backupFolder database backup folder
     * @param filename database filename
     * @return true if backup database version matches current database version, otherwise false
     */
    boolean checkDbVersion(String sourceDatabase, String backupFolder, String filename);
}
