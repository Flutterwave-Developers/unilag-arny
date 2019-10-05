package com.yorubadev.arny.utilities;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.hbb20.CCPCountry;
import com.hbb20.CountryCodePicker;

import java.util.Locale;

public class PhoneNumberUtils {

//    private static final String LOG_TAG = PhoneNumberUtils.class.getSimpleName();

    public static String formatToInternational(Context context, String phoneNumberToFormat) {
        if (TextUtils.isEmpty(phoneNumberToFormat)) return null;
        String trimmedInput = phoneNumberToFormat.trim();
        boolean isInternational = trimmedInput.charAt(0) == '+';
        String barePhoneNumber = prepareForInternationalization(trimmedInput);
        if (TextUtils.isEmpty(barePhoneNumber)) return null;
        if (isInternational) return "+" + barePhoneNumber;
        CountryCodePicker.Language language = autoDetectLanguage(context);
        String countryNameCode = autoDetectCountryNameCode(context);
        if (TextUtils.isEmpty(countryNameCode)) return null;
        CCPCountry ccpCountry = CCPCountry.getCountryForNameCodeFromLibraryMasterList(context, language, autoDetectCountryNameCode(context));
        return String.format("+%s%s", ccpCountry.getPhoneCode(), barePhoneNumber);
    }

    private static String prepareForInternationalization(String trimmedInput) {
//        String phoneNumberWithoutParentheses = removeParentheses(trimmedInput);
        String numericPhoneNumber = makeNumeric(trimmedInput);
        return removeLeadingZeros(numericPhoneNumber);
    }

    private static String removeLeadingZeros(String numericPhoneNumber) {
        if (TextUtils.isEmpty(numericPhoneNumber)) return null;
        if (numericPhoneNumber.charAt(0) != '0') return numericPhoneNumber;
        String trimmed = numericPhoneNumber.substring(1);
        return removeLeadingZeros(trimmed);
    }

    private static String makeNumeric(String phoneNumberWithoutParentheses) {
        return phoneNumberWithoutParentheses.replaceAll("[^0-9]", "");
    }

    /**
     * Wrong implementation of method
     */
    /*private static String removeParentheses(String phoneNumberToFormat) {
        return phoneNumberToFormat.replaceAll("\\([0-9]+?\\)", "");
    }*/

    private static CountryCodePicker.Language autoDetectLanguage(Context context) {
        CountryCodePicker.Language language = getCCPLanguageFromLocale(context);
        return language != null ? language : CountryCodePicker.Language.ENGLISH;
    }

    private static CountryCodePicker.Language getCCPLanguageFromLocale(Context context) {
        Locale currentLocale = context.getResources().getConfiguration().locale;
        for (CountryCodePicker.Language language : CountryCodePicker.Language.values()) {
            if (language.getCode().equalsIgnoreCase(currentLocale.getLanguage())) {

                if (language.getCountry() == null
                        || language.getCountry().equalsIgnoreCase(currentLocale.getCountry()))
                    return language;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (language.getScript() == null
                            || language.getScript().equalsIgnoreCase(currentLocale.getScript()))
                        return language;

                }
            }
        }
        return null;
    }

    private static String autoDetectCountryNameCode(Context context) {
        String simCountryNameCode = autoDetectSIMCountryNameCode(context);
        if (!TextUtils.isEmpty(simCountryNameCode)) return simCountryNameCode;
        else {
            String networkCountryNameCode = autoDetectNetworkCountryNameCode(context);
            if (!TextUtils.isEmpty(networkCountryNameCode)) return networkCountryNameCode;
            else return autoDetectLocaleCountryNameCode(context);
        }
    }

    private static String autoDetectSIMCountryNameCode(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) return null;
        return telephonyManager.getSimCountryIso();
    }

    private static String autoDetectNetworkCountryNameCode(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) return null;
        return telephonyManager.getNetworkCountryIso();
    }

    private static String autoDetectLocaleCountryNameCode(Context context) {
        return context.getResources().getConfiguration().locale.getCountry();
    }

    public static boolean isInternationalized(String phoneNumber) {
        return phoneNumber.charAt(0) == '+';
    }

    public static String removeInternationalPlusSign(@NonNull String internationalNumber) {
        return internationalNumber.replace("+", "");
    }
}
