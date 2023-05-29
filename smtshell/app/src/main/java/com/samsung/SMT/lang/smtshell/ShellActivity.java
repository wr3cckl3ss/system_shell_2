package com.samsung.SMT.lang.smtshell;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import net.blufenix.smtshell.api.SMTShellAPI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ShellActivity extends Activity {

    private static final String TAG = ShellActivity.class.getSimpleName();

    private static final int port = 9999;

    private ScrollView mScrollView;
    private TextView mTermOutput;
    private EditText mInputField;

    private Socket mSocket;
    private OutputStream mOutStream;
    private InputStream mInStream;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shell);

        mScrollView = findViewById(R.id.scrollView);
        mTermOutput = findViewById(R.id.textView);
        mInputField = findViewById(R.id.editText);
        mInputField.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                String command = mInputField.getText().toString()+"\n";
                sendCommand(command);
                mInputField.setText("");
                return true;
            }
            return false;
        });

        SMTShellAPI.loadLibrary(this, getApplicationInfo().nativeLibraryDir + "/" + "libsmtshell.so");

        AsyncTask.execute(new ConnectTask());
    }

    private void sendCommand(String command) {
        appendOutput(command);
        AsyncTask.execute(new SendTask(command));
    }

    private void appendOutput(String output) {
        mTermOutput.append(output);
    }

    private class ConnectTask implements Runnable {
        @Override
        public void run() {
            try (ServerSocket ss = new ServerSocket(port)) {
                mSocket = ss.accept();
                mOutStream = mSocket.getOutputStream();
                mInStream = mSocket.getInputStream();
                startReaderThread();
            } catch (IOException e) {
                Log.e(TAG, "error connecting to shell!", e);
            }
        }
    }

    private class SendTask implements Runnable {
        private final String cmd;

        SendTask(String cmd) {
            this.cmd = cmd;
        }

        @Override
        public void run() {
            try {
                mOutStream.write(cmd.getBytes());
            } catch (IOException e) {
                Log.e(TAG, "error running command!", e);
            }
        }
    }

    private void startReaderThread() {
        new Thread(() -> {
            try {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = mInStream.read(buffer)) != -1) {
                    String output = new String(buffer, 0, bytesRead);
                    runOnUiThread(() -> {
                        appendOutput(output);
                        // post the scroll as a new runnable so the view can update and remeasure
                        mScrollView.post(() -> mScrollView.fullScroll(View.FOCUS_DOWN));
                    });
                }
            } catch (IOException e) {
                Log.e(TAG, "error reading shell stream!", e);
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mSocket != null) {
                mSocket.close();
            }
            if (mOutStream != null) {
                mOutStream.close();
            }
            if (mInStream != null) {
                mInStream.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "error closing shell", e);
        }
    }
}
