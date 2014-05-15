package com.rharriso.minstrel.util;

import java.util.Locale;

public class TimeFormatter {

    public static String MillesecondsToTimestamp(int milis) {
        return String.format(Locale.US, "%02d:%02d",
                (int) (milis / (1000 * 60)),
                (int) (milis / 1000) % 60);
    }

}
