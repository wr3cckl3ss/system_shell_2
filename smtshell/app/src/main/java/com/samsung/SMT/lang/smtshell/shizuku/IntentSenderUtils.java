package com.samsung.SMT.lang.smtshell.shizuku;

import android.content.IIntentSender;
import android.content.IntentSender;

import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("JavaReflectionMemberAccess")
public class IntentSenderUtils {

    public static IntentSender newInstance(IIntentSender binder) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        return IntentSender.class.getConstructor(IIntentSender.class).newInstance(binder);
    }
}
