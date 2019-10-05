package com.yorubadev.arny.utilities;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseUtils {

    private static DatabaseReference getBaseDatabaseRef() {
        return FirebaseDatabase.getInstance().getReference();
    }


    /*----------------------
    ------- USERS ----------
    ----------------------*/

    public static DatabaseReference getBaseUsersRef() {
        return getBaseDatabaseRef().child(Constants.FIELD_USERS);
    }

    public static DatabaseReference getUserRef(@NonNull String phoneNumber) {
        return getBaseUsersRef().child(phoneNumber);
    }

    public static DatabaseReference getUserStatusMessageRef(String phoneNumber) {
        return getUserRef(phoneNumber).child(Constants.FIELD_STATUS_MESSAGE);
    }

    // END OF USERS


    /*----------------------
    ------ PRESENCE --------
    ----------------------*/

    private static DatabaseReference getBasePresenceRef() {
        return getBaseDatabaseRef().child(Constants.FIELD_PRESENCE);
    }

    public static DatabaseReference getPresenceRef(@NonNull String userId) {
        return getBasePresenceRef().child(userId);
    }

    // END OF PRESENCE


    /*----------------------
    ------- CHATS ----------
    ----------------------*/
    public static DatabaseReference getBaseChatsRef() {
        return getBaseDatabaseRef().child(Constants.FIELD_CHATS);
    }

    private static DatabaseReference getBaseChatPresenceRef() {
        return getBaseChatsRef().child(Constants.FIELD_PRESENCE);
    }

    public static DatabaseReference getBaseChatIdsRef() {
        return getBaseChatsRef().child(Constants.FIELD_IDS);
    }

    public static DatabaseReference getBaseAllChatsRef() {
        return getBaseChatsRef().child(Constants.FIELD_ALL);
    }

    private static DatabaseReference getBaseChatsApprovalRef() {
        return getBaseChatsRef().child(Constants.FIELD_APPROVAL);
    }

    private static DatabaseReference getChatApprovalRef(@NonNull String chatId) {
        return getBaseChatsApprovalRef().child(chatId);
    }

    public static DatabaseReference getUserChatApprovalRef(@NonNull String chatId, @NonNull String userId) {
        return getChatApprovalRef(chatId).child(userId);
    }

    private static DatabaseReference getChatPresenceRef(@NonNull String chatId) {
        return getBaseChatPresenceRef().child(chatId);
    }

    public static DatabaseReference getUserChatPresenceRef(@NonNull String chatId, @NonNull String userId) {
        return getChatPresenceRef(chatId).child(userId);
    }

    private static DatabaseReference getChatsRef(@NonNull String userId) {
        return getBaseAllChatsRef().child(userId);
    }

    public static DatabaseReference getQueuedChatsRef(@NonNull String userId) {
        return getChatsRef(userId).child(Constants.FIELD_CHATS_QUEUE);
    }

    public static DatabaseReference getQueuedChatRef(@NonNull String userId, @NonNull String chatId) {
        return getQueuedChatsRef(userId).child(chatId);
    }

    public static DatabaseReference getOngoingChatsRef(@NonNull String userId) {
        return getChatsRef(userId).child(Constants.FIELD_CHATS_ONGOING);
    }

    private static DatabaseReference getOngoingChatRef(@NonNull String userId, @NonNull String chatId) {
        return getOngoingChatsRef(userId).child(chatId);
    }

    public static DatabaseReference getScreenshotsRef(@NonNull String userId, @NonNull String chatId) {
        return getOngoingChatRef(userId, chatId).child(Constants.FIELD_SCREENSHOTS);
    }

    public static DatabaseReference getRttRef(@NonNull String userId, @NonNull String chatId) {
        return getOngoingChatRef(userId, chatId).child(Constants.FIELD_RTT);
    }

    /*private static DatabaseReference getFinishedChatsRef(@NonNull String userId) {
        return getChatsRef(userId).child(Constants.FIELD_CHATS_FINISHED);
    }*/

    /*public static DatabaseReference getFinishedChatRef(@NonNull String userId, @NonNull String chatId) {
        return getFinishedChatsRef(userId).child(chatId);
    }*/

    private static DatabaseReference getChatIdsRef(@NonNull String userId) {
        return getBaseChatIdsRef().child(userId);
    }

    public static DatabaseReference getContactChatIdRef(@NonNull String userId, @NonNull String contactId) {
        return getChatIdsRef(userId).child(contactId);
    }

    public static DatabaseReference getTypingNotifRef(@NonNull String userId, @NonNull String chatId) {
        return getOngoingChatRef(userId, chatId).child(Constants.FIELD_TYPING_NOTIF);
    }

    // END OF PRESENCE


    /*----------------------
    ------ MESSAGES --------
    ----------------------*/

    private static DatabaseReference getBaseMessagesRef() {
        return getBaseDatabaseRef().child(Constants.FIELD_MESSAGES);
    }

    private static DatabaseReference getMessagesRef(@NonNull String chatId) {
        return getBaseMessagesRef().child(chatId);
    }

    public static DatabaseReference getChatMessagesRef(@NonNull String chatId, @NonNull String currentChatId) {
        return getMessagesRef(chatId).child(currentChatId);
    }

    public static DatabaseReference getChatMessageRef(@NonNull String chatId, @NonNull String currentChatId, @NonNull String messageId) {
        return getChatMessagesRef(chatId, currentChatId).child(messageId);
    }

    // END OF MESSAGES



    /*-------------------------
    ---------- STORAGE --------
    -------------------------*/

    public static StorageReference getBaseStorageRef() {
        return FirebaseStorage.getInstance().getReference();
    }

    public static StorageReference getProfilePicsStorageRef() {
        return getBaseStorageRef().child("profile_pictures");
    }

    public static StorageReference getStorageProfilePicRef(String userId) {
        return getProfilePicsStorageRef().child(userId + ".jpg");
    }

    // END OF STORAGE


    /*-----------------------
    ---- CLOUD MESSAGING ----
    -----------------------*/

    private static DatabaseReference getBaseCloudMessagingTokensRef() {
        return getBaseDatabaseRef()
                .child(Constants.FIELD_MESSAGING_TOKENS);
    }

    public static DatabaseReference getCloudMessagingTokenRef(@NonNull String uid) {
        return getBaseCloudMessagingTokensRef().child(uid);
    }

    // END OF CLOUD OF MESSAGING


    /*-------------------------
    -------- UTILITIES --------
    -------------------------*/

    public static String makeRef(String... refPaths) {
        StringBuilder stringBuilder = new StringBuilder(1024);
        stringBuilder.append(refPaths[0]);
        for (int i = 1; i < refPaths.length; i++) {
            stringBuilder.append("/").append(refPaths[i]);
        }
        return stringBuilder.toString();
    }

    // END OF UTILITIES
}