package com.samsung.SMT.lang.smtshell;

import android.app.Application;
import android.os.Build;

import org.lsposed.hiddenapibypass.HiddenApiBypass;

public class SMTShell extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // enable hidden API access (required for some Shizuku APIs to work correctly)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.addHiddenApiExemptions("");
        }
    }
}
