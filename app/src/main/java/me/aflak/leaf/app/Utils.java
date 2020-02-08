package me.aflak.leaf.app;

import android.os.Handler;

public class Utils {
    public static void executeNTimes(int n, int intervalMs, Runnable runnable) {
        class Counter {
            int i = 1;
        }

        final Counter counter = new Counter();
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                runnable.run();
                if (counter.i < n) {
                    counter.i++;
                    handler.postDelayed(this, intervalMs);
                }
            }
        });
    }
}
