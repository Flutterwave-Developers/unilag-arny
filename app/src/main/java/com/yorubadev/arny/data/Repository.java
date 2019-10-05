package com.yorubadev.arny.data;
/*
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


import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.UploadTask;
import com.yorubadev.arny.AppExecutors;
import com.yorubadev.arny.data.database.UserDao;
import com.yorubadev.arny.data.database.UserEntry;
import com.yorubadev.arny.data.network.FirebaseNetworkOperations;

import java.io.File;
import java.util.List;


public class Repository {

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static Repository sInstance;
    private final UserDao mUserDao;
    private final FirebaseNetworkOperations mFirebaseNetworkOperations;
    private final AppExecutors mExecutors;

    private Repository(UserDao userDao,
                       FirebaseNetworkOperations firebaseNetworkOperations,
                       AppExecutors executors) {
        this.mUserDao = userDao;
        this.mFirebaseNetworkOperations = firebaseNetworkOperations;
        mExecutors = executors;
    }

    public synchronized static Repository getInstance(UserDao userDao,
                                                      FirebaseNetworkOperations firebaseNetworkOperations,
                                                      AppExecutors executors) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new Repository(userDao, firebaseNetworkOperations, executors);

            }
        }
        return sInstance;
    }


    /*----------------------
    ------------------------
    --- USER OPERATIONS ----
    ------------------------
    ----------------------*/

    public LiveData<Task<Void>> updateUserDetailsOnFirebase(UserEntry userEntry) {
        return mFirebaseNetworkOperations.updateUserDetails(userEntry);
    }

    public LiveData<UserEntry> getUserById(String userId) {
        return mUserDao.getUserById(userId);
    }

    public void insertNewUser(UserEntry userEntry) {
        mExecutors.diskIO().execute(() -> {
            mUserDao.deleteAllUsers();
            mUserDao.insertUser(userEntry);
        });
    }

    public String getFirebaseUid() {
        return mFirebaseNetworkOperations.getUid();
    }

    /*public void initContacts() {
        mExecutors.diskIO().execute(() -> {
            if (isContactFetchNeeded()) {
                WorkUtils.schedulePeriodicContactResolveWork();
            }
        });
        WorkUtils.schedulePeriodicPresenceUpdateWork();
        mFirebaseNetworkOperations.trackUserStatus();
    }*/

    public void trackUserStatus(LiveData<Boolean> chattingPrefLiveData) {
        mFirebaseNetworkOperations.trackUserStatus(chattingPrefLiveData);
    }

    public LiveData<Task<UploadTask.TaskSnapshot>> uploadImageToFirebase(Uri uri) {
        return mFirebaseNetworkOperations.uploadImage(uri);
    }

    public void fetchImageFromFirebase(File path, String userId) {
        mFirebaseNetworkOperations.fetchImage(path, userId);
    }

    public String getUserPhoneNumber() {
        return mFirebaseNetworkOperations.getUserPhoneNumber();
    }

    public LiveData<UserEntry> fetchUser(String phoneNumber) {
        return mFirebaseNetworkOperations.fetchUser(phoneNumber);
    }

    public void updatePhoneNumber(String userId, String newPhoneNumber) {
        mExecutors.diskIO().execute(() -> mUserDao.updatePhoneNumber(userId, newPhoneNumber));
    }

    // END OF USER OPERATIONS


    /*public void refreshContactsList() {
        WorkUtils.scheduleContactResolveWork();
    }*/

    public LiveData<String> updateMessagingToken(String token) {
        return mFirebaseNetworkOperations.updateMessagingToken(token);
    }

    public Task<List<String>> sendContactsForSync(List<String> contactsToSync) {
        return mFirebaseNetworkOperations.sendContactsForSync(contactsToSync);
    }

    public LiveData<String> fetchContactChatId(String userId, String contactId) {
        return mFirebaseNetworkOperations.fetchContactChatId(userId, contactId);
    }

    public LiveData<Task<Void>> updateContactChatId(String userId, String contactId, String chatId) {
        return mFirebaseNetworkOperations.updateContactChatId(userId, contactId, chatId);
    }

    public void updateContactNameByPhone(String phone, String newName) {
        mExecutors.diskIO().execute(() -> updateContactNameByPhone(phone, newName));
    }

    // END OF CONTACT OPERATIONS

    /*----------------------
    ------------------------
    -- MESSAGE OPERATIONS --
    ------------------------
    ----------------------*/

    public LiveData<Integer> fetchStatus(String contactId) {
        return mFirebaseNetworkOperations.fetchStatusForContact(contactId);
    }

    public LiveData<String> getUserName(String userId) {
        return mUserDao.getUserName(userId);
    }

    public String getMessageId(String chatId, String currentChatId) {
        return mFirebaseNetworkOperations.getMessageId(chatId, currentChatId);
    }

    // END OF MESSAGE OPERATIONS

    /*-----------------------
    -------------------------
    ---- CHAT OPERATIONS ----
    -------------------------
    -----------------------*/

    public LiveData<Integer> trackContactChatPresence(String chatId, String contactId) {
        return mFirebaseNetworkOperations.trackContactChatPresence(chatId, contactId);
    }

    public void leaveConversation(String chatId) {
        mFirebaseNetworkOperations.leaveConversation(chatId);
    }

    public void resumeConversation(String chatId) {
        mFirebaseNetworkOperations.resumeConversation(chatId);
    }

    public void sendScreenshotNotification(String chatId, int screenshotCount) {
        mFirebaseNetworkOperations.sendScreenshotNotification(chatId, screenshotCount);
    }

    public void sendRtt(String chatId, String rtt) {
        mFirebaseNetworkOperations.sendRtt(chatId, rtt);
    }

    public LiveData<Task<Void>> uploadStatusMessage(String newStatus) {
        return mFirebaseNetworkOperations.uploadStatusMessage(newStatus);
    }

    public LiveData<Task<HttpsCallableResult>> sendMissedRequestNotification(@NonNull String recipientId) {
        return mFirebaseNetworkOperations.sendMissedRequestNotification(recipientId);
    }

    public LiveData<Task<Void>> rejectChatRequest(String requestId) {
        return mFirebaseNetworkOperations.rejectChatRequest(requestId);
    }

    public LiveData<Task<Void>> cancelChatRequest(String contactId, String requestId) {
        return mFirebaseNetworkOperations.cancelChatRequest(contactId, requestId);
    }

    public void updateStatusMessage(String contactId, String statusMessage) {
        mExecutors.diskIO().execute(() -> updateStatusMessage(contactId, statusMessage));
    }

    public void sendTypingNotif(String chatId, int typingNotif) {
        mFirebaseNetworkOperations.sendTypingNotif(chatId, typingNotif);
    }
}