package com.samsung.SMT.lang.smtshell.shizuku;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.IPackageInstaller;
import android.content.pm.IPackageInstallerSession;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.RemoteException;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

import rikka.shizuku.ShizukuBinderWrapper;

@SuppressWarnings({"JavaReflectionMemberAccess", "ConstantConditions"})
@SuppressLint("PrivateApi")
public class PackageInstallerUtils {

    private static final String TAG = PackageInstallerUtils.class.getSimpleName();

    public static PackageInstaller createPackageInstaller(Context context, IPackageInstaller installer, String installerPackageName, String installerAttributionTag, int userId) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Context appContext = context.getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return PackageInstaller.class.getConstructor(IPackageInstaller.class, String.class, String.class, int.class)
                    .newInstance(installer, installerPackageName, installerAttributionTag, userId);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return PackageInstaller.class.getConstructor(IPackageInstaller.class, String.class, int.class)
                    .newInstance(installer, installerPackageName, userId);
        } else {
            return PackageInstaller.class.getConstructor(Context.class, PackageManager.class, IPackageInstaller.class, String.class, int.class)
                    .newInstance(appContext, appContext.getPackageManager(), installer, installerPackageName, userId);
        }
    }

    public static PackageInstaller.Session createSession(IPackageInstallerSession session) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return PackageInstaller.Session.class.getConstructor(IPackageInstallerSession.class)
                .newInstance(session);

    }

    public static int getInstallFlags(PackageInstaller.SessionParams params) throws NoSuchFieldException, IllegalAccessException {
        return (int) PackageInstaller.SessionParams.class.getDeclaredField("installFlags").get(params);
    }

    public static void setInstallFlags(PackageInstaller.SessionParams params, int newValue) throws NoSuchFieldException, IllegalAccessException {
        PackageInstaller.SessionParams.class.getDeclaredField("installFlags").set(params, newValue);
    }

    @SuppressWarnings({"ConstantConditions", "JavaReflectionMemberAccess"})
    public static boolean installApkFromAssets(Context context, String apkName) {
        Context appContext = context.getApplicationContext();
        try {
            IPackageInstaller _packageInstaller = ShizukuSystemServerApi.getPackageInstaller();

            String installerAttributionTag = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                installerAttributionTag = context.getAttributionTag();
            }
            PackageInstaller packageInstaller = PackageInstallerUtils.createPackageInstaller(
                    appContext, _packageInstaller, "com.sec.android.preloadinstaller", installerAttributionTag, 0);

            PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
            int installFlags = PackageInstallerUtils.getInstallFlags(params);
            installFlags |= (int) PackageManager.class.getField("INSTALL_REPLACE_EXISTING").get(null);
            installFlags |= (int) PackageManager.class.getField("INSTALL_REQUEST_DOWNGRADE").get(null);
            PackageInstallerUtils.setInstallFlags(params, installFlags);

            int sessionId = packageInstaller.createSession(params);

            Log.i(TAG, "createSession: " + sessionId);

            IPackageInstallerSession _session = IPackageInstallerSession.Stub.asInterface(new ShizukuBinderWrapper(_packageInstaller.openSession(sessionId).asBinder()));


            // capture result of install
            IIntentSenderCallback resultsHook = new IIntentSenderCallback();

            try (
                    InputStream is = appContext.getAssets().open(apkName);
                    PackageInstaller.Session session = PackageInstallerUtils.createSession(_session);
            ) {
                // the output stream needs to be closed before calling session.commit(),
                //  otherwise we get a security exception
                try (OutputStream os = session.openWrite("foo.apk", 0, -1)) {
                    // write the APK into the install session stream
                    byte[] buf = new byte[8192];
                    int len;
                    while ((len = is.read(buf)) > 0) {
                        os.write(buf, 0, len);
                        os.flush();
                        session.fsync(os);
                    }
                }

                // commit session with hook
                IntentSender intentSender = IntentSenderUtils.newInstance(resultsHook);
                session.commit(intentSender);
            }

            Intent result = resultsHook.getResult();
            int status = result.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE);
            String message = result.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE);
            Log.i(TAG, String.format("status: %d (%s)", status, message));

            return (0 == status);
        } catch (IOException | NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException | InstantiationException | NoSuchFieldException |
                 RemoteException | InterruptedException e) {
            Log.e(TAG, "failed to install APK", e);
            return false;
        }
    }
}
