package com.yorubadev.arny.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.yorubadev.arny.R;
import com.yorubadev.arny.data.Repository;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {

    public static Bitmap getUserProfilePic(Context context) {
        Repository repository = InjectorUtils.provideRepository(context);
        String userId = repository.getFirebaseUid();
        if (userId == null) return null;
        return getProfilePic(context, userId);
    }

    public static Bitmap getProfilePic(Context context, @NonNull String userId) {
        File profilePicDir = context.getDir(Constants.PATH_PROFILE_PIC, Context.MODE_PRIVATE);
        File profilePicFile = new File(profilePicDir, userId + Constants.PATH_PROFILE_PICS_EXTENSION);
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(profilePicFile));
            return bitmap;
        } catch (FileNotFoundException e) {
            Crashlytics.logException(e);
            return null;
        } catch (OutOfMemoryError e) {
            Crashlytics.logException(e);
            return null;
        }
    }

    public static Bitmap getUserMainProfilePicThumbnail(Context context) {
        Repository repository = InjectorUtils.provideRepository(context);
        String userId = repository.getFirebaseUid();
        if (userId == null) return null;
        return getContactMainProfilePicThumbnail(context, userId);
    }

    public static Bitmap getContactMainProfilePicThumbnail(Context context, String userId) {
        File profilePicDir = context.getDir(Constants.PATH_PROFILE_PIC, Context.MODE_PRIVATE);
        File profilePicFile = new File(profilePicDir, userId + Constants.PATH_PROFILE_PICS_EXTENSION);
        return getFileThumbnail(profilePicFile, 100, 100);
    }

    public static Bitmap getUserMenuProfilePicThumbnail(Context context) {
        Repository repository = InjectorUtils.provideRepository(context);
        String userId = repository.getFirebaseUid();
        if (userId == null) return null;
        File profilePicDir = context.getDir(Constants.PATH_PROFILE_PIC, Context.MODE_PRIVATE);
        File profilePicFile = new File(profilePicDir, userId + Constants.PATH_PROFILE_PICS_EXTENSION);
        return getFileThumbnail(profilePicFile, 96, 96);
    }

    public static Bitmap getUserListItemProfilePicThumbnail(Context context) {
        Repository repository = InjectorUtils.provideRepository(context);
        String userId = repository.getFirebaseUid();
        if (userId == null) return null;
        return getContactListItemProfilePicThumbnail(context, userId);
    }

    public static Bitmap getContactListItemProfilePicThumbnail(Context context, @NonNull String userId) {
        File profilePicDir = context.getDir(Constants.PATH_PROFILE_PIC, Context.MODE_PRIVATE);
        File profilePicFile = new File(profilePicDir, userId + Constants.PATH_PROFILE_PICS_EXTENSION);
        return getFileThumbnail(profilePicFile, 60, 60);
    }

    private static Bitmap getFileThumbnail(File imageFile, int width, int height) {
        try {
            return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeStream(new FileInputStream(imageFile)),
                    width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        } catch (FileNotFoundException e) {
            Crashlytics.logException(e);
            return null;
        } catch (OutOfMemoryError e) {
            Crashlytics.logException(e);
            return null;
        }
    }

    private static Bitmap getBitmapThumbnail(Bitmap bitmap, int width, int height) {
        return ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
    }

    public static Bitmap getDefaultContactListProfilePic(Context context) {
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.default_profile_pic);
    }

    public static Bitmap getDefaultContactListProfilePicThumbnail(Context context) {
        return getBitmapThumbnail(getDefaultContactListProfilePic(context), 60, 60);
    }

    public static Bitmap getDefaultUserProfilePic(Context context) {
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.default_profile_pic);
    }

    public static Bitmap getDefaultUserProfilePicThumbnail(Context context) {
        return getBitmapThumbnail(getDefaultUserProfilePic(context), 96, 96);
    }

    public static void fetchUserProfilePic(Context context) {
        Repository repository = InjectorUtils.provideRepository(context);
        String userId = repository.getFirebaseUid();
        if (userId != null) fetchProfilePic(context, userId);
    }

    public static void fetchProfilePic(Context context, String userId) {
        Repository repository = InjectorUtils.provideRepository(context);
        File profilePicDir = context.getDir(Constants.PATH_PROFILE_PIC, Context.MODE_PRIVATE);
        File profilePicFile = new File(profilePicDir, userId + Constants.PATH_PROFILE_PICS_EXTENSION);
        repository.fetchImageFromFirebase(profilePicFile, userId);
    }

    public static void saveImageToStorage(Context context, String userId, Uri imageUri) {
        File profilePicDir = context.getDir(Constants.PATH_PROFILE_PIC, Context.MODE_PRIVATE);
        File profilePicFile = new File(profilePicDir, userId + Constants.PATH_PROFILE_PICS_EXTENSION);
        FileOutputStream fileOutputStream;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] byteArray = baos.toByteArray();
            fileOutputStream = new FileOutputStream(profilePicFile);
            fileOutputStream.write(byteArray);
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            Crashlytics.logException(e);
        } catch (OutOfMemoryError e) {
            Crashlytics.logException(e);
        }
    }

    /*public static void loadProfilePic(Context context, String userId, ImageView imageView) {
        File profilePicDir = context.getDir(Constants.PATH_PROFILE_PIC, Context.MODE_PRIVATE);
        File profilePicFile = new File(profilePicDir, userId + Constants.PATH_PROFILE_PICS_EXTENSION);
        Glide.with(context)
                .load(profilePicFile)
                .placeholder(R.drawable.default_profile_pic)
                .signature(new ObjectKey(profilePicFile.lastModified()))
                .into(imageView);
    }*/
}
