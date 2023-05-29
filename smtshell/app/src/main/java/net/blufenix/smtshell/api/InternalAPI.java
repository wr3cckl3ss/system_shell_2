package net.blufenix.smtshell.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class InternalAPI extends SMTShellAPI {

    public static final String ACTION_DEACTIVATE = "smtshell.intent.action.DEACTIVATE";

    public static final String PERMISSION_SELF = "smtshell.permission.SELF";

    public static void killAPI(Context context, InternalCallback cb) {
        Intent intent = createIntent(ACTION_DEACTIVATE, nextId());
        if (cb != null) {
            // setup receiver
            context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    context.unregisterReceiver(this);
                    cb.onComplete(true);
                }
            }, new IntentFilter(ACTION_API_DEATH_NOTICE), PERMISSION_RECEIVER_GUARD, null);
        }
        context.sendBroadcast(intent);
    }

    public interface InternalCallback {
        void onComplete(boolean success);
    }

}
