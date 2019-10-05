package com.yorubadev.arny.utilities;

import android.content.Context;

import androidx.core.app.NotificationManagerCompat;

public class NotificationUtils {

    /*public static void showRequestNotification(Context context, ChatRequest chatRequest, String requesterName) {
        createRequestNotificationChannel(context);

        // Create an explicit intent for an Activity in your app
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mainIntent.putExtra(MainActivity.EXTRA_INTENT_ACTION, MainActivity.EXTRA_INTENT_ACTION_SHOW_REQUEST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.FIELD_REQUEST_ID, chatRequest.getPushId());
        bundle.putString(Constants.FIELD_REQUESTER_ID, chatRequest.getRequesterId());
        bundle.putString(Constants.FIELD_RECIPIENT_ID, chatRequest.getRecipientId());
        bundle.putString(Constants.FIELD_REQUESTER_PHONE, chatRequest.getRequesterPhone());
        bundle.putString(Constants.FIELD_CHAT_ID, chatRequest.getChatId());
        bundle.putInt(Constants.FIELD_DURATION, chatRequest.getDuration());
        bundle.putBoolean(Constants.FIELD_SCREENSHOT_PREF, chatRequest.getScreenshotPref());
        bundle.putBoolean(Constants.FIELD_RTT_PREF, chatRequest.getRttPref());
        mainIntent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent rejectIntent = new Intent(context, ChatRequestRejectionService.class);
        rejectIntent.putExtra(ChatRequestRejectionService.EXTRA_REQUEST_ID, chatRequest.getPushId());
        rejectIntent.putExtra(ChatRequestRejectionService.EXTRA_REQUEST_DURATION, chatRequest.getDuration());
        PendingIntent rejectPendingIntent = PendingIntent.getService(context, 1, rejectIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent acceptIntent = new Intent(context, MainActivity.class);
        acceptIntent.putExtra(MainActivity.EXTRA_INTENT_ACTION, MainActivity.EXTRA_INTENT_ACTION_ACCEPT_REQUEST);
        acceptIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        acceptIntent.putExtras(bundle);
        PendingIntent acceptPendingIntent = PendingIntent.getActivity(context, 2, acceptIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Uri notification = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.request);
        String notificationText = requesterName == null ?
                context.getString(R.string.notification_text_default) :
                context.getString(R.string.notification_new_chat_request_temp, requesterName);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, context.getString(R.string.notification_request_channel_id))
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(context.getString(R.string.notification_new_chat_request_header))
                .setContentText(notificationText)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationText))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setSound(notification)
                .setVibrate(new long[]{0, 1000, 500, 1000, 500, 1000, 500, 1000, 500, 1000})
                .setContentIntent(pendingIntent)
                .setTimeoutAfter(20000)
                .addAction(R.drawable.thumb_up_icon, context.getString(R.string.button_accept),
                        acceptPendingIntent)
                .addAction(R.drawable.thumb_down_icon, context.getString(R.string.button_reject),
                        rejectPendingIntent)
                .setAutoCancel(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            notificationBuilder.setFullScreenIntent(pendingIntent, true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(chatRequest.getDuration(), notificationBuilder.build());

        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        if (powerManager != null) {
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                    context.getString(R.string.tag_request_notif_wakelock_tag));
            wakeLock.acquire(1000);
        }
    }

    public static void createRequestNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = context.getString(R.string.notification_request_channel_name);
            String channelDescription = context.getString(R.string.notification_request_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(context.getString(R.string.notification_request_channel_id), channelName, importance);
            channel.setDescription(channelDescription);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 1000, 500, 1000, 500, 1000, 500, 1000, 500, 1000});
            Uri notification = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.request);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION).build();
            channel.setSound(notification, audioAttributes);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) notificationManager.createNotificationChannel(channel);
        }
    }*/


    public static void dismissAllNotifications(Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancelAll();
    }

    /*public static void showMissedRequestNotification(Context context, @NonNull String name, int missedRequestCount, int notificationId) {
        createMissedRequestNotificationChannel(context);

        // Create an explicit intent for an Activity in your app
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, mainIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String notificationText = missedRequestCount == 1 ?
                context.getString(R.string.missed_request_notification_text_single, name)
                : context.getString(R.string.missed_request_notification_text_multiple, missedRequestCount, name);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, context.getString(R.string.notification_missed_request_channel_id))
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(name)
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setSound(notificationSound)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    public static void createMissedRequestNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = context.getString(R.string.notification_missed_request_channel_name);
            String channelDescription = context.getString(R.string.notification_missed_request_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(context.getString(R.string.notification_missed_request_channel_id), channelName, importance);
            channel.setDescription(channelDescription);
            channel.enableVibration(true);
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION).build();
            channel.setSound(notification, audioAttributes);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) notificationManager.createNotificationChannel(channel);
        }
    }*/

    public static void dismissNotification(Context context, int notificationId) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(notificationId);
    }
}