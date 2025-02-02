package com.example.onlytube;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class CustomSwipeRefresh extends SwipeRefreshLayout {
    private CanChildScrollUpCallback mCanChildScrollUpCallback;
    public CustomSwipeRefresh(@NonNull Context context) {
        super(context);
    }


    public CustomSwipeRefresh(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public interface CanChildScrollUpCallback {
        boolean canSwipeRefreshChildScrollUp();
    }
    public void setCanChildScrollUpCallback(CanChildScrollUpCallback canChildScrollUpCallback) {
        mCanChildScrollUpCallback = canChildScrollUpCallback;
    }
    @Override
    public boolean canChildScrollUp() {
        if (mCanChildScrollUpCallback != null) {
            return mCanChildScrollUpCallback.canSwipeRefreshChildScrollUp();
        }
        return super.canChildScrollUp();
    }
}
