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
package eu.kalodiodev.customersnote.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import eu.kalodiodev.customersnote.R;

/**
 * Dialog Helper, contains common dialogs
 *
 * @author Raptodimos Athanasios
 */
public class DialogHelper {

    /**
     * Show Confirm Dialog
     * caller must implement {@link AppDialog.DialogEvents}
     *
     * @param fm Fragment Manager
     * @param dialogId dialog id
     * @param message message to show
     * @param positiveRID positive text resource id
     * @param negativeRID negative text resource id
     */
    public static void showConfirmDialog(FragmentManager fm, int dialogId, String message,
                                         int positiveRID, int negativeRID) {

        // Show dialogue to get confirmation to quit editing
        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, dialogId);
        args.putString(AppDialog.DIALOG_MESSAGE, message);
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, positiveRID);
        args.putInt(AppDialog.DIALOG_NEGATIVE_RID, negativeRID);

        dialog.setArguments(args);
        dialog.show(fm, null);
    }

    /**
     * Show warning dialog
     *
     * @param context caller context
     * @param title title to be shown
     * @param message message to be shown
     */
    public static void showWarningDialog(Context context, String title, String message) {
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setIcon(R.drawable.ic_warning_black_24dp);
        alertDialog.setMessage(message);
        alertDialog.setButton(-1, context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //Show read only dialog
        alertDialog.show();
    }
}
