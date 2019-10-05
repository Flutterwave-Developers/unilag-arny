package com.yorubadev.arny.data.network;

import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yorubadev.arny.data.database.UserEntry;
import com.yorubadev.arny.utilities.Constants;
import com.yorubadev.arny.utilities.FirebaseUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void sendScreenshotNotification(String chatId, int screenshotCount) {
        String userId = getUid();
        if (userId == null) return;
        DatabaseReference screenshotsRef = FirebaseUtils.getScreenshotsRef(userId, chatId);
        screenshotsRef.setValue(screenshotCount);
    }

    public void sendTypingNotif(String chatId, int typingNotif) {
        String userId = getUid();
        if (userId == null) return;
        DatabaseReference typingNotifRef = FirebaseUtils.getTypingNotifRef(userId, chatId);
        typingNotifRef.setValue(typingNotif);
    }

    public void sendRtt(String chatId, String rtt) {
        String userId = getUid();
        if (userId == null) return;
        DatabaseReference rttRef = FirebaseUtils.getRttRef(userId, chatId);
        rttRef.setValue(rtt);
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