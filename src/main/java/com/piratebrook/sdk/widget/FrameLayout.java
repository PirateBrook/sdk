package com.piratebrook.sdk.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.RemoteViews;

import com.piratebrook.sdk.R;

import java.util.ArrayList;

/**
 * Created by wyy on 2017-11-21.
 */
@RemoteViews.RemoteView
public class FrameLayout extends ViewGroup {

    private static final int DEFAULT_CHILD_GRAVITY = Gravity.TOP | Gravity.START;

    @ViewDebug.ExportedProperty(category = "measurement")
    boolean mMeasureAllChildren = false;

    @ViewDebug.ExportedProperty(category = "padding")
    private int mForegroundPaddingLeft = 0;

    @ViewDebug.ExportedProperty(category = "padding")
    private int mForegroundPaddingTop = 0;

    @ViewDebug.ExportedProperty(category = "padding")
    private int mForegroundPaddingRight = 0;

    @ViewDebug.ExportedProperty(category = "padding")
    private int mForegroundPaddingBottom = 0;

    private final ArrayList<View> mMatchParentChildren = new ArrayList<>(1);

    public FrameLayout(@NonNull Context context) {
        super(context);
    }

    public FrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FrameLayout(@NonNull Context context, @Nullable AttributeSet attrs,
                       @AttrRes int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public FrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.FrameLayout, defStyleAttr, defStyleRes);

        if (a.getBoolean(R.styleable.FrameLayout_measureAllChildren, false)) {
            setMeasureAllChildren(true);
        }

        a.recycle();
    }

    /**
     * Describes how the foreground is positioned. Defaults to START to TOP.
     *
     * @param foregroundGravity See {@link android.view..Gravity}
     *
     * @see #getForegroundGravity()
     *
     * @attr ref android.R.styleable#View_foregroundGravity
     */
    @Override
    public void setForegroundGravity(int foregroundGravity) {
        if (getForegroundGravity() != foregroundGravity) {
            super.setForegroundGravity(foregroundGravity);
        }

        // calling get* again here because the set above may apply default constraints
        final Drawable foreground = getForeground();
        if (getForegroundGravity() == Gravity.FILL && foreground != null) {
            Rect padding = new Rect();
            if (foreground.getPadding(padding)) {
                mForegroundPaddingLeft = padding.left;
                mForegroundPaddingTop = padding.top;
                mForegroundPaddingRight = padding.right;
                mForegroundPaddingBottom = padding.bottom;
            }
        } else {
            mForegroundPaddingLeft = 0;
            mForegroundPaddingTop = 0;
            mForegroundPaddingRight = 0;
            mForegroundPaddingBottom = 0;
        }

        requestLayout();
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    int getPaddingLeftWithForeground() {
        return isForegroundInsidePadding() ? Math.max(mPaddingRight, mForegroundPaddingLeft) :
                mPaddingLeft + mForegroundPaddingLeft;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();

        final boolean measureMatchParentChildren =
                MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY ||
                        MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY;
        mMatchParentChildren.clear();

        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (mMeasureAllChildren || child.getVisibility() != GONE) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                final LayoutParams lp = child.getLayoutParams();
                maxWidth = Math.max(maxWidth,
                        child.getMeasuredWidth() + lp.le)
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    /**
     * Per-child layout information for layouts that support margins.
     * See {@link android.R.styleable#FrameLayout_Layout FrameLayout Layout Attributes}
     * for a list of all child view attributes that this class supports.
     *
     * @attr ref android.R.styleable#FrameLayout_Layout_layout_gravity
     */
    public static class LayoutParams extends MarginLayoutParams {
        /**
         * Value for {@link #gravity} indicating that a gravity has not been
         * explicitly specified.
         */
        public static final int UNSPECIFIED_GRAVITY = -1;

        /**
         * The gravity to apply with the View to which these layout parameters
         * are associated.
         * <p>
         * The default value is {@link #UNSPECIFIED_GRAVITY}, which is treated
         * by FrameLayout as {@code Gravity.TOP | Gravity.START}.
         * @see android.view.Gravity
         * @attr ref android.R.styleable#FrameLayout_Layout_layout_gravity
         */
        public int gravity = UNSPECIFIED_GRAVITY;

        public LayoutParams(@NonNull Context c, @Nullable AttributeSet attrs) {
            super(c, attrs);

            final TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.FrameLayout_layout);
            gravity = a.getInt(R.styleable.FrameLayout_layout_layout_gravity, UNSPECIFIED_GRAVITY);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        /**
         * Create a new set of layout parameters with the specified width, height
         * and weight.???
         *
         * @param width the width, either {@link #MATCH_PARENT},
         *              {@link #WRAP_CONTENT} or a fixed size in pixels
         * @param height the height, either {@link #MATCH_PARENT},
         *               {@link #WRAP_CONTENT} or a fixed size in pixels.
         * @param gravity the gravity
         *
         * @see android.view.Gravity
         */
        public LayoutParams(int width, int height, int gravity) {
            super(width, height);
            this.gravity = gravity;
        }

        public LayoutParams(@NonNull ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(@NonNull ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        /**
         * Copy constructor. Clones the width, height, margin values, and
         * gravity of the source.
         *
         * @param source The layout params to copy from.
         */
        public LayoutParams(@NonNull LayoutParams source) {
            super(source);

            this.gravity = source.gravity;
        }
    }
}
