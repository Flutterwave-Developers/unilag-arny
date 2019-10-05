package com.yorubadev.arny.data.network;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.yorubadev.arny.data.database.UserEntry;
import com.yorubadev.arny.utilities.InjectorUtils;


public class UserDetailsUpdateWorker extends Worker {

    public static final String USER_NAME = "user_name";
    public static final String USER_PHONE_NUMBER = "user_phone_number";
    public static final String USER_UID = "user_uid";

    public UserDetailsUpdateWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String name = getInputData().getString(USER_NAME);
        String phoneNumber = getInputData().getString(USER_PHONE_NUMBER);
        String uid = getInputData().getString(USER_UID);
        if (uid == null) return Result.failure();
        UserEntry userEntry = new UserEntry(uid, name, phoneNumber, 5.0);
        InjectorUtils.provideRepository(getApplicationContext()).updateUserDetailsOnFirebase(userEntry);
        return Result.success();
    }
}
