package com.samplayer.utils;

import java.util.Locale;

public class SAMLog {

    private static boolean OUTPUT_LOG = true;

    private SAMLog() {
    }

    public static void enable(boolean enable) {
        OUTPUT_LOG = enable;
    }

    public static void e(String tag, String msg) {
        if (!OUTPUT_LOG) {
            return;
        }
        android.util.Log.e(tag, msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (!OUTPUT_LOG) {
            return;
        }
        android.util.Log.e(tag, msg, tr);
    }

    public static void efmt(String tag, String fmt, Object... args) {
        if (!OUTPUT_LOG) {
            return;
        }
        String msg = String.format(Locale.US, fmt, args);
        android.util.Log.e(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (!OUTPUT_LOG) {
            return;
        }
        android.util.Log.i(tag, msg);
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (!OUTPUT_LOG) {
            return;
        }
        android.util.Log.i(tag, msg, tr);
    }

    public static void ifmt(String tag, String fmt, Object... args) {
        if (!OUTPUT_LOG) {
            return;
        }
        String msg = String.format(Locale.US, fmt, args);
        android.util.Log.i(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (!OUTPUT_LOG) {
            return;
        }
        android.util.Log.w(tag, msg);
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (!OUTPUT_LOG) {
            return;
        }
        android.util.Log.w(tag, msg, tr);
    }

    public static void wfmt(String tag, String fmt, Object... args) {
        if (!OUTPUT_LOG) {
            return;
        }
        String msg = String.format(Locale.US, fmt, args);
        android.util.Log.w(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (!OUTPUT_LOG) {
            return;
        }
        android.util.Log.d(tag, msg);
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (!OUTPUT_LOG) {
            return;
        }
        android.util.Log.d(tag, msg, tr);
    }

    public static void dfmt(String tag, String fmt, Object... args) {
        if (!OUTPUT_LOG) {
            return;
        }
        String msg = String.format(Locale.US, fmt, args);
        android.util.Log.d(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (!OUTPUT_LOG) {
            return;
        }
        android.util.Log.v(tag, msg);
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (!OUTPUT_LOG) {
            return;
        }
        android.util.Log.v(tag, msg, tr);
    }

    public static void vfmt(String tag, String fmt, Object... args) {
        if (!OUTPUT_LOG) {
            return;
        }
        String msg = String.format(Locale.US, fmt, args);
        android.util.Log.v(tag, msg);
    }

    public static void printStackTrace(Throwable e) {
        if (!OUTPUT_LOG) {
            return;
        }
        e.printStackTrace();
    }

    public static void printCause(Throwable e) {
        if (!OUTPUT_LOG) {
            return;
        }
        Throwable cause = e.getCause();
        if (cause != null) {
            e = cause;
        }

        printStackTrace(e);
    }
}
