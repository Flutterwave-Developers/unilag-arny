package com.yorubadev.arny.utilities;

@SuppressWarnings("HardCodedStringLiteral")
public class Constants {

    public static final int GENDER_MALE = 1;
    public static final int GENDER_FEMALE = 2;
    public static final int MESSAGE_STATUS_UNSENT = 0;
    public static final int MESSAGE_STATUS_SENT = 1;
    public static final int MESSAGE_STATUS_READ = 2;
    public static final int CONTACT_STATUS_OFFLINE = 0;
    public static final int CONTACT_STATUS_ONLINE = 1;
    public static final int CONTACT_STATUS_ACTIVE = 2;

    public static final int CONVERSATION_STATUS_OFFLINE = 0;
    public static final int CONVERSATION_STATUS_ONLINE = 1;
    public static final int CONVERSATION_STATUS_ABSENT = 2;
    public static final int CONVERSATION_STATUS_LEFT = 3;


    public static final String FIELD_CHAT_ID = "chatId";
    public static final String FIELD_LAST_MESSAGE_ID = "lastMessageId";
    public static final String FIELD_LAST_MESSAGE = "lastMessage";
    public static final String FIELD_CONTACT_ID = "contactId";
    public static final String FIELD_CONTACT_NAME = "contactName";
    public static final String FIELD_USERS = "users";
    public static final String FIELD_CHAT_IDS = "chatIds";
    public static final String FIELD_MESSAGES = "messages";
    public static final String FIELD_PRESENCE = "presence";

    public static final int CHAT_REQUEST_STATUS_REJECTED = -1;
    public static final int CHAT_REQUEST_STATUS_PENDING = 0;
    public static final int CHAT_REQUEST_STATUS_ACCEPTED = 1;

    public static final String FIELD_CHATS = "chats";
    public static final String FIELD_CHATS_QUEUE = "queue";
    public static final String FIELD_CHATS_ONGOING = "ongoing";
    public static final String FIELD_CHATS_FINISHED = "finished";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_DURATION = "duration";
    public static final String FIELD_PUSH_ID = "pushId";
    public static final String FIELD_REQUESTER_ID = "requesterId";
    public static final String FIELD_RECIPIENT_ID = "recipientId";
    public static final String FIELD_REQUESTER_PHONE = "requesterPhone";
    public static final String FIELD_REQUEST_DURATION_INIT = "request_duration_init";
    public static final String FIELD_CHAT_PRESENCE = "chat_presence";
    public static final String FIELD_IDS = "ids";
    public static final String FIELD_ALL = "all";
    public static final String FIELD_SENDER_ID = "senderId";
    public static final String FIELD_MESSAGE = "message";
    public static final String FIELD_TIMESTAMP = "timestamp";
    public static final String FIELD_APPROVAL = "approval";
    public static final String FIELD_REQUEST_ID = "requestId";
    public static final String FIELD_PHONE = "phone";
    public static final String FIELD_SCREENSHOT_PREF = "screenshotPref";
    public static final String FIELD_STATUS_MESSAGE = "statusMessage";
    public static final String PATH_PROFILE_PIC = "profile_pic";
    public static final String PATH_PROFILE_PICS_EXTENSION = ".jpg";
    public static final String FIELD_TYPING_NOTIF = "typingNotif";

    public static final int TYPING_NOTIF_TYPING = 1;
    public static final int TYPING_NOTIF_NOT_TYPING = 0;
    public static final String FIELD_SCREENSHOTS = "screenshots";
    public static final String FIELD_MESSAGING_TOKENS = "messagingTokens";
    public static final String FIELD_RTT = "rtt";
    public static final String FIELD_RTT_PREF = "rttPref";


    /*----------------------------------------------------------------*/
    public static final String RIDE_SERVICE_BOLT = "rs_bolt";
    public static final String RIDE_SERVICE_UBER = "rs_uber";
    public static final String RIDE_SERVICE_OTHERS = "rs_others";
}
