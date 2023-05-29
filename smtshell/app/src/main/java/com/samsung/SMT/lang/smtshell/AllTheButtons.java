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
                    "SYSTEM down! SYSTEM down! R.I.P MAY 2023. Samsung couldn't fix their MISTAKES, so they called on Google to do it for them.",
                    "Kill Kill Kill", v -> {
                InternalAPI.killAPI(this, success -> {
                    ActivityUtils.launchNewTask(this, MainActivity.class);
                });
            }),
            new SMTCapability(
                    "#system_shell_2#'s In-House TERMINAL",
                    "Connect to #SS2 directly in the app!",
                    "Welcome 2 the Term LIFE", v -> {
                ActivityUtils.launch(AllTheButtons.this, ShellActivity.class);
            }),
            new SMTCapability(
                    "NC Life",
                    "Reverse shell that can be connected to via:\n adb shell nc -lp 9999, use Termux or any terminal on Win-blows or Linux.",
                    "9999", v -> {
                SMTShellAPI.loadLibrary(this, getApplicationInfo().nativeLibraryDir + "/" + "libsmtshell.so");
            }),
            new SMTCapability(
                    "SHIZUKU",
                    "Go!",
                    "SHIZUKU", v -> {
                InternalAPI.loadShizuku(this);
            }),
            new SMTCapability(
                    "Bands Will Make Her Dance",
                    "Finally...you can enable some 5G in a place that DOESNT have it! LMAOOOO",
                    "Spend A Few Bands", v -> {
                SMTShellAPI.executeCommand(this, "am start com.samsung.android.app.telephonyui/.hiddennetworksetting.MainActivity");
            }),
            new SMTCapability(
                    "Bands Will Make Her Dance #2",
                    "After hours edition...Just make sure your girl's not around",
                    "Busted", v -> {
                SMTShellAPI.executeCommand(this, "am broadcast -a com.samsung.android.action.SECRET_CODE -d android_secret_code://2263 -n com.sec.android.RilServiceModeApp/.SecKeyStringBroadcastReceiver");
            }),
            new SMTCapability(
                    "Band Priority...wait WHAT!?",
                    "Set the Bands at YOUR Level of Priority and Needs....DONT ASK..DONT TELL. Access to this depends on your current CSC.",
                    "BANDS BANDS BANDS", v -> {
                SMTShellAPI.executeCommand(this, "am start -n com.sec.hiddenmenu/.BandPriorityEdit -e 7267864872 72678647376477466");
            }),
            new SMTCapability(
                    "CSC, Welcome to Preconfig",
                    "Now you can stop crying about carrier bloatware",
                    "Flawless Victory", v -> {
                SMTShellAPI.executeCommand(this, "am start -n com.samsung.android.cidmanager/.modules.preconfig.PreconfigActivity -a com.samsung.android.action.SECRET_CODE -d secret_code://27262826 --ei type 2");
            }),
           new SMTCapability(
                    "Service Menu - Pandora's Box",
                    "Kind of boring in there....but you people, just wont give it up. Have fun!",
                    "Super Boring", v -> {
                SMTShellAPI.executeCommand(this, "am broadcast -a com.samsung.android.action.SECRET_CODE -d android_secret_code://27663368378 -n com.sec.android.RilServiceModeApp/.SecKeyStringBroadcastReceiver");
            }),
            new SMTCapability(
                    "Service Menu #2 ...the WHITE version...HUH???",
                    "Dont be racist bro...but its legit WHITE though - No intel on what's different - Use at your discretion.",
                    "Fatality!", v -> {
                SMTShellAPI.executeCommand(this, "am start -n com.sec.hiddenmenu/.ServiceModeApp -e 7267864872 72678647376477466");
            }),
            new SMTCapability(
                    "IOTHiddenMenu - Master Menu",
                    "Dont CRY if you break or disable something...go to your MOTHER or XDA for that!",
                    "Let the WATERWORKS go!", v -> {
                SMTShellAPI.executeCommand(this, "am start -n com.sec.hiddenmenu/.IOTHiddenMenu -e 7267864872 72678647376477466");
            }),
            new SMTCapability(
                    "Carrier-Aggregation",
                    "Enable that shiiiiiiz!",
                    "Carriers wont WIN", v -> {
                SMTShellAPI.executeCommand(this, "am start -n com.sec.hiddenmenu/.CAEnabled_Edit -e 7267864872 72678647376477466");
            }),
            new SMTCapability(
                    "LTE",
                    "Enable/Force different LTE Modes!",
                    "LTE MODE", v -> {
                SMTShellAPI.executeCommand(this, "am start -n com.sec.hiddenmenu/.LTEMode -e 7267864872 72678647376477466");
            }),
            new SMTCapability(
                    "Enable Hidden Menu",
                    "1 of 2 Hidden Menus, just dont ask what they do.",
                    "Y...SO SECRET??", v -> {
                SMTShellAPI.executeCommand(this, "am start -n com.sec.hiddenmenu/.HiddenMenuEnable -e 7267864872 72678647376477466");
            }),
            new SMTCapability(
                    "Enable Global Hidden Menu",
                    "Last One....again, dont ask!",
                    "Best Price!?", v -> {
                SMTShellAPI.executeCommand(this, "am start -n com.sec.hiddenmenu/.GlobalHiddenMenuEnable -e 7267864872 72678647376477466");
            }),
            new SMTCapability(
                    "FIELD TEST MODE",
                    "Information about your device",
                    "INFO WORLD", v -> {
                SMTShellAPI.executeCommand(this, "am start -n com.sec.hiddenmenu/.FIELDTESTMODE -e 7267864872 72678647376477466");
            }),
            new SMTCapability(
                    "Debug Menu",
                    "Again, cry over at that place that starts with an X, if you break something!",
                    "DONT CRY", v -> {
                SMTShellAPI.executeCommand(this, "am start -n com.sec.hiddenmenu/.DEBUGMENU -e 7267864872 72678647376477466");
            }),
            new SMTCapability(
                    "USB Settings",
                    "Quick on-the-go USB change.",
                    "MTP, PTP, DM, ADB", v -> {
                SMTShellAPI.executeCommand(this, "am start -n com.sec.usbsettings/.USBSettings");
            }),
            new SMTCapability(
                    "IMS Settings",
                    "USE EXTREME CAUTION IN THIS MENU...IF YOU DONT KNOW WHAT SOMETHING IS..LEAVE IT ALONE...THIS IS YOUR ONLY WARNING!!!!",
                    "##wr3cckl3ss##", v -> {
                SMTShellAPI.executeCommand(this, "am start -n com.samsung.advp.imssettings/.MainActivity");
            }),
            new SMTCapability(
                    "DM MODE",
                    "If YOU know what this is, please call 281-330-8004 and ask for Mike Jones...who???",
                    "MIKE JONES", v -> {
                SMTShellAPI.executeCommand(this, "am start -n com.sec.hiddenmenu/.DmMode -e 7267864872 72678647376477466");
            }),       
            new SMTCapability(
                    "KOREA-INDIA-SPRINT",
                    "Set device to each one of these modes and figure out what they do because I STILL DONT KNOW!",
                    "Clueless?", v -> {
                SMTShellAPI.executeCommand(this, "am start -n com.sec.hiddenmenu/.KOREA_Mode -e 7267864872 72678647376477466");
            }),
            new SMTCapability(
                    "Secret Calculator???",
                    "DRParser Mode - This is orginally for devices that don't have a DIALER, but can still be useful",
                    "*#1234#", v -> {
                SMTShellAPI.executeCommand(this, "am start -n com.sec.android.app.parser/.SecretCodeIME");                      
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
