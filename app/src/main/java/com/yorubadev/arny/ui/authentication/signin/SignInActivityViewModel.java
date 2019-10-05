package com.yorubadev.arny.ui.authentication.signin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.yorubadev.arny.data.Repository;
import com.yorubadev.arny.data.database.UserEntry;


class SignInActivityViewModel extends ViewModel {
    private final Repository mRepository;

    SignInActivityViewModel(Repository repository) {
        mRepository = repository;
    }

    LiveData<Task<Void>> setUserDetailsOnFirebase(UserEntry userEntry) {
        return mRepository.updateUserDetailsOnFirebase(userEntry);
    }

    void insertNewUserInDatabase(UserEntry userEntry) {
        mRepository.insertNewUser(userEntry);
    }

    String getFirebaseUid() {
        return mRepository.getFirebaseUid();
    }
}