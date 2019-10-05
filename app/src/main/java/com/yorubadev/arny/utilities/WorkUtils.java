package com.yorubadev.arny.utilities;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.yorubadev.arny.data.database.UserEntry;
import com.yorubadev.arny.data.network.MessagingTokenUpdateWorker;
import com.yorubadev.arny.data.network.UserDetailsUpdateWorker;

public class WorkUtils {

    private static final String UNIQUE_WORK_NAME_PERIODIC_CONTACT_RESOLVE = "PeriodicContactResolveWork",
            UNIQUE_WORK_NAME_CONTACT_RESOLVE = "ContactResolveWork",
            UNIQUE_WORK_NAME_PRESENCE_UPDATE = "PresenceUpdateWork",
            UNIQUE_WORK_NAME_USER_DETAILS_UPDATE = "UserDetailsUpdateWork",
            UNIQUE_WORK_NAME_ONGOING_CHATS_CLEAR = "OngoingChatsClearWork",
            UNIQUE_WORK_NAME_MESSAGING_TOKEN_UPDATE = "MessagingTokenUpdateWork";

    /*


    public static void schedulePeriodicPresenceUpdateWork(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest work = new PeriodicWorkRequest.Builder(PresenceUpdateWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(UNIQUE_WORK_NAME_PRESENCE_UPDATE, ExistingPeriodicWorkPolicy.REPLACE, work);
    }

    public static void scheduleChatEndWork(Context context, ChatRequest chat) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        Data data = new Data.Builder()
                .putString(ChatEndWorker.REQUEST_ID, chat.getPushId())
                .putString(ChatEndWorker.CHAT_ID, chat.getChatId())
                .putString(ChatEndWorker.CHAT_REQUESTER_ID, chat.getRequesterId())
                .putString(ChatEndWorker.CHAT_RECIPIENT_ID, chat.getRecipientId())
                .putString(ChatEndWorker.CHAT_REQUESTER_PHONE, chat.getRequesterPhone())
                .putInt(ChatEndWorker.CHAT_DURATION, chat.getDuration())
                .putBoolean(ChatEndWorker.CHAT_SCREENSHOT_PREF, chat.getScreenshotPref())
                .putBoolean(ChatEndWorker.CHAT_RTT_PREF, chat.getRttPref())
                .build();
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(ChatEndWorker.class)
                .setConstraints(constraints)
                .setInputData(data)
                .build();
        WorkManager.getInstance(context).enqueue(work);
    }

    public static void scheduleOngoingChatsClearWork(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(OngoingChatsClearWorker.class)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(context).enqueueUniqueWork(UNIQUE_WORK_NAME_ONGOING_CHATS_CLEAR, ExistingWorkPolicy.REPLACE, work);
    }


    public static void scheduleContactLastChatTimestampUpdateWork(Context context, String contactId) {
        Data inputData = new Data.Builder().
                putString(ContactLastTimestampUpdateWorker.CONTACT_ID, contactId)
                .putLong(ContactLastTimestampUpdateWorker.TIMESTAMP, System.currentTimeMillis())
                .build();
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(ContactLastTimestampUpdateWorker.class)
                .setInputData(inputData)
                .build();
        WorkManager.getInstance(context).enqueue(workRequest);
    }

    public static void scheduleChatRequestCancelWork(Context context, String contactId, String requestId) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        Data data = new Data.Builder()
                .putString(ChatRequestCancelWorker.CHAT_ID, requestId)
                .putString(ChatRequestCancelWorker.CONTACT_ID, contactId)
                .build();
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(ChatRequestCancelWorker.class)
                .setConstraints(constraints)
                .setInputData(data)
                .build();
        WorkManager.getInstance(context).enqueue(work);
    }

    public static void scheduleMissedRequestWork(Context context, String recipientId) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        Data data = new Data.Builder()
                .putString(MissedRequestWorker.RECIPIENT_ID, recipientId)
                .build();
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MissedRequestWorker.class)
                .setConstraints(constraints)
                .setInputData(data)
                .build();
        WorkManager.getInstance(context).enqueue(work);
    }


    public static void scheduleMissedRequestNotificationWork(Context context, String contactId) {
        Data data = new Data.Builder().putString(MissedRequestNotificationWorker.CONTACT_ID, contactId).build();
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MissedRequestNotificationWorker.class)
                .setInputData(data)
                .build();
        WorkManager.getInstance(context).enqueue(work);
    }

    public static void scheduleChatRequestRejectWork(Context context, String requestId, int duration) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        Data data = new Data.Builder()
                .putString(ChatRequestRejectWorker.REQUEST_ID, requestId)
                .putInt(ChatRequestRejectWorker.REQUEST_DURATION, duration)
                .build();
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(ChatRequestRejectWorker.class)
                .setConstraints(constraints)
                .setInputData(data)
                .build();
        WorkManager.getInstance(context).enqueue(work);
    }

    *//*-------- CONTACT RESOLVE --------*//*
    public static void scheduleContactResolveWork(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(ContactResolveWorker.class)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(context).enqueueUniqueWork(UNIQUE_WORK_NAME_CONTACT_RESOLVE, ExistingWorkPolicy.REPLACE, work);
    }

    public static void schedulePeriodicContactResolveWork(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest work = new PeriodicWorkRequest.Builder(ContactResolveWorker.class, 7, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(UNIQUE_WORK_NAME_PERIODIC_CONTACT_RESOLVE, ExistingPeriodicWorkPolicy.REPLACE, work);
    }

    public static void scheduleInternationalizationWork(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(InternationalizationWorker.class)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(context).enqueue(work);
    }

    public static LiveData<List<WorkInfo>> getContactResolveWorkLiveData(Context context) {
        return WorkManager.getInstance(context).
                getWorkInfosForUniqueWorkLiveData(UNIQUE_WORK_NAME_CONTACT_RESOLVE);
    }*/

    public static void scheduleMessagingTokenUpdateWork(Context context) {
        scheduleMessagingTokenUpdateWork(context, null);
    }

    public static void scheduleMessagingTokenUpdateWork(Context context, String token) {
        Data data = new Data.Builder()
                .putString(MessagingTokenUpdateWorker.NEW_TOKEN, token)
                .build();
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MessagingTokenUpdateWorker.class)
                .setInputData(data)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(context).enqueueUniqueWork(UNIQUE_WORK_NAME_MESSAGING_TOKEN_UPDATE, ExistingWorkPolicy.REPLACE, work);
    }

    public static void scheduleUserDetailsUpdateWork(Context context, UserEntry userEntry) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        Data data = new Data.Builder()
                .putString(UserDetailsUpdateWorker.USER_NAME, userEntry.getName())
                .putString(UserDetailsUpdateWorker.USER_PHONE_NUMBER, userEntry.getPhoneNumber())
                .putString(UserDetailsUpdateWorker.USER_UID, userEntry.getUid())
                .build();
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(UserDetailsUpdateWorker.class)
                .setConstraints(constraints)
                .setInputData(data)
                .build();
        WorkManager.getInstance(context).enqueueUniqueWork(UNIQUE_WORK_NAME_USER_DETAILS_UPDATE, ExistingWorkPolicy.REPLACE, work);
    }
}
