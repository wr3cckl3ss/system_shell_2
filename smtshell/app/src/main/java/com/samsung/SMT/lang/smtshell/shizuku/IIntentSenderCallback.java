package com.samsung.SMT.lang.smtshell.shizuku;

import android.content.IIntentReceiver;
import android.content.IIntentSender;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import java.util.concurrent.CountDownLatch;

public class IIntentSenderCallback extends IIntentSender.Stub {

    private final CountDownLatch mLatch = new CountDownLatch(1);
    private Intent mResult;

    @Override
    public int send(int code, Intent intent, String resolvedType, IIntentReceiver finishedReceiver, String requiredPermission, Bundle options) {
        setResult(intent);
        return 0;
    }

    @Override
    public void send(int code, Intent intent, String resolvedType, IBinder whitelistToken, IIntentReceiver finishedReceiver, String requiredPermission, Bundle options) {
        setResult(intent);
    }

    void setResult(Intent intent) {
        mResult = intent;
        mLatch.countDown();
    }

    public Intent getResult() throws InterruptedException {
        mLatch.await();
        return mResult;
    }
}
