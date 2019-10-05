package com.yorubadev.arny.utilities;

import android.content.Context;
import android.os.FileObserver;

import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;

import java.io.File;

public class ProfilePicWatcher implements LifecycleObserver {

    private final FileObserver profilePicObserver;
    private final Lifecycle lifecycle;
    private boolean enabled = false;

    public ProfilePicWatcher(Context context, Lifecycle lifecycle, String userId, MutableLiveData<String> profilePicLiveData) {
        this.lifecycle = lifecycle;
        File profilePicDir = context.getDir("profile_pic", Context.MODE_PRIVATE);
        profilePicObserver = new FileObserver(profilePicDir.getPath(), FileObserver.ALL_EVENTS) {
            @Override
            public void onEvent(int event, @Nullable String path) {
                switch (event) {
                    case 256:
                    case 2:
                        // created or modified
                        if (userId == null) profilePicLiveData.postValue(path == null ? null :
                                path.substring(0, path.length() - 4));
                        else if ((userId + ".jpg").equals(path))
                            profilePicLiveData.postValue(userId);
                        break;
                    case 512:
                        // deleted
                        profilePicLiveData.postValue(path == null ? null :
                                path.substring(0, path.length() - 4));
                        break;
                }
            }
        };
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void start() {
        if (enabled) profilePicObserver.startWatching();
    }

    public void enable() {
        enabled = true;
        if (lifecycle.getCurrentState().isAtLeast(Lifecycle.State.STARTED))
            profilePicObserver.startWatching();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void stop() {
        profilePicObserver.stopWatching();
    }

}
