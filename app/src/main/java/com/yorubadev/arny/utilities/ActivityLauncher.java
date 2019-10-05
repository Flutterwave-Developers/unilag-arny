package com.yorubadev.arny.utilities;

import android.content.Context;
import android.content.Intent;

import com.yorubadev.arny.ui.ResumeActivity;
import com.yorubadev.arny.ui.SplashScreenActivity;
import com.yorubadev.arny.ui.authentication.signin.SignInActivity;
import com.yorubadev.arny.ui.main.MainActivity;

public class ActivityLauncher {

    /*public static void launchPermissionsCheck(Context context) {
        boolean isOnboarded = PreferenceUtils.getOnboardingStatus(context);
        Intent intent;
        if (isOnboarded) intent = new Intent(context, PermissionCheckActivity.class);
        else intent = new Intent(context, OnboardingActivity.class);
        context.startActivity(intent);
    }

    public static void launchAcceptChatActivity(Context context, Bundle bundle) {
        Intent mainIntent = new Intent(context, AcceptChatActivity.class);
        mainIntent.putExtras(bundle);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mainIntent);
    }

    public static void launchProfilePicActivity(Activity context, String mContactId, CircleImageView imgProfilePic) {
        Intent intent = new Intent(context, ProfilePicActivity.class);
        intent.putExtra(ProfilePicActivity.EXTRA_USER_ID, mContactId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(context, imgProfilePic, context.getString(R.string.transition_name_shared_profile_pic));
            context.startActivity(intent, options.toBundle());
        } else context.startActivity(intent);
    }

    public static void launchProfileActivity(Context context, String contactId) {
        Intent intent = new Intent(context, ProfileActivity.class);
        if (contactId != null)
            intent.putExtra(ProfileActivity.EXTRA_USER_ID, contactId);
        context.startActivity(intent);
    }

    public static void launchProfileActivityWithActivityOptions(Activity context) {
        Intent intent = new Intent(context, ProfileActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(context).toBundle());
        else context.startActivity(intent);
    }

    public static void launchRequestChatActivity(Context context, String pushId, String chatId) {
        Intent intent = new Intent(context, RequestChatActivity.class);
        intent.putExtra(RequestChatActivity.EXTRA_CONTACT_ID, pushId);
        intent.putExtra(RequestChatActivity.EXTRA_CHAT_ID, chatId);
        context.startActivity(intent);
    }

    public static void launchSettingsActivity(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    public static void launchChatScreenActivity(Context context, Bundle bundle) {
        Intent intent = new Intent(context, ChatScreenActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void launchSignInActivity(Context context) {
        Intent signUpIntent = new Intent(context, SignInActivity.class);
        context.startActivity(signUpIntent);
    }*/

    public static void launchResumeActivity(Context context) {
        Intent homeIntent = new Intent(context, ResumeActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(homeIntent);
    }

    public static void launchMainActivity(Context context) {
        Intent homeIntent = new Intent(context, MainActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(homeIntent);
    }

    public static void launchSplashScreenActivity(Context context) {
        Intent intent = new Intent(context, SplashScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void launchSignInActivity(Context context) {
        Intent signUpIntent = new Intent(context, SignInActivity.class);
        context.startActivity(signUpIntent);
    }
}
