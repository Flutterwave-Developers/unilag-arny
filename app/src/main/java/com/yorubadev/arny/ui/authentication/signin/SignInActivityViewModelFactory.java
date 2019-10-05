package com.yorubadev.arny.ui.authentication.signin;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.yorubadev.arny.data.Repository;


public class SignInActivityViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final Repository mRepository;

    public SignInActivityViewModelFactory(Repository repository) {
        this.mRepository = repository;
    }

    @Override
    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new SignInActivityViewModel(mRepository);
    }
}
