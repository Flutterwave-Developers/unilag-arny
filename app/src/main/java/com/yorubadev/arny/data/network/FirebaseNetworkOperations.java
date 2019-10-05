package com.yorubadev.arny.data.network;

import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.talkspaceapp.talkspace.AppExecutors;
import com.talkspaceapp.talkspace.data.database.ChatAcceptance;
import com.talkspaceapp.talkspace.data.database.ChatMessage;
import com.talkspaceapp.talkspace.data.database.ChatRequest;
import com.talkspaceapp.talkspace.data.database.UserEntry;
import com.talkspaceapp.talkspace.utilities.Constants;
import com.talkspaceapp.talkspace.utilities.FirebaseUtils;
import com.talkspaceapp.talkspace.utilities.InjectorUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirebaseNetworkOperations {


    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static FirebaseNetworkOperations sInstance;
    private FirebaseAuth mAuth;

    private FirebaseNetworkOperations() {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null)
            Crashlytics.setUserIdentifier(mAuth.getCurrentUser().getUid());
    }

    public static FirebaseNetworkOperations getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new FirebaseNetworkOperations();
            }
        }
        return sInstance;
    }

    public String getUid() {
        return mAuth.getCurrentUser() == null ? null : mAuth.getCurrentUser().getUid();
    }

    public String getUserPhoneNumber() {
        return mAuth.getCurrentUser() == null ? null : mAuth.getCurrentUser().getPhoneNumber();
    }

    /*public String getUserDisplayName() {
        FirebaseUser user = mAuth.getCurrentUser();
        return user == null ? null : user.getDisplayName();
    }*/

    public LiveData<Task<Void>> updateUserDetails(UserEntry userEntry) {
        MutableLiveData<Task<Void>> liveData = new MutableLiveData<>();

        DatabaseReference userRef = FirebaseUtils.getUserRef(userEntry.getPhoneNumber());
        userRef.setValue(userEntry).addOnCompleteListener(liveData::setValue);
        return liveData;
    }

    public LiveData<UserEntry> fetchUser(String phoneNumber) {
        DatabaseReference userRef = FirebaseUtils.getUserRef(phoneNumber);
        FirebaseQueryLiveData firebaseQueryLiveData = new FirebaseQueryLiveData(userRef, FirebaseQueryLiveData.ONE_TIME_QUERY);
        return Transformations.map(firebaseQueryLiveData, new UserEntryDeserializer());
    }

    public LiveData<String> fetchContactChatId(String userId, String contactId) {
        DatabaseReference contactChatIdRef = FirebaseUtils.getContactChatIdRef(userId, contactId);
        FirebaseQueryLiveData firebaseQueryLiveData = new FirebaseQueryLiveData(contactChatIdRef, FirebaseQueryLiveData.ONE_TIME_QUERY);
        return Transformations.map(firebaseQueryLiveData, new FirebaseStringDeserializer());
    }

    public LiveData<Task<Void>> updateContactChatId(String userId, String contactId, String chatId) {
        MutableLiveData<Task<Void>> liveData = new MutableLiveData<>();
        DatabaseReference chatIdsRef = FirebaseUtils.getBaseChatIdsRef();
        HashMap<String, Object> updateMap = new HashMap<>();
        String userPath = FirebaseUtils.makeRef(userId, contactId);
        String contactPath = FirebaseUtils.makeRef(contactId, userId);
        updateMap.put(userPath, chatId);
        updateMap.put(contactPath, chatId);
        chatIdsRef.updateChildren(updateMap).addOnCompleteListener(liveData::setValue);
        return liveData;
    }

    public LiveData<Task<UploadTask.TaskSnapshot>> uploadImage(Uri uri) {
        MutableLiveData<Task<UploadTask.TaskSnapshot>> liveData = new MutableLiveData<>();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            StorageReference profilePicRef = FirebaseUtils.getStorageProfilePicRef(currentUser.getUid());
            profilePicRef.putFile(uri).addOnCompleteListener(liveData::setValue);
        } else liveData.setValue(null);
        return liveData;
    }

    public void fetchImage(File path, @NonNull String userId) {
        StorageReference profilePicRef = FirebaseUtils.getStorageProfilePicRef(userId);
        try {
            profilePicRef.getFile(path);
        } catch (OutOfMemoryError e) {
            Crashlytics.logException(e);
        }
    }


    public LiveData<Integer> fetchStatusForContact(String contactId) {
        DatabaseReference contactPresenceRef = FirebaseUtils.getPresenceRef(contactId);
        FirebaseQueryLiveData presenceLiveData = new FirebaseQueryLiveData(contactPresenceRef, FirebaseQueryLiveData.RECURRING_QUERY);
        return Transformations.map(presenceLiveData, new FirebaseIntegerDeserializer());
    }

    static ChatRequest createChatRequestFromHashMap(@NonNull HashMap<String, Object> chatMap) {
        String pushId = (String) chatMap.get(Constants.FIELD_PUSH_ID);
        String chatId = (String) chatMap.get(Constants.FIELD_CHAT_ID);
        String requesterId = (String) chatMap.get(Constants.FIELD_REQUESTER_ID);
        String recipientId = (String) chatMap.get(Constants.FIELD_RECIPIENT_ID);
        String phone = (String) chatMap.get(Constants.FIELD_PHONE);
        Object objScreenshotPref = chatMap.get(Constants.FIELD_SCREENSHOT_PREF);
        boolean screenshotPref = objScreenshotPref != null && (boolean) objScreenshotPref;
        Object objRttPref = chatMap.get(Constants.FIELD_RTT_PREF);
        boolean rttPref = objRttPref != null && (boolean) objRttPref;
        Object objDuration = chatMap.get(Constants.FIELD_DURATION);
        int duration = objDuration == null ? 30 : (int) ((long) objDuration);

        return new ChatRequest(pushId != null ? pushId : "", chatId, requesterId, recipientId, duration, phone, screenshotPref, rttPref);
    }

    public LiveData<Boolean> isContactChatting(String contactId) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        DatabaseReference ongoingRef = FirebaseUtils.getOngoingChatsRef(contactId);
        FirebaseQueryLiveData ongoingLiveData = new FirebaseQueryLiveData(ongoingRef, FirebaseQueryLiveData.ONE_TIME_QUERY);
        AppExecutors executors = InjectorUtils.provideAppExecutors();
        executors.mainThread().execute(() -> ongoingLiveData.observeForever(new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                liveData.setValue(dataSnapshot == null ? null : dataSnapshot.getValue() != null);
                ongoingLiveData.removeObserver(this);
            }
        }));
        return liveData;
    }

    public LiveData<Boolean> isContactChatQueueFull(String contactId) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        DatabaseReference queueRef = FirebaseUtils.getQueuedChatsRef(contactId);
        FirebaseQueryLiveData queueLiveData = new FirebaseQueryLiveData(queueRef, FirebaseQueryLiveData.ONE_TIME_QUERY);
        AppExecutors executors = InjectorUtils.provideAppExecutors();
        executors.mainThread().execute(() -> queueLiveData.observeForever(new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                liveData.setValue(dataSnapshot == null ? null : dataSnapshot.getValue() != null);
                queueLiveData.removeObserver(this);
            }
        }));
        return liveData;
    }

    public LiveData<String> requestChat(String contactId, String chatId, String name, int duration, boolean screenshotPref, boolean rttPref) {
        MutableLiveData<String> liveData = new MutableLiveData<>();
        String userId = getUid();
        if (userId == null) liveData.setValue(null);
        else {
            if (TextUtils.isEmpty(name)) name = getUserPhoneNumber();
            DatabaseReference queueRef = FirebaseUtils.getQueuedChatsRef(contactId);
            DatabaseReference requestQueueRef = queueRef.push();
            String requestId = requestQueueRef.getKey();
            if (requestId == null) liveData.setValue(null);
            else {
                ChatRequest newRequest = new ChatRequest(requestId, chatId, userId, contactId, duration, name, screenshotPref, rttPref);
                queueRef.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        Object request = mutableData.getValue();
                        if (request != null) return Transaction.success(mutableData);
                        else {
                            mutableData.child(requestId).setValue(newRequest);
                            return Transaction.success(mutableData);
                        }
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                        if (!committed) {
                            liveData.setValue(null);
                        } else {
                            if (dataSnapshot == null) liveData.setValue("");
                            else {
                                HashMap<String, Object> data = (HashMap<String, Object>) dataSnapshot.getValue();
                                if (data == null) liveData.setValue("");
                                else {
                                    try {
                                        String pushId = (String) Objects.requireNonNull(data.keySet().toArray())[0];
                                        boolean isRequestSuccessful = pushId.equals(requestId);
                                        if (!isRequestSuccessful) liveData.setValue("");
                                        else {
                                            liveData.setValue(requestId);
                                        }
                                    } catch (NullPointerException e) {
                                        Crashlytics.logException(e);
                                        liveData.setValue("");
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }

        return liveData;
    }

    public LiveData<HashMap<String, Object>> fetchOngoingChats() {
        String userId = getUid();
        MutableLiveData<HashMap<String, Object>> liveData = new MutableLiveData<>();
        if (userId == null) liveData.setValue(null);
        else {
            DatabaseReference ongoingRef = FirebaseUtils.getOngoingChatsRef(userId);
            FirebaseQueryLiveData ongoingLiveData = new FirebaseQueryLiveData(ongoingRef, FirebaseQueryLiveData.ONE_TIME_QUERY);
            AppExecutors executors = InjectorUtils.provideAppExecutors();
            executors.mainThread().execute(() -> ongoingLiveData.observeForever(new Observer<DataSnapshot>() {
                @Override
                public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                    if (dataSnapshot == null) liveData.setValue(null);
                    else {
                        try {
                            HashMap<String, Object> ongoingChatsMap = (HashMap<String, Object>) dataSnapshot.getValue();
                            liveData.setValue(ongoingChatsMap);
                        } catch (DatabaseException databaseException) {
                            Crashlytics.logException(databaseException);
                            liveData.setValue(null);
                        }
                    }
                    ongoingLiveData.removeObserver(this);
                }
            }));
        }
        return liveData;
    }

    public LiveData<Integer> monitorRequest(String chatId, String requesterId) {
        MutableLiveData<Integer> liveData = new MutableLiveData<>();
        String userId = getUid();
        if (userId == null) liveData.setValue(null);
        else {
            DatabaseReference contactApprovalRef = FirebaseUtils.getUserChatApprovalRef(chatId, requesterId);
            FirebaseQueryLiveData approvalQueryLiveData = new FirebaseQueryLiveData(contactApprovalRef, FirebaseQueryLiveData.RECURRING_QUERY);
            LiveData<Integer> approvalLiveData = Transformations.map(approvalQueryLiveData, new FirebaseIntegerDeserializer());
            AppExecutors executors = InjectorUtils.provideAppExecutors();
            executors.mainThread().execute(() -> approvalLiveData.observeForever(new Observer<Integer>() {
                int count = 0;

                @Override
                public void onChanged(@Nullable Integer requestStatus) {
                    if (count > 0) {
                        liveData.setValue(requestStatus);
                        approvalLiveData.removeObserver(this);
                    }
                    count++;
                }
            }));
        }
        return liveData;
    }

    public LiveData<Task<Void>> startRequestedChat(String userId, String contactId, ChatRequest request) {
        MutableLiveData<Task<Void>> liveData = new MutableLiveData<>();
        DatabaseReference chatsRef = FirebaseUtils.getBaseAllChatsRef();
        HashMap<String, Object> updateMap = new HashMap<>();
        String ongoingPath = FirebaseUtils.makeRef(userId, Constants.FIELD_CHATS_ONGOING,
                request.getPushId());
        String queuePath = FirebaseUtils.makeRef(contactId, Constants.FIELD_CHATS_QUEUE,
                request.getPushId());
        updateMap.put(ongoingPath, request);
        updateMap.put(queuePath, null);
        chatsRef.updateChildren(updateMap).addOnCompleteListener(liveData::setValue);
        return liveData;
    }

    public LiveData<Task<Void>> startAcceptedChat(String contactId, ChatRequest request) {
        MutableLiveData<Task<Void>> liveData = new MutableLiveData<>();
        DatabaseReference userOngoingChatsRef = FirebaseUtils.getOngoingChatsRef(contactId);
        userOngoingChatsRef.child(request.getPushId()).setValue(request).addOnCompleteListener(liveData::setValue);
        return liveData;
    }

    public LiveData<ChatAcceptance> monitorRequestStatus(String contactId, String requestId) {
        MutableLiveData<ChatAcceptance> liveData = new MutableLiveData<>();
        String userId = getUid();
        if (userId == null) liveData.setValue(null);
        else {
            DatabaseReference contactChatApprovalRef = FirebaseUtils.getUserChatApprovalRef(requestId, contactId);
            FirebaseQueryLiveData requestStatusLiveData = new FirebaseQueryLiveData(contactChatApprovalRef, FirebaseQueryLiveData.RECURRING_QUERY);
            LiveData<Integer> statusLiveData = Transformations.map(requestStatusLiveData, new FirebaseIntegerDeserializer());
            AppExecutors executors = InjectorUtils.provideAppExecutors();
            executors.mainThread().execute(() -> statusLiveData.observeForever(new Observer<Integer>() {
                @Override
                public void onChanged(@Nullable Integer status) {
                    if (status != null) {
                        if (status == Constants.CHAT_REQUEST_STATUS_REJECTED) {
                            liveData.setValue(new ChatAcceptance(status, true, true));
                        } else {
                            DatabaseReference requestDurationRef = FirebaseUtils.getQueuedChatRef(contactId, requestId).child(Constants.FIELD_DURATION);
                            FirebaseQueryLiveData requestDurationLiveData = new FirebaseQueryLiveData(requestDurationRef, FirebaseQueryLiveData.ONE_TIME_QUERY);
                            LiveData<ChatAcceptance> durationLiveData = Transformations.map(requestDurationLiveData, new ChatAcceptanceDeserializer());
                            durationLiveData.observeForever(new Observer<ChatAcceptance>() {
                                @Override
                                public void onChanged(@Nullable ChatAcceptance chatAcceptance) {
                                    liveData.setValue(chatAcceptance);
                                    durationLiveData.removeObserver(this);
                                }
                            });
                        }
                        statusLiveData.removeObserver(this);
                    }
                }
            }));
        }
        return liveData;
    }

    public LiveData<Task<Void>> acceptChatRequest(String requestId, int duration, boolean screenshotPref, boolean rttPref) {
        MutableLiveData<Task<Void>> liveData = new MutableLiveData<>();
        String userId = getUid();
        if (userId == null) liveData.setValue(null);
        else {
            DatabaseReference requestRef = FirebaseUtils.getQueuedChatRef(userId, requestId);
            FirebaseQueryLiveData requestQueryLiveData = new FirebaseQueryLiveData(requestRef, FirebaseQueryLiveData.ONE_TIME_QUERY);
            LiveData<ChatRequest> requestLiveData = Transformations.map(requestQueryLiveData, new ChatRequestDeserializer());
            AppExecutors executors = InjectorUtils.provideAppExecutors();
            executors.mainThread().execute(() -> requestLiveData.observeForever(new Observer<ChatRequest>() {
                @Override
                public void onChanged(@Nullable ChatRequest basicChatRequest) {
                    if (basicChatRequest == null) liveData.setValue(null);
                    else {
                        ChatAcceptance chatAcceptance = new ChatAcceptance(duration, screenshotPref, rttPref);
                        DatabaseReference chatsRef = FirebaseUtils.getBaseChatsRef();
                        HashMap<String, Object> updateMap = new HashMap<>();
                        String approvalRef = FirebaseUtils.makeRef(Constants.FIELD_APPROVAL, requestId, userId);
                        String durationRef = FirebaseUtils.makeRef(Constants.FIELD_ALL, userId, Constants.FIELD_CHATS_QUEUE, requestId, Constants.FIELD_DURATION);
                        updateMap.put(approvalRef, Constants.CHAT_REQUEST_STATUS_ACCEPTED);
                        updateMap.put(durationRef, chatAcceptance);
                        chatsRef.updateChildren(updateMap).addOnCompleteListener(liveData::setValue);
                    }
                    requestLiveData.removeObserver(this);
                }
            }));
        }
        return liveData;
    }

    public LiveData<Task<Void>> rejectChatRequest(String requestId) {
        MutableLiveData<Task<Void>> liveData = new MutableLiveData<>();
        String userId = getUid();
        if (userId == null) liveData.setValue(null);
        else {
            DatabaseReference chatsRef = FirebaseUtils.getBaseChatsRef();
            HashMap<String, Object> updateMap = new HashMap<>();
            String approvalRef = FirebaseUtils.makeRef(Constants.FIELD_APPROVAL, requestId, userId);
            String requestRef = FirebaseUtils.makeRef(Constants.FIELD_ALL, userId, Constants.FIELD_CHATS_QUEUE, requestId);
            updateMap.put(approvalRef, Constants.CHAT_REQUEST_STATUS_REJECTED);
            updateMap.put(requestRef, null);
            chatsRef.updateChildren(updateMap).addOnCompleteListener(liveData::setValue);
        }
        return liveData;
    }

    public LiveData<Task<Void>> cancelChatRequest(String contactId, String requestId) {
        MutableLiveData<Task<Void>> liveData = new MutableLiveData<>();
        String userId = getUid();
        if (userId == null) liveData.setValue(null);
        else {
            DatabaseReference chatsRef = FirebaseUtils.getBaseChatsRef();
            HashMap<String, Object> updateMap = new HashMap<>();
            String approvalRef = FirebaseUtils.makeRef(Constants.FIELD_APPROVAL, requestId, userId);
            String requestRef = FirebaseUtils.makeRef(Constants.FIELD_ALL, contactId, Constants.FIELD_CHATS_QUEUE, requestId);
            updateMap.put(approvalRef, Constants.CHAT_REQUEST_STATUS_REJECTED);
            updateMap.put(requestRef, null);
            chatsRef.updateChildren(updateMap).addOnCompleteListener(liveData::setValue);
        }
        return liveData;
    }

    public LiveData<Task<Void>> uploadStatusMessage(String newStatus) {
        MutableLiveData<Task<Void>> liveData = new MutableLiveData<>();
        String phone = getUserPhoneNumber();
        if (phone == null) liveData.setValue(null);
        else {
            DatabaseReference statusMessageRef = FirebaseUtils.getUserStatusMessageRef(phone);
            statusMessageRef.setValue(newStatus).addOnCompleteListener(liveData::setValue);
        }
        return liveData;
    }

    public String getMessageId(String chatId, String currentChatId) {
        DatabaseReference messageRef = FirebaseUtils.getChatMessagesRef(chatId, currentChatId).push();
        return messageRef.getKey();
    }

    public LiveData<Task<Void>> sendMessage(ChatMessage chatMessage, String currentChatId) {
        MutableLiveData<Task<Void>> liveData = new MutableLiveData<>();
        String chatId = chatMessage.getChatId();
        DatabaseReference newMessageRef = FirebaseUtils.getChatMessageRef(chatId, currentChatId, chatMessage.getPushId());
        newMessageRef.setValue(chatMessage).addOnCompleteListener(liveData::setValue);
        return liveData;
    }

    public LiveData<Task<Void>> endChat(ChatRequest chat) {
        MutableLiveData<Task<Void>> liveData = new MutableLiveData<>();
        String userId = getUid();
        if (userId == null) liveData.setValue(null);
        else {
            DatabaseReference chatsRef = FirebaseUtils.getBaseChatsRef();
            HashMap<String, Object> updateMap = new HashMap<>();
            String ongoingPath = FirebaseUtils.makeRef(Constants.FIELD_ALL, userId,
                    Constants.FIELD_CHATS_ONGOING, chat.getPushId());
            String finishedPath = FirebaseUtils.makeRef(Constants.FIELD_ALL, userId,
                    Constants.FIELD_CHATS_FINISHED, chat.getPushId());
            String presencePath = FirebaseUtils.makeRef(Constants.FIELD_PRESENCE,
                    chat.getPushId(), userId);

            updateMap.put(ongoingPath, null);
            updateMap.put(finishedPath, chat);
            updateMap.put(presencePath, null);

            DatabaseReference userChatPresenceRef = FirebaseUtils.getUserChatPresenceRef(chat.getPushId(), userId);
            userChatPresenceRef.onDisconnect().cancel();
            chatsRef.updateChildren(updateMap).addOnCompleteListener(liveData::setValue);
        }
        return liveData;
    }

    public void trackUserStatus(LiveData<Boolean> chattingPrefLiveData) {
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        ValueEventListener connectedListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    Boolean connected = snapshot.getValue(Boolean.class);
                    if (connected != null && connected) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            String userId = currentUser.getUid();
                            DatabaseReference userPresenceRef = FirebaseUtils.getPresenceRef(userId);
                            userPresenceRef.onDisconnect().setValue(Constants.CONTACT_STATUS_OFFLINE);
                            userPresenceRef.setValue(Constants.CONTACT_STATUS_ONLINE);
                        }
                    }
                } catch (DatabaseException databaseException) {
                    Crashlytics.logException(databaseException);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Crashlytics.logException(error.toException());
            }
        };

        chattingPrefLiveData.observeForever(isChatting -> {
            if (isChatting) connectedRef.removeEventListener(connectedListener);
            else connectedRef.addValueEventListener(connectedListener);
        });
    }

    public LiveData<ChatMessage> fetchChatMessages(String chatId, String currentChatId) {
        MutableLiveData<ChatMessage> liveData = new MutableLiveData<>();
        String userId = getUid();
        if (userId == null) liveData.setValue(null);
        else {
            DatabaseReference messagesRef = FirebaseUtils.getChatMessagesRef(chatId, currentChatId);
            FirebaseChildQueryLiveData messagesRefLiveData = new FirebaseChildQueryLiveData(messagesRef, FirebaseChildQueryLiveData.RECURRING_QUERY);
            LiveData<ChatMessage> messagesLiveData = Transformations.map(messagesRefLiveData, new MessageEntryDeserializer());
            AppExecutors executors = InjectorUtils.provideAppExecutors();
            executors.mainThread().execute(() -> messagesLiveData.observeForever(liveData::setValue));
        }
        return liveData;
    }

    public LiveData<Integer> trackContactChatPresence(String chatId, String contactId) {
        DatabaseReference contactChatPresenceRef = FirebaseUtils.getUserChatPresenceRef(chatId, contactId);
        FirebaseQueryLiveData contactQueryLiveData = new FirebaseQueryLiveData(contactChatPresenceRef, FirebaseQueryLiveData.RECURRING_QUERY);
        return Transformations.map(contactQueryLiveData, new FirebaseIntegerDeserializer());
    }

    public void leaveConversation(String chatId) {
        String userId = getUid();
        if (userId == null) return;
        DatabaseReference userChatPresenceRef = FirebaseUtils.getUserChatPresenceRef(chatId, userId);
        userChatPresenceRef.setValue(Constants.CONVERSATION_STATUS_ABSENT);
    }

    public void resumeConversation(String chatId) {
        String userId = getUid();
        if (userId == null) return;
        DatabaseReference userChatPresenceRef = FirebaseUtils.getUserChatPresenceRef(chatId, userId);
        userChatPresenceRef.setValue(Constants.CONVERSATION_STATUS_ONLINE);
    }

    public Task<List<String>> sendContactsForSync(List<String> contactsToSync) {
        Map<String, Object> data = new HashMap<>();
        data.put("contacts", contactsToSync);

        FirebaseFunctions functions = FirebaseFunctions.getInstance();

        return functions
                .getHttpsCallable("syncContacts")
                .call(data)
                .continueWith(task -> {
                    // This continuation runs on either success or failure, but if the task
                    // has failed then getResult() will throw an Exception which will be
                    // propagated down.
                    if (task.getResult() != null) {
                        HashMap result = (HashMap) task.getResult().getData();
                        if (result != null) {
                            return (List<String>) result.get("syncedContacts");
                        }
                    }
                    return null;
                });

    }

    public MutableLiveData<Boolean> trackChatPresence(String chatId, MutableLiveData<Boolean> connectionLiveData) {
        MutableLiveData<Boolean> lifecycleLiveData = new MutableLiveData<>();
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    Boolean connected = snapshot.getValue(Boolean.class);
                    connectionLiveData.setValue(connected);
                    if (connected != null && connected) {
                        String userId = getUid();
                        if (userId != null) {
                            DatabaseReference userChatPresenceRef = FirebaseUtils.getUserChatPresenceRef(chatId, userId);
                            userChatPresenceRef.onDisconnect().setValue(Constants.CONTACT_STATUS_OFFLINE);
                            userChatPresenceRef.setValue(Constants.CONTACT_STATUS_ONLINE);
                        }
                    }
                } catch (DatabaseException databaseException) {
                    Crashlytics.logException(databaseException);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Crashlytics.logException(error.toException());
            }
        };
        connectedRef.addValueEventListener(valueEventListener);
        AppExecutors executors = InjectorUtils.provideAppExecutors();
        executors.mainThread().execute(() -> lifecycleLiveData.observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean lifecycleStatus) {
                if (lifecycleStatus != null && lifecycleStatus) {
                    connectedRef.removeEventListener(valueEventListener);
                    lifecycleLiveData.removeObserver(this);
                }
            }
        }));
        return lifecycleLiveData;
    }

    public LiveData<Task<HttpsCallableResult>> sendMissedRequestNotification(@NonNull String recipientId) {
        MutableLiveData<Task<HttpsCallableResult>> liveData = new MutableLiveData<>();
        Map<String, Object> data = new HashMap<>();
        data.put("recipientId", recipientId);

        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        functions.getHttpsCallable("sendMissedRequestNotif")
                .call(data).addOnCompleteListener(liveData::setValue);
        return liveData;
    }

    /*-----------------------
    -------------------------
    ---- DESERIALIZERS ------
    -------------------------
    -----------------------*/

    /*private UserEntry createUserFromHashMap(HashMap<String, Object> hashMap) {
        String name = (String) hashMap.get("name");
        String phoneNumber = (String) hashMap.get("phoneNumber");
        String pushId = (String) hashMap.get("pushId");
        return new UserEntry(name, phoneNumber, pushId);
    }*/

    /*private ChatMessage createMessageFromHashMap(HashMap<String, Object> hashMap) {
        String pushId = (String) hashMap.get(Constants.FIELD_PUSH_ID);
        String senderId = (String) hashMap.get(Constants.FIELD_SENDER_ID);
        String receiverId = (String) hashMap.get(Constants.FIELD_RECIPIENT_ID);
        String chatId = (String) hashMap.get(Constants.FIELD_CHAT_ID);
        String message = (String) hashMap.get(Constants.FIELD_MESSAGE);
        long timestamp = (long) hashMap.get(Constants.FIELD_TIMESTAMP);
        int status = (int) ((long) hashMap.get(Constants.FIELD_STATUS));
        return new ChatMessage(pushId, senderId, receiverId, chatId, message, timestamp, status);
    }*/

    public LiveData<String> updateMessagingToken(String token) {
        MutableLiveData<String> liveData = new MutableLiveData<>();
        if (TextUtils.isEmpty(token)) {
            FirebaseInstanceId
                    .getInstance()
                    .getInstanceId()
                    .addOnSuccessListener(success -> liveData.setValue(success.getToken()));
        }
        String userId = getUid();
        if (TextUtils.isEmpty(userId)) liveData.setValue("failure");
        else {
            DatabaseReference tokenRef = FirebaseUtils.getCloudMessagingTokenRef(userId);
            tokenRef.setValue(token)
                    .addOnCompleteListener(result -> liveData.setValue(result.isSuccessful() ? "success" : "failure"));
        }
        return liveData;
    }

    public LiveData<Integer> getScreenshotCount(String contactId, String chatId) {
        MutableLiveData<Integer> liveData = new MutableLiveData<>();
        DatabaseReference screenshotsRef = FirebaseUtils.getScreenshotsRef(contactId, chatId);
        FirebaseQueryLiveData screenshotsQueryLiveData = new FirebaseQueryLiveData(screenshotsRef, FirebaseQueryLiveData.RECURRING_QUERY);
        LiveData<Integer> screenshotsLiveData = Transformations.map(screenshotsQueryLiveData, new FirebaseIntegerDeserializer());
        AppExecutors executors = InjectorUtils.provideAppExecutors();
        executors.mainThread().execute(() -> screenshotsLiveData.observeForever(screenshotCount -> {
            if (screenshotCount != null) liveData.setValue(screenshotCount);
        }));
        return liveData;
    }

    public void sendScreenshotNotification(String chatId, int screenshotCount) {
        String userId = getUid();
        if (userId == null) return;
        DatabaseReference screenshotsRef = FirebaseUtils.getScreenshotsRef(userId, chatId);
        screenshotsRef.setValue(screenshotCount);
    }

    public LiveData<Integer> getTypingNotif(String contactId, String chatId) {
        MutableLiveData<Integer> liveData = new MutableLiveData<>();
        DatabaseReference typingNotifRef = FirebaseUtils.getTypingNotifRef(contactId, chatId);
        FirebaseQueryLiveData typingNotifQueryLiveData = new FirebaseQueryLiveData(typingNotifRef, FirebaseQueryLiveData.RECURRING_QUERY);
        LiveData<Integer> typingNotifLiveData = Transformations.map(typingNotifQueryLiveData, new FirebaseIntegerDeserializer());
        AppExecutors executors = InjectorUtils.provideAppExecutors();
        executors.mainThread().execute(() -> typingNotifLiveData.observeForever(typingNotif -> {
            if (typingNotif != null) liveData.setValue(typingNotif);
        }));
        return liveData;
    }

    public void sendTypingNotif(String chatId, int typingNotif) {
        String userId = getUid();
        if (userId == null) return;
        DatabaseReference typingNotifRef = FirebaseUtils.getTypingNotifRef(userId, chatId);
        typingNotifRef.setValue(typingNotif);
    }

    public LiveData<String> getRtt(String contactId, String chatId) {
        MutableLiveData<String> liveData = new MutableLiveData<>();
        DatabaseReference rttRef = FirebaseUtils.getRttRef(contactId, chatId);
        FirebaseQueryLiveData rttQueryLiveData = new FirebaseQueryLiveData(rttRef, FirebaseQueryLiveData.RECURRING_QUERY);
        LiveData<String> rttLiveData = Transformations.map(rttQueryLiveData, new FirebaseStringDeserializer());
        AppExecutors executors = InjectorUtils.provideAppExecutors();
        executors.mainThread().execute(() -> rttLiveData.observeForever(rtt -> {
            if (rtt != null) liveData.setValue(rtt);
        }));
        return liveData;
    }

    public void sendRtt(String chatId, String rtt) {
        String userId = getUid();
        if (userId == null) return;
        DatabaseReference rttRef = FirebaseUtils.getRttRef(userId, chatId);
        rttRef.setValue(rtt);
    }

    private class MessageEntryDeserializer implements Function<DataSnapshot, ChatMessage> {
        @Override
        public ChatMessage apply(DataSnapshot dataSnapshot) {
            try {
                return dataSnapshot.getValue(ChatMessage.class);
            } catch (DatabaseException databaseException) {
                Crashlytics.logException(databaseException);
                return null;
            }
        }
    }

    private class ChatRequestDeserializer implements Function<DataSnapshot, ChatRequest> {
        @Override
        public ChatRequest apply(DataSnapshot dataSnapshot) {
            try {
                return dataSnapshot.getValue(ChatRequest.class);
            } catch (DatabaseException databaseException) {
                Crashlytics.logException(databaseException);
                return null;
            }
        }
    }

    private class UserEntryDeserializer implements Function<DataSnapshot, UserEntry> {
        @Override
        public UserEntry apply(DataSnapshot dataSnapshot) {
            try {
                return dataSnapshot.getValue(UserEntry.class);
            } catch (DatabaseException databaseException) {
                Crashlytics.logException(databaseException);
                return null;
            }
        }
    }

    private class ChatAcceptanceDeserializer implements Function<DataSnapshot, ChatAcceptance> {
        @Override
        public ChatAcceptance apply(DataSnapshot dataSnapshot) {
            try {
                return dataSnapshot.getValue(ChatAcceptance.class);
            } catch (DatabaseException databaseException) {
                Crashlytics.logException(databaseException);
                return null;
            }
        }
    }

    /*private class FirebaseHashMapDeserializer implements Function<DataSnapshot, HashMap<String, Object>> {
        @Override
        public HashMap<String, Object> apply(DataSnapshot dataSnapshot) {
            return (HashMap<String, Object>) dataSnapshot.getValue();
        }
    }*/

    private class FirebaseStringDeserializer implements Function<DataSnapshot, String> {

        @Override
        public String apply(DataSnapshot input) {
            try {
                return input.getValue(String.class);
            } catch (DatabaseException databaseException) {
                Crashlytics.logException(databaseException);
                return null;
            }
        }
    }

    private class FirebaseIntegerDeserializer implements Function<DataSnapshot, Integer> {

        @Override
        public Integer apply(DataSnapshot input) {
            try {
                return input.getValue(Integer.class);
            } catch (DatabaseException databaseException) {
                Crashlytics.logException(databaseException);
                return null;
            }
        }
    }

    // END OF DESERIALIZERS
}