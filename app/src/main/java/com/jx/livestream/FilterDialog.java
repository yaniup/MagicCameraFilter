package com.jx.livestream;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class FilterDialog extends BottomSheetDialog {
    private Context context;

    public FilterDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

}
