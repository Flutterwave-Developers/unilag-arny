package com.yorubadev.arny.utilities;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.Arrays;

public class InputUtils {

    private ArrayList<View> mViews;

    private InputUtils(View... inputFields) {
        if (mViews == null) mViews = new ArrayList<>();
        mViews.addAll(Arrays.asList(inputFields));
    }

    public static InputUtils init(View... inputFields) {
        return new InputUtils(inputFields);
    }

    public void enableInput() {
        for (View view : mViews) {
            if (view instanceof ProgressBar) view.setVisibility(View.GONE);
            else if (view instanceof CountryCodePicker)
                ((CountryCodePicker) view).setCcpClickable(true);
            else view.setEnabled(true);
            if (view instanceof Button) view.setAlpha(1f);
        }
    }

    public void disableInput() {
        for (View view : mViews) {
            if (view instanceof ProgressBar) view.setVisibility(View.VISIBLE);
            else if (view instanceof CountryCodePicker)
                ((CountryCodePicker) view).setCcpClickable(false);
            else view.setEnabled(false);
            if (view instanceof Button) view.setAlpha(0.8f);
        }
    }

}
