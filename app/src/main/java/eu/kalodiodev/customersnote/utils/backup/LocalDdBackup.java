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

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Database File Backup or Restore
 *
 * @author Raptodimos Athanasios
 */
public class LocalDdBackup implements DbBackup{

    @Override
    public void backup(String sourceDatabase, String backupFolder, String filename) {
        File Database_file = new File(Environment.getDataDirectory() + sourceDatabase);
        File Directory = new File(Environment.getExternalStorageDirectory() + backupFolder);
        File file = new File(Directory.getPath(), filename);

        //Create Directories
        if ((Directory.mkdir()) || (Directory.exists())) {
            try {
                copyFile(Database_file, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void restore(String sourceDatabase, String backupFolder, String filename) {
        //Database file
        File Database_file = new File(Environment.getDataDirectory() + sourceDatabase);
        File Directory = new File(Environment.getExternalStorageDirectory(), backupFolder);
        final File file = new File(Directory.getPath(), filename);

        //Copy file
        try {
            copyFile(file, Database_file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Check Database Version
    public boolean checkDbVersion(String databaseSource, String backupFolder, String filename) {
        boolean version_ok = false;

        try {
            File Database_file = new File(Environment.getDataDirectory() + databaseSource);
            File Directory = new File(Environment.getExternalStorageDirectory(), backupFolder);
            final File file = new File(Directory.getPath(), filename);

            // open database in readonly mode
            SQLiteDatabase db_backup = SQLiteDatabase.openDatabase(
                    file.toString(), null, SQLiteDatabase.OPEN_READONLY);

            SQLiteDatabase db_current = SQLiteDatabase.openDatabase(
                    Database_file.toString(), null, SQLiteDatabase.OPEN_READONLY);

            if (db_backup.getVersion() == db_current.getVersion()) {
                db_backup.close();
                db_current.close();
                version_ok = true;
            } else {
                db_backup.close();
                db_current.close();
                version_ok = false;
            }

        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        return version_ok;
    }

    //Copy File
    private static void copyFile(File src, File dst) throws IOException {

        FileChannel inChannel = null;
        FileChannel outChannel = null;

        try {
            inChannel = new FileInputStream(src).getChannel();
            outChannel = new FileOutputStream(dst).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }
}
