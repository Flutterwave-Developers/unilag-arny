package com.yorubadev.arny.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.talkspaceapp.talkspace.data.Repository;


public class MainActivityViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final Repository mRepository;
    private final Application application;

    public MainActivityViewModelFactory(Application application, Repository repository) {
        this.mRepository = repository;
        this.application = application;
    }

    @Override
    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainActivityViewModel(application, mRepository);
    }
}
