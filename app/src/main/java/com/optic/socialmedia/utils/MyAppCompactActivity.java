package com.optic.socialmedia.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.service.autofill.UserData;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.optic.socialmedia.providers.AuthProviders;
import com.optic.socialmedia.providers.UserDatabaseProvider;

import java.util.List;

public abstract class MyAppCompactActivity extends AppCompatActivity {
    UserDatabaseProvider mUserProvider;
    AuthProviders mAuth;
    boolean p;

    public MyAppCompactActivity() {
        mUserProvider = new UserDatabaseProvider();
        p = true;
        mAuth = new AuthProviders();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.existSession()){
            mUserProvider.updateUserOnline(mAuth.getIdCurrentUser(), true);
        }

    }



    @Override
    protected void onStop() {
        super.onStop();

        if(!isBackgroundRunning()){
            mUserProvider.updateUserOnline(mAuth.getIdCurrentUser(), false);
        }
    }


    public  boolean isBackgroundRunning() {
        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : processInfo.pkgList) {
                    if (activeProcess.equals(this.getPackageName())) {
//If your app is the process in foreground, then it's not in running in background
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
