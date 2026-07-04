package com.tileunbindfix;

import android.os.Handler;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class TileUnbindHook implements IXposedHookLoadPackage {

    private static final String TAG = "TileUnbindHook";
    private static final String TARGET_PACKAGE = "com.android.systemui";
    private static final String TARGET_CLASS = "com.android.systemui.qs.external.TileServiceManager";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!TARGET_PACKAGE.equals(lpparam.packageName)) {
            return;
        }

        try {
            XposedHelpers.findAndHookMethod(
                    TARGET_CLASS,
                    lpparam.classLoader,
                    "setBindRequested",
                    boolean.class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            boolean requested = (boolean) param.args[0];
                            XposedBridge.log(TAG + ": setBindRequested(" + requested + ") on " + param.thisObject);
                            if (requested) {
                                return;
                            }
                            try {
                                Handler handler = (Handler) XposedHelpers.getObjectField(param.thisObject, "mHandler");
                                Runnable unbindRunnable = (Runnable) XposedHelpers.getObjectField(param.thisObject, "mUnbind");
                                handler.removeCallbacks(unbindRunnable);
                                XposedBridge.log(TAG + ": cancelled pending idle-unbind");
                            } catch (Throwable innerT) {
                                XposedBridge.log(TAG + ": FAILED to cancel unbind - " + innerT);
                            }
                        }
                    }
            );

            XposedBridge.log(TAG + ": hook installed OK on " + TARGET_CLASS);
        } catch (Throwable t) {
            XposedBridge.log(TAG + ": FAILED to install hook on " + TARGET_CLASS + " - " + t);
        }
    }
}
