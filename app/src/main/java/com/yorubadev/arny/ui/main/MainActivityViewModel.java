package com.yorubadev.arny.ui.main;

import android.app.Application;

import androidx.lifecycle.ViewModel;

import com.yorubadev.arny.data.Repository;


class MainActivityViewModel extends ViewModel {
    private final Repository mRepository;
    private final Application mApplication;
    MainActivityViewModel(Application application, Repository repository) {
        mRepository = repository;
        mApplication = application;
    }

    String getUid() {
        return mRepository.getFirebaseUid();
    }
}