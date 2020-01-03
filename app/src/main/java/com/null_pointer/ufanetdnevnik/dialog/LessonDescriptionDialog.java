package com.null_pointer.diarycloud.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.null_pointer.diarycloud.R;

/**
 * Created by null_pointer on 15.04.17.
 */
// ������ ��������� � �����
public class LessonDescriptionDialog {
    private AlertDialog alert;

    public LessonDescriptionDialog(Context context, String title, String message, String buttonText){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton(buttonText,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        alert = builder.create();
    }

    public AlertDialog getAlert(){
        return alert;
    }

    /*

        alert.show();
     */
}
