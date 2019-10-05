package com.yorubadev.arny.data.network;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.yorubadev.arny.data.Repository;
import com.yorubadev.arny.utilities.InjectorUtils;
import com.yorubadev.arny.utilities.WorkUtils;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

//    private static final String LOG_TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onNewToken(@NonNull String token) {
        WorkUtils.scheduleMessagingTokenUpdateWork(this, token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            String type = data.get("type");
            if (type != null) {
                Repository repository = InjectorUtils.provideRepository(getApplicationContext());
                /*switch (type) {
                    case "request":
                        String requesterId = data.get(Constants.FIELD_REQUESTER_ID);
                        String requestId = data.get(Constants.FIELD_REQUEST_ID);
                        String durationString = data.get(Constants.FIELD_DURATION);
                        int duration = durationString != null ? Integer.parseInt(durationString) : 30;
                        String requesterPhone = data.get(Constants.FIELD_REQUESTER_PHONE);
                        String chatId = data.get(Constants.FIELD_CHAT_ID);
                        String screenshotPrefString = data.get(Constants.FIELD_SCREENSHOT_PREF);
                        String rttPrefString = data.get(Constants.FIELD_RTT_PREF);
                        boolean screenshotPref = TextUtils.isEmpty(screenshotPrefString) ? true :
                                Boolean.valueOf(screenshotPrefString);
                        // rtt pref should be false when empty, because if it's empty, then it means
                        // the user sending this request hasn't upgraded to the Veboe version that
                        // supports Rtt, which means we shouldn't initialize Rtt for them at all.
                        boolean rttPref = TextUtils.isEmpty(rttPrefString) ? false :
                                Boolean.valueOf(rttPrefString);

                        String recipientId = repository.getFirebaseUid();
                        if (recipientId != null && requestId != null) {
                            ChatRequest chatRequest = new ChatRequest(requestId, chatId, requesterId,
                                    recipientId, duration, requesterPhone, screenshotPref, rttPref);

                            boolean isMainActivityInForeground = PreferenceUtils.getMainActivityForegroundStatus(this);

                            if (!isMainActivityInForeground) {
                                AppExecutors executors = InjectorUtils.provideAppExecutors();
                                executors.mainThread().execute(() -> {
                                    LiveData<String> contactNameLiveData = repository.getContactName(requesterId);
                                    contactNameLiveData.observeForever(new Observer<String>() {
                                        @Override
                                        public void onChanged(@Nullable String name) {
                                            NotificationUtils.showRequestNotification(MyFirebaseMessagingService.this, chatRequest, name == null ? requesterPhone : name);
                                            // NotificationCompat.Builder.setTimeoutAfter only works on Oreo+
                                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
                                                new Handler().postDelayed(() -> NotificationUtils.dismissNotification(MyFirebaseMessagingService.this, chatRequest.getDuration()), 20000);
                                            contactNameLiveData.removeObserver(this);
                                        }
                                    });
                                });
                            } else {
                                Bundle bundle = new Bundle();
                                bundle.putString(Constants.FIELD_REQUEST_ID, chatRequest.getPushId());
                                bundle.putString(Constants.FIELD_REQUESTER_ID, chatRequest.getRequesterId());
                                bundle.putString(Constants.FIELD_RECIPIENT_ID, chatRequest.getRecipientId());
                                bundle.putString(Constants.FIELD_REQUESTER_PHONE, chatRequest.getRequesterPhone());
                                bundle.putString(Constants.FIELD_CHAT_ID, chatRequest.getChatId());
                                bundle.putInt(Constants.FIELD_DURATION, chatRequest.getDuration());
                                bundle.putBoolean(Constants.FIELD_SCREENSHOT_PREF, chatRequest.getScreenshotPref());
                                bundle.putBoolean(Constants.FIELD_RTT_PREF, chatRequest.getRttPref());
                                ActivityLauncher.launchAcceptChatActivity(this, bundle);
                            }
                        }
                        break;
                    case "newContact":
                        WorkUtils.scheduleContactResolveWork(this);
                        break;
                    case "missedRequest":
                        String senderId = data.get(Constants.FIELD_SENDER_ID);
                        if (!TextUtils.isEmpty(senderId)) {

                            AppExecutors executors = InjectorUtils.provideAppExecutors();
                            executors.mainThread().execute(() -> {
                                LiveData<Integer> missedRequestCountLiveData = repository.getTotalMissedRequestCount(senderId);
                                missedRequestCountLiveData.observeForever(new Observer<Integer>() {
                                    @Override
                                    public void onChanged(Integer missedRequestCount) {
                                        if (missedRequestCount != null)
                                            repository.updateMissedRequestCount(senderId, missedRequestCount + 1);

                                        repository.updateLastChatTimestamp(senderId, System.currentTimeMillis());
                                        WorkUtils.scheduleMissedRequestNotificationWork(MyFirebaseMessagingService.this, senderId);
                                        LiveData<Integer> totalMissedRequestCountLiveData = repository.getTotalMissedRequestCount();
                                        totalMissedRequestCountLiveData.observeForever(new Observer<Integer>() {
                                            @Override
                                            public void onChanged(@Nullable Integer totalMissedRequestCount) {
                                                if (totalMissedRequestCount != null)
                                                    ShortcutBadger.applyCount(getApplicationContext(), totalMissedRequestCount);
                                                totalMissedRequestCountLiveData.removeObserver(this);
                                            }
                                        });

                                        missedRequestCountLiveData.removeObserver(this);
                                    }
                                });
                            });
                        }
                        break;
                    case "profilePictureUpdate":
                        String contactId = data.get(Constants.FIELD_CONTACT_ID);
                        if (!TextUtils.isEmpty(contactId))
                            ImageUtils.fetchProfilePic(getApplicationContext(), contactId);
                        break;
                    case "statusMessageUpdate":
                        String newStatus = data.get(Constants.FIELD_STATUS_MESSAGE);
                        String contactUid = data.get(Constants.FIELD_CONTACT_ID);
                        if (!TextUtils.isEmpty(contactUid)) {
                            repository.updateStatusMessage(contactUid, newStatus);
                        }
                }*/
            }
        }
    }
}
