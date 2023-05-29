package com.samsung.SMT.lang.smtshell;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * We need to keep the minSdkVersion at 22 or lower, so use @RequiresApi to use newer stuff.
 * This only needs to support Android 9.0 (API 28) and higher anyway.
 */
@RequiresApi(api = Build.VERSION_CODES.P)
public class ConflictActivity extends AppCompatActivity {

    private TextView mTextView;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conflict);
        mTextView = findViewById(R.id.text);
        mListView = findViewById(R.id.list);
        if (resolvePackageConflicts()) {
            ActivityUtils.launchNewTask(this, MainActivity.class);
        }
    }

    /**
     * This will fire when we get a response from an uninstall request. No need to check the
     *  requestCode or resultCode, since we only care about one result for now.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resolvePackageConflicts()) {
            ActivityUtils.launchNewTask(this, MainActivity.class);
        }
    }

    private boolean resolvePackageConflicts() {
        ArrayList<String> pkgs = ConflictUtil.getPackageConflicts(this);

        if (pkgs.size() > 0) {
            mTextView.setText(R.string.app_conflict_prompt);
            mListView.setAdapter(new ArrayAdapter<>(this, R.layout.pkg_item, pkgs));
            mListView.setOnItemClickListener((parent, view, position, id) -> {
                String pkgName = pkgs.get(position);
                Intent intent = new Intent(Intent.ACTION_DELETE);
                intent.setData(Uri.parse("package:" + pkgName));
                startActivityForResult(intent, 0);
            });
            return false;
        } else {
            mTextView.setText(R.string.no_conflicts);
            mListView.setAdapter(null);
            return true;
        }
    }

}
