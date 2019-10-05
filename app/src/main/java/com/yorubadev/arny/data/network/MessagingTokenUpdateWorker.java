package com.yorubadev.arny.data.network;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.ResolvableFuture;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;
import com.yorubadev.arny.AppExecutors;
import com.yorubadev.arny.data.Repository;
import com.yorubadev.arny.utilities.InjectorUtils;
import com.yorubadev.arny.utilities.WorkUtils;

public class MessagingTokenUpdateWorker extends ListenableWorker {

    public static final String NEW_TOKEN = "new_token";

    public MessagingTokenUpdateWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        ResolvableFuture<Result> result = ResolvableFuture.create();
        String token = getInputData().getString(NEW_TOKEN);
        Repository repository = InjectorUtils.provideRepository(getApplicationContext());
        LiveData<String> tokenUpdateLiveData = repository.updateMessagingToken(token);
        AppExecutors executors = InjectorUtils.provideAppExecutors();
        executors.mainThread().execute(() -> {
            tokenUpdateLiveData.observeForever(new Observer<String>() {
                @Override
                public void onChanged(String tokenOrResult) {
                    if (tokenOrResult.equals("success"))
                        result.set(Result.success());
                    else if (tokenOrResult.equals("failure"))
                        result.set(Result.retry());
                    else {
                        WorkUtils.scheduleMessagingTokenUpdateWork(getApplicationContext(), tokenOrResult);
                        result.set(Result.success());
                    }
                    tokenUpdateLiveData.removeObserver(this);
                }
            });
        });
        return result;
    }
}
