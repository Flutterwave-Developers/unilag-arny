package com.yorubadev.arny.utilities;/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.app.Application;
import android.content.Context;

import com.yorubadev.arny.AppExecutors;
import com.yorubadev.arny.data.Repository;
import com.yorubadev.arny.data.database.ArnyDatabase;
import com.yorubadev.arny.data.network.FirebaseNetworkOperations;
import com.yorubadev.arny.ui.authentication.signin.SignInActivityViewModelFactory;
import com.yorubadev.arny.ui.main.MainActivityViewModelFactory;


/**
 * Provides static methods to inject the various classes needed for Sunshine
 */
public class InjectorUtils {

    private static FirebaseNetworkOperations provideFirebaseNetworkOperations() {
        return FirebaseNetworkOperations.getInstance();
    }

    public static Repository provideRepository(Context context) {
        ArnyDatabase database = ArnyDatabase.getInstance(context.getApplicationContext());
        FirebaseNetworkOperations firebaseNetworkOperations = provideFirebaseNetworkOperations();
        AppExecutors executors = provideAppExecutors();

        return Repository.getInstance(database.userDao(), firebaseNetworkOperations, executors);
    }

    public static AppExecutors provideAppExecutors() {
        return AppExecutors.getInstance();
    }

    public static MainActivityViewModelFactory provideMainActivityViewModelFactory(Application application, Context context) {
        Repository repository = provideRepository(context);
        return new MainActivityViewModelFactory(application, repository);
    }

    public static SignInActivityViewModelFactory provideSignInActivityViewModelFactory(Context context) {
        Repository repository = provideRepository(context);
        return new SignInActivityViewModelFactory(repository);
    }
}
