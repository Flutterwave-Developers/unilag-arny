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
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.UploadTask;
import com.talkspaceapp.talkspace.AppExecutors;
import com.talkspaceapp.talkspace.data.database.BasicContactInfo;
import com.talkspaceapp.talkspace.data.database.ChatAcceptance;
import com.talkspaceapp.talkspace.data.database.ChatMessage;
import com.talkspaceapp.talkspace.data.database.ChatRequest;
import com.talkspaceapp.talkspace.data.database.ContactDao;
import com.talkspaceapp.talkspace.data.database.ContactEntry;
import com.talkspaceapp.talkspace.data.database.ContactListItem;
import com.talkspaceapp.talkspace.data.database.ContactStatusItem;
import com.talkspaceapp.talkspace.data.database.MissedRequestData;
import com.talkspaceapp.talkspace.data.database.ModifiableUserDataWithPhone;
import com.talkspaceapp.talkspace.data.database.OnlineContactItem;
import com.talkspaceapp.talkspace.data.database.RecentChatItem;
import com.talkspaceapp.talkspace.data.database.UserDao;
import com.talkspaceapp.talkspace.data.database.UserEntry;
import com.talkspaceapp.talkspace.data.network.FirebaseNetworkOperations;

import java.io.File;
import java.util.HashMap;
import java.util.List;


public class Repository {

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static Repository sInstance;
    private final UserDao mUserDao;
    private final ContactDao mContactDao;
    private final FirebaseNetworkOperations mFirebaseNetworkOperations;
    private final AppExecutors mExecutors;

    private Repository(UserDao userDao,
                       ContactDao contactDao,
                       FirebaseNetworkOperations firebaseNetworkOperations,
                       AppExecutors executors) {
        this.mUserDao = userDao;
        this.mContactDao = contactDao;
        this.mFirebaseNetworkOperations = firebaseNetworkOperations;
        mExecutors = executors;
    }

    public synchronized static Repository getInstance(UserDao userDao,
                                                      ContactDao contactDao,
                                                      FirebaseNetworkOperations firebaseNetworkOperations,
                                                      AppExecutors executors) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new Repository(userDao, contactDao, firebaseNetworkOperations, executors);

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
            deleteAllContacts();
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

    public LiveData<ModifiableUserDataWithPhone> getModifiableUserDataWithPhone(String userId) {
        return mUserDao.getModifiableUserDataWithPhone(userId);
    }

    public void updatePhoneNumber(String userId, String newPhoneNumber) {
        mExecutors.diskIO().execute(() -> mUserDao.updatePhoneNumber(userId, newPhoneNumber));
    }

    // END OF USER OPERATIONS


    /*----------------------
    ------------------------
    -- CONTACT OPERATIONS --
    ------------------------
    ----------------------*/

    public LiveData<List<ContactEntry>> getContacts() {
        return mContactDao.getContacts();
    }

    public void updateContactPhone(String contactId, String newPhone) {
        mExecutors.diskIO().execute(() -> mContactDao.updateContactPhone(contactId, newPhone));
    }

    private boolean isContactFetchNeeded() {
        return mContactDao.countAllContacts() <= 0;
    }

    public LiveData<Integer> getContactsCount() {
        return mContactDao.getContactsCount();
    }

    public LiveData<String> getChatId(String contactId) {
        return mContactDao.getChatId(contactId);
    }

    public LiveData<List<ContactEntry>> getAllContacts() {
        return mContactDao.getContactsOrderedByName();
    }

    public LiveData<List<ContactStatusItem>> getContactStatusItems() {
        return mContactDao.getContactStatusItems();
    }

    public LiveData<List<ContactListItem>> getContactListItems() {
        return mContactDao.getContactListItems();
    }

    public LiveData<List<ContactEntry>> getOnlineContacts() {
        return mContactDao.getOnlineContactsOrderedByName();
    }

    public LiveData<List<ContactListItem>> getOnlineContactListItems() {
        return mContactDao.getOnlineContactListItems();
    }

    public LiveData<List<OnlineContactItem>> getRecentOnlineContacts() {
        return mContactDao.getRecentOnlineContacts();
    }

    public LiveData<ContactEntry> getContact(String pushId) {
        return mContactDao.getContact(pushId);
    }

    public LiveData<String> getContactName(String pushId) {
        return mContactDao.getContactName(pushId);
    }

    public void updateStatus(String contactId, int status) {
        mExecutors.diskIO().execute(() -> mContactDao.updateStatus(contactId, status));
    }

    public void updateUserStatusMessage(String statusMessage) {
        String userId = getFirebaseUid();
        if (userId != null)
            mExecutors.diskIO().execute(() -> mUserDao.updateStatusMessage(userId, statusMessage));
    }

    public void updateUserName(String name) {
        String userId = getFirebaseUid();
        if (userId != null)
            mExecutors.diskIO().execute(() -> mUserDao.updateName(userId, name));
    }

    public void clearMissedRequests(String contactId) {
        mExecutors.diskIO().execute(() -> mContactDao.clearMissedRequestCount(contactId));
    }

    public LiveData<Integer> getTotalMissedRequestCount() {
        return mContactDao.getTotalMissedRequestCount();
    }

    public LiveData<Integer> getTotalMissedRequestCount(String contactId) {
        return mContactDao.getTotalMissedRequestCount(contactId);
    }

    public LiveData<MissedRequestData> getMissedRequestData(String contactId) {
        return mContactDao.getMissedRequestData(contactId);
    }

    /*public void refreshContactsList() {
        WorkUtils.scheduleContactResolveWork();
    }*/

    public LiveData<String> updateMessagingToken(String token) {
        return mFirebaseNetworkOperations.updateMessagingToken(token);
    }

    public LiveData<String> getPhoneNumber(String contactId) {
        return mContactDao.getPhoneNumber(contactId);
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

    public void updateMissedRequestCount(String contactId, int newCount) {
        mExecutors.diskIO().execute(() -> mContactDao.updateMissedRequestCount(contactId, newCount));
    }

    public LiveData<List<BasicContactInfo>> getBasicContactInfos() {
        return mContactDao.getBasicContactInfos();
    }

    public void updateContactNameByPhone(String phone, String newName) {
        mExecutors.diskIO().execute(() -> updateContactNameByPhone(phone, newName));
    }

    public void deleteContactByPhone(String phoneNumber) {
        mExecutors.diskIO().execute(() -> mContactDao.deleteContactByPhone(phoneNumber));
    }

    public void deleteAllContacts() {
        mExecutors.diskIO().execute(mContactDao::deleteAllContacts);
    }

    public void insertContact(ContactEntry contactEntry) {
        mExecutors.diskIO().execute(() -> mContactDao.insertContact(contactEntry));
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

    public LiveData<Boolean> isContactChatting(String contactId) {
        return mFirebaseNetworkOperations.isContactChatting(contactId);
    }

    public LiveData<Boolean> isContactChatQueueFull(String contactId) {
        return mFirebaseNetworkOperations.isContactChatQueueFull(contactId);
    }

    public LiveData<String> requestChat(String contactId, String chatId, String name, int duration, boolean screenshotPref, boolean rttPref) {
        return mFirebaseNetworkOperations.requestChat(contactId, chatId, name, duration, screenshotPref, rttPref);
    }

    public LiveData<String> getUserName(String userId) {
        return mUserDao.getUserName(userId);
    }

    public LiveData<ChatAcceptance> monitorRequestStatus(String contactId, String requestId) {
        return mFirebaseNetworkOperations.monitorRequestStatus(contactId, requestId);
    }

    public LiveData<Task<Void>> acceptChatRequest(String requestId, int duration, boolean screenshotPref, boolean rttPref) {
        return mFirebaseNetworkOperations.acceptChatRequest(requestId, duration, screenshotPref, rttPref);
    }

    public LiveData<Integer> monitorRequest(String chatId, String requesterId) {
        return mFirebaseNetworkOperations.monitorRequest(chatId, requesterId);
    }

    public String getMessageId(String chatId, String currentChatId) {
        return mFirebaseNetworkOperations.getMessageId(chatId, currentChatId);
    }

    public LiveData<Task<Void>> sendMessage(ChatMessage chatMessage, String currentChatId) {
        return mFirebaseNetworkOperations.sendMessage(chatMessage, currentChatId);
    }

    public LiveData<ChatMessage> fetchChatMessages(String chatId, String currentChatId) {
        return mFirebaseNetworkOperations.fetchChatMessages(chatId, currentChatId);
    }

    // END OF MESSAGE OPERATIONS

    /*-----------------------
    -------------------------
    ---- CHAT OPERATIONS ----
    -------------------------
    -----------------------*/

    public LiveData<Task<Void>> startRequestedChat(String userId, String contactId, ChatRequest request) {
        return mFirebaseNetworkOperations.startRequestedChat(userId, contactId, request);
    }

    public LiveData<Task<Void>> startAcceptedChat(String contactId, ChatRequest request) {
        return mFirebaseNetworkOperations.startAcceptedChat(contactId, request);
    }

    public MutableLiveData<Boolean> trackChatPresence(String chatId, MutableLiveData<Boolean> connectionLiveData) {
        return mFirebaseNetworkOperations.trackChatPresence(chatId, connectionLiveData);
    }

    public LiveData<Integer> trackContactChatPresence(String chatId, String contactId) {
        return mFirebaseNetworkOperations.trackContactChatPresence(chatId, contactId);
    }

    public LiveData<List<RecentChatItem>> getAllRecentChats() {
        return mContactDao.getRecentChats();
    }

    public void leaveConversation(String chatId) {
        mFirebaseNetworkOperations.leaveConversation(chatId);
    }

    public void resumeConversation(String chatId) {
        mFirebaseNetworkOperations.resumeConversation(chatId);
    }

    public LiveData<Integer> getScreenshotCount(String contactId, String chatId) {
        return mFirebaseNetworkOperations.getScreenshotCount(contactId, chatId);
    }

    public void sendScreenshotNotification(String chatId, int screenshotCount) {
        mFirebaseNetworkOperations.sendScreenshotNotification(chatId, screenshotCount);
    }

    public void sendRtt(String chatId, String rtt) {
        mFirebaseNetworkOperations.sendRtt(chatId, rtt);
    }

    public LiveData<String> getRtt(String contactId, String chatId) {
        return mFirebaseNetworkOperations.getRtt(contactId, chatId);
    }

    public LiveData<Task<Void>> uploadStatusMessage(String newStatus) {
        return mFirebaseNetworkOperations.uploadStatusMessage(newStatus);
    }

    public LiveData<HashMap<String, Object>> fetchOngoingChats() {
        return mFirebaseNetworkOperations.fetchOngoingChats();
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

    public LiveData<Task<Void>> endChat(ChatRequest chat) {
        return mFirebaseNetworkOperations.endChat(chat);
    }

    public void updateLastChatTimestamp(String contactId, long lastChatTimestamp) {
        mExecutors.diskIO().execute(() -> mContactDao.updateLastChatTimestamp(contactId, lastChatTimestamp));
    }

    public void updateStatusMessage(String contactId, String statusMessage) {
        mExecutors.diskIO().execute(() -> updateStatusMessage(contactId, statusMessage));
    }

    public LiveData<Integer> getTypingNotif(String contactId, String chatId) {
        return mFirebaseNetworkOperations.getTypingNotif(contactId, chatId);
    }

    public void sendTypingNotif(String chatId, int typingNotif) {
        mFirebaseNetworkOperations.sendTypingNotif(chatId, typingNotif);
    }
}