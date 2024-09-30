package com.example.memestorage.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayoutManager;

public class SafeFlexboxLayoutManager extends FlexboxLayoutManager {
    public SafeFlexboxLayoutManager(Context context) {
        super(context);
    }

    public SafeFlexboxLayoutManager(Context context, int flexDirection) {
        super(context, flexDirection);
    }

    public SafeFlexboxLayoutManager(Context context, int flexDirection, int flexWrap) {
        super(context, flexDirection, flexWrap);
    }

    public SafeFlexboxLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new FlexboxLayoutManager.LayoutParams(lp);
    }
}
