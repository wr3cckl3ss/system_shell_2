package com.samsung.SMT.lang.smtshell;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.widget.ArrayAdapter;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * We need to keep the minSdkVersion at 22 or lower, so use @RequiresApi to use newer stuff.
 * This only needs to support Android 9.0 (API 28) and higher anyway.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class ConflictUtil {

    public static ArrayList<String> getPackageConflicts(Context context) {
        ArrayList<String> pkgs = context.getPackageManager()
                .getInstalledPackages(PackageManager.MATCH_ALL)
                .stream()
                .map(packageInfo -> packageInfo.packageName)
                .filter(pkgName -> pkgName.startsWith("com.samsung.SMT.lang"))
                .filter(pkgName -> !pkgName.equals(context.getPackageName()))
                .collect(Collectors.toCollection(ArrayList::new));

        return pkgs;
    }

    public static boolean hasConflicts(Context context) {
        return getPackageConflicts(context).size() > 0;
    }

}
