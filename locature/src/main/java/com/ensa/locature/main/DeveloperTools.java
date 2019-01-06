package com.ensa.locature.main;

import android.app.Application;

import com.ensa.locature.main.internal.LeakLoggerService;
import com.facebook.stetho.Stetho;

public class DeveloperTools {

    public static void setup(Application application) {
        LeakLoggerService.setupLeakCanary(application);
        Stetho.initializeWithDefaults(application);
    }
}
