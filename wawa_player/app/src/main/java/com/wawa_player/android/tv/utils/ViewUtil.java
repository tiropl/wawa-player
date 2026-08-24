package com.wawa_player.android.tv.utils;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import androidx.core.widget.NestedScrollView;

public class ViewUtil {

    // 向上查找可滚动祖先并滚动，使目标视图出现在可见区域（用于输入法弹出时错误提示不被遮挡）
    public static void scrollToReveal(View target) {
        target.post(() -> reveal(target));
        // 兜底：窗口因输入法调整尺寸的时机可能晚于首次滚动，延迟再执行一次
        target.postDelayed(() -> reveal(target), 300);
    }

    // 将弹窗内的滚动视图高度约束在当前窗口内（预留标题、按钮等区域），
    // 使内容在自身内部滚动，不依赖 AlertDialog 自带滚动容器的行为
    public static void constrainToWindow(View scrollRoot, int reservedPx) {
        scrollRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                constrain(scrollRoot, reservedPx);
            }
        });
        scrollRoot.post(() -> constrain(scrollRoot, reservedPx));
    }

    private static void reveal(View target) {
        // 标准机制：请求所有可滚动的祖先容器将目标滚入可见区域
        target.requestRectangleOnScreen(new Rect(0, 0, target.getWidth(), target.getHeight()), true);
        // 再从内到外对每个滚动容器按坐标直接滚动（只向下滚，避免把已可见内容滚出屏幕）
        ViewParent parent = target.getParent();
        while (parent instanceof View view) {
            if ((view instanceof NestedScrollView || view instanceof ScrollView) && scrollInto(view, target)) return;
            parent = view.getParent();
        }
    }

    private static boolean scrollInto(View scroller, View target) {
        Rect rect = new Rect();
        target.getDrawingRect(rect);
        ((ViewGroup) scroller).offsetDescendantRectToMyCoords(target, rect);
        int y = Math.max(0, rect.bottom - scroller.getHeight());
        if (y <= scroller.getScrollY()) return false;
        if (scroller instanceof NestedScrollView view) view.scrollTo(0, y);
        else ((ScrollView) scroller).scrollTo(0, y);
        return true;
    }

    private static void constrain(View scrollRoot, int reservedPx) {
        if (!(scrollRoot instanceof ViewGroup group) || group.getChildCount() == 0) return;
        int windowHeight = scrollRoot.getRootView().getHeight();
        if (windowHeight <= 0) return;
        int contentHeight = group.getChildAt(0).getHeight();
        int maxHeight = Math.max(0, windowHeight - reservedPx);
        ViewGroup.LayoutParams params = scrollRoot.getLayoutParams();
        int height = contentHeight > maxHeight ? maxHeight : ViewGroup.LayoutParams.WRAP_CONTENT;
        if (params == null || params.height == height) return;
        params.height = height;
        scrollRoot.setLayoutParams(params);
    }
}
