import static net.blufenix.smtshell.api.InternalAPI.ACTION_DEACTIVATE;
import static net.blufenix.smtshell.api.InternalAPI.PERMISSION_SELF;
import static net.blufenix.smtshell.api.SMTShellAPI.ACTION_API_DEATH_NOTICE;
import static net.blufenix.smtshell.api.SMTShellAPI.ACTION_API_PING;
import static net.blufenix.smtshell.api.SMTShellAPI.ACTION_API_READY;
import static net.blufenix.smtshell.api.SMTShellAPI.ACTION_LOAD_LIBRARY;
import static net.blufenix.smtshell.api.SMTShellAPI.ACTION_LOAD_LIBRARY_RESULT;
import static net.blufenix.smtshell.api.SMTShellAPI.ACTION_SHELL_COMMAND;
import static net.blufenix.smtshell.api.SMTShellAPI.ACTION_SHELL_RESULT;
import static net.blufenix.smtshell.api.SMTShellAPI.EXTRA_CALLBACK_PKG;
import static net.blufenix.smtshell.api.SMTShellAPI.EXTRA_COMMAND;
import static net.blufenix.smtshell.api.SMTShellAPI.EXTRA_EXIT_CODE;
import static net.blufenix.smtshell.api.SMTShellAPI.EXTRA_LIBRARY_PATH;
import static net.blufenix.smtshell.api.SMTShellAPI.EXTRA_LOAD_SUCCESS;
import static net.blufenix.smtshell.api.SMTShellAPI.EXTRA_REQUEST_ID;
import static net.blufenix.smtshell.api.SMTShellAPI.EXTRA_STDERR;
import static net.blufenix.smtshell.api.SMTShellAPI.EXTRA_STDOUT;
import static net.blufenix.smtshell.api.SMTShellAPI.PERMISSION_LOAD_LIBRARY;
import static net.blufenix.smtshell.api.SMTShellAPI.PERMISSION_SYSTEM_COMMAND;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

@SuppressLint("PrivateApi")
@RequiresApi(api = Build.VERSION_CODES.P)
public class SystemEntrypoint {

    private static final String TAG = "SMTShell";
    private static final HashSet<BroadcastReceiver> sRegisteredReceivers = new HashSet<>();
    private static Application sAppContext;

    public static void main(String[] args) {
        try {
            sAppContext = getAppContext();
            Log.i(TAG, "got app context: " + sAppContext);
        } catch (ReflectiveOperationException e) {
            Log.e(TAG, "failed to find app context!");
            return;
        }

        Log.i(TAG, "registering broadcast receivers ...");

        // commands
        register(new CommandReceiver(), ACTION_SHELL_COMMAND, PERMISSION_SELF);
        register(new CommandReceiver(), ACTION_SHELL_COMMAND, PERMISSION_SYSTEM_COMMAND);

        // load library requests
        register(new LoadLibraryReceiver(), ACTION_LOAD_LIBRARY, PERMISSION_SELF);
        register(new LoadLibraryReceiver(), ACTION_LOAD_LIBRARY, PERMISSION_LOAD_LIBRARY);

        // API pings (no permission needed)
        register(new PingReceiver(), ACTION_API_PING, null);

        // internal receiver to kill the API
        register(new KillReceiver(), ACTION_DEACTIVATE, PERMISSION_SELF);

        // send initial ping to our app only
        Log.i(TAG, "sending ready msg");
        sAppContext.sendBroadcast(new Intent(ACTION_API_READY).setPackage("com.samsung.SMT.lang.smtshell"));

        Log.i(TAG, "API ready");
    }

    private static BroadcastReceiver register(BroadcastReceiver receiver, String action, String permission) {
        sAppContext.registerReceiver(receiver, new IntentFilter(action), permission, null);
        sRegisteredReceivers.add(receiver);
        return receiver;
    }

    private static void unregisterAll() {
        for (BroadcastReceiver br : sRegisteredReceivers) {
            sAppContext.unregisterReceiver(br);
        }
        sRegisteredReceivers.clear();
    }

    private static Application getAppContext() throws ReflectiveOperationException {
        return (Application) Class.forName("android.app.ActivityThread")
                .getMethod("currentApplication").invoke(null, (Object[]) null);
    }

    private static String streamToString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader isOut = new BufferedReader(new InputStreamReader(is))) {
            isOut.lines().forEach(s -> sb.append(s).append("\n"));
        }
        return sb.toString();
    }

    private static class PingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            IntentSender sender = intent.getParcelableExtra(EXTRA_CALLBACK_PKG);
            if (sender == null) {
                Log.e(TAG, "no sender specified; will not receive results");
            } else {
                String pkg = sender.getCreatorPackage();
                sAppContext.sendBroadcast(new Intent(ACTION_API_READY).setPackage(pkg));
            }
        }
    }

    private static class CommandReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "received intent: " + intent.toUri(Intent.URI_INTENT_SCHEME));

            String cmd = intent.getStringExtra(EXTRA_COMMAND);
            int id = intent.getIntExtra(EXTRA_REQUEST_ID, -1);
            IntentSender sender = intent.getParcelableExtra(EXTRA_CALLBACK_PKG);

            if (cmd == null) {
                Log.e(TAG, "no command specified");
                return;
            }

            if (sender == null) {
                Log.w(TAG, "no sender specified; will not receive results");
            }

            // execute the command in a new thread
            AsyncTask.execute(() -> {
                try {
                    Runtime rt = Runtime.getRuntime();
                    Process p = rt.exec(cmd);

                    // send results to caller
                    if (sender != null) {
                        String stdout = streamToString(p.getInputStream());
                        String stderr = streamToString(p.getErrorStream());
                        int exitCode = p.waitFor();
                        Log.i(TAG, String.format("command complete (id: %d)", id));

                        String pkg = sender.getCreatorPackage();
                        Intent callback = new Intent(ACTION_SHELL_RESULT);
                        callback.putExtra(EXTRA_REQUEST_ID, id);
                        callback.putExtra(EXTRA_STDOUT, stdout);
                        callback.putExtra(EXTRA_STDERR, stderr);
                        callback.putExtra(EXTRA_EXIT_CODE, exitCode);
                        callback.setPackage(pkg);
                        sAppContext.sendBroadcast(callback);
                        Log.i(TAG, String.format("callback sent (id: %d)", id));
                    }
                } catch (IOException | InterruptedException e) {
                    Log.e(TAG, String.format("failed to run command (id: %d): %s", id, cmd));
                }
            });
        }
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    private static class LoadLibraryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "received intent: " + intent.toUri(Intent.URI_INTENT_SCHEME));

            String libPath = intent.getStringExtra(EXTRA_LIBRARY_PATH);
            int id = intent.getIntExtra(EXTRA_REQUEST_ID, -1);
            IntentSender sender = intent.getParcelableExtra(EXTRA_CALLBACK_PKG);

            if (libPath == null) {
                Log.e(TAG, "no library specified");
                return;
            }

            if (sender == null) {
                Log.w(TAG, "no sender specified; will not receive results");
            }

            // execute the command in a new thread
            AsyncTask.execute(() -> {
                boolean success = false;
                try {
                    System.load(libPath);
                    success = true;
                    Log.i(TAG, String.format("load complete (id: %d)", id));
                } catch (RuntimeException e) {
                    Log.e(TAG, String.format("load failed (id: %d): %s", id, libPath));
                }

                if (sender != null) {
                    String pkg = sender.getCreatorPackage();
                    Intent callback = new Intent(ACTION_LOAD_LIBRARY_RESULT);
                    callback.putExtra(EXTRA_REQUEST_ID, id);
                    callback.putExtra(EXTRA_LOAD_SUCCESS, success);
                    callback.setPackage(pkg);
                    sAppContext.sendBroadcast(callback);
                    Log.i(TAG, String.format("callback sent (id: %d)", id));
                }
            });
        }
    }

    private static class KillReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // disable all receivers
            unregisterAll();
            // clear the app preferences that cause us to be loaded
            sAppContext.getSharedPreferences("SamsungTTSSettings", 0).edit().clear().commit();
            // notify all apps
            sAppContext.sendBroadcast(new Intent(ACTION_API_DEATH_NOTICE));

            // use pm clear to kill the process because the TTS service like to come back to life
            //  if we stop it any other way
            new Handler(sAppContext.getMainLooper()).postDelayed(() -> {
                try {
                    Runtime.getRuntime().exec("pm clear com.samsung.SMT");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, 1000);
        }
    }

}
