package com.samsung.SMT.lang.smtshell;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import net.blufenix.smtshell.api.InternalAPI;
import net.blufenix.smtshell.api.SMTShellAPI;

import java.util.ArrayList;
import java.util.Arrays;

@RequiresApi(api = Build.VERSION_CODES.P)
public class AllTheButtons extends AppCompatActivity {

    ArrayList<SMTCapability> list = new ArrayList<>(Arrays.asList(
            new SMTCapability(
                    "Kill #system_shell_2#'s API",
                    "SHOTS FIRED! SYSTEM is down...SYSTEM is down. RIP MAY 2023.",
                    "Kill", v -> {
                InternalAPI.killAPI(this, success -> {
                    ActivityUtils.launchNewTask(this, MainActivity.class);
                });
            }),
            new SMTCapability(
                    "#system_shell_2# Terminal",
                    "For the lazy people, who dont want that PC life...like ME!",
                    "Launch", v -> {
                ActivityUtils.launch(AllTheButtons.this, ShellActivity.class);
            }),
            new SMTCapability(
                    "For the NERDS, who want that PC life"
                    "NC LYFE 4Ever....nc -l -p 9999",
                    "Launch", v -> {
                SMTShellAPI.loadLibrary(this, getApplicationInfo().nativeLibraryDir + "/" + "libsmtshell.so");
            }),
            new SMTCapability(
                    "Bands Will Make Her Dance",
                    null,
                    "Launch", v -> {
                SMTShellAPI.executeCommand(this, "am start com.samsung.android.app.telephonyui/.hiddennetworksetting.MainActivity");
            }),
            new SMTCapability(
                    "Bands Will Make Her Dance #2)",
                    "After hours edition,
                    "Launch", v -> {
                SMTShellAPI.executeCommand(this, "am broadcast -a com.samsung.android.action.SECRET_CODE -d android_secret_code://2263 -n com.sec.android.RilServiceModeApp/.SecKeyStringBroadcastReceiver");
            }),
            new SMTCapability(
                    "Cave of Wonders - CSC boys!",
                    null,
                    "Launch", v -> {
                SMTShellAPI.executeCommand(this, "am start -n com.samsung.android.cidmanager/.modules.preconfig.PreconfigActivity -a com.samsung.android.action.SECRET_CODE -d secret_code://27262826 --ei type 2");
            }),
            new SMTCapability(
                    "Pandora's Box",
                    "If you come inside, dont cry when you break something...do it at XDA",
                    "Launch", v -> {
                SMTShellAPI.executeCommand(this, "am broadcast -a com.samsung.android.action.SECRET_CODE -d android_secret_code://27663368378 -n com.sec.android.RilServiceModeApp/.SecKeyStringBroadcastReceiver");
            }),
            new SMTCapability(
                    "Info Menu",
                    "Info? What info...I AINT NO SNITCH!",
                    "Launch", v -> {
                SMTShellAPI.executeCommand(this, "am broadcast -a com.samsung.android.action.SECRET_CODE -d android_secret_code://0011 -n com.sec.android.RilServiceModeApp/.SecKeyStringBroadcastReceiver");
            }),
            new SMTCapability(
                    "IOTHiddenMenu",
                    "Enter the Master Menu of IOTHiddenMenu",
                    "Launch", v -> {
                SMTShellAPI.executeCommand(this, "am start -n com.sec.hiddenmenu/.IOTHiddenMenu -e 7267864872 72678647376477466");
            }),
            new SMTCapability(
                    "5G Tile Service in Quick Settings",
                    null,
                    "Launch", v -> {
                SMTShellAPI.executeCommand(this, "am start -n com.samsung.android.app.telephonyui/.carrierui.tile.TurnOn5gTileService");
            }),           
            new SMTCapability(
                    "DSU Loader",
                    "FAKE ROM........You have!.",
                    "Launch", v -> {
                SMTShellAPI.executeCommand(this, "am start -n com.android.settings/.development.DSULoader");
            }},
            new SMTCapability(
                    "Spank Me Daddy!!! Ive been BAD.",
                    "Spawns a notification in the system tray that allows a selected DSU to be discarded.",
                    "Spank Me", v -> {
                SMTShellAPI.executeCommand(this, "am start -n com.android.dynsystem/.VerificationActivity");
            })
    ));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allthebuttons);
        ListView listview = findViewById(R.id.list);
        listview.setAdapter(new ArrayAdapter<SMTCapability>(this, R.layout.descriptive_button, list) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.descriptive_button, null);
                }

                SMTCapability item = getItem(position);

                TextView title = convertView.findViewById(R.id.title);
                title.setText(item.title);

                TextView details = convertView.findViewById(R.id.details);
                details.setText(item.desc);

                Button btn = convertView.findViewById(R.id.btn);
                btn.setText(item.btnText);
                btn.setOnClickListener(item.onClick);

                return convertView;
            }
        });
    }

    private static class SMTCapability {
        String title;
        String desc;
        String btnText;
        View.OnClickListener onClick;

        SMTCapability(String title, String desc, String btnText, View.OnClickListener onClick) {
            this.title = title;
            this.desc = desc;
            this.btnText = btnText;
            this.onClick = onClick;
        }
    }
}
