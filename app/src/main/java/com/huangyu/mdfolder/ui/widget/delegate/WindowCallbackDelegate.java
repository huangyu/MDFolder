package com.huangyu.mdfolder.ui.widget.delegate;

import android.os.Build;
import android.support.annotation.Nullable;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by huangyu on 2017/7/31.
 * 屏蔽点击返回键ActionMode会给finish的问题，自行查阅源码解决
 */

public class WindowCallbackDelegate implements Window.Callback {

    private Window.Callback mOriginalWindowCallback;
    private Window.Callback mWindowDelegate;

    public WindowCallbackDelegate(Window.Callback originalWindowCallback, Window.Callback windowDelegate) {
        this.mOriginalWindowCallback = originalWindowCallback;
        this.mWindowDelegate = windowDelegate;
    }

    public Window.Callback getOriginalWindowCallback() {
        return mOriginalWindowCallback;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return mWindowDelegate.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        return mOriginalWindowCallback.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return mOriginalWindowCallback.dispatchTouchEvent(event);
    }

    @Override
    public boolean dispatchTrackballEvent(MotionEvent event) {
        return mOriginalWindowCallback.dispatchTrackballEvent(event);
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        return mOriginalWindowCallback.dispatchGenericMotionEvent(event);
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return mOriginalWindowCallback.dispatchPopulateAccessibilityEvent(event);
    }

    @Nullable
    @Override
    public View onCreatePanelView(int featureId) {
        return mOriginalWindowCallback.onCreatePanelView(featureId);
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        return mOriginalWindowCallback.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        return mOriginalWindowCallback.onPreparePanel(featureId, view, menu);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return mOriginalWindowCallback.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        return mOriginalWindowCallback.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams attrs) {
        mOriginalWindowCallback.onWindowAttributesChanged(attrs);
    }

    @Override
    public void onContentChanged() {
        mOriginalWindowCallback.onContentChanged();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        mOriginalWindowCallback.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onAttachedToWindow() {
        mOriginalWindowCallback.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        mOriginalWindowCallback.onDetachedFromWindow();
    }

    @Override
    public void onPanelClosed(int featureId, Menu menu) {
        mOriginalWindowCallback.onPanelClosed(featureId, menu);
    }

    @Override
    public boolean onSearchRequested() {
        return mOriginalWindowCallback.onSearchRequested();
    }

    @Override
    public boolean onSearchRequested(SearchEvent searchEvent) {
        return mOriginalWindowCallback.onSearchRequested(searchEvent);
    }

    @Nullable
    @Override
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
        return mOriginalWindowCallback.onWindowStartingActionMode(callback);
    }

    @Nullable
    @Override
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int type) {
        if (Build.VERSION.SDK_INT > 23) {
            return mOriginalWindowCallback.onWindowStartingActionMode(callback, type);
        } else {
            return null;
        }
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        mOriginalWindowCallback.onActionModeStarted(mode);
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        mOriginalWindowCallback.onActionModeStarted(mode);
    }

}
