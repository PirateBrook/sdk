//package com.piratebrook.sdk.widget;
//
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.graphics.Rect;
//import android.graphics.drawable.Drawable;
//import android.support.annotation.AttrRes;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.util.AttributeSet;
//import android.view.Gravity;
//import android.view.View;
//import android.view.ViewDebug;
//import android.view.ViewGroup;
//import android.widget.RemoteViews;
//
//import com.piratebrook.sdk.R;
//
//import java.util.ArrayList;
//
///**
// * Created by wyy on 2017-11-21.
// */
//@RemoteViews.RemoteView
//public class FrameLayout extends ViewGroup {
//
//    private static final int DEFAULT_CHILD_GRAVITY = Gravity.TOP | Gravity.START;
//
//    @ViewDebug.ExportedProperty(category = "measurement")
//    boolean mMeasureAllChildren = false;
//
//    @ViewDebug.ExportedProperty(category = "padding")
//    private int mForegroundPaddingLeft = 0;
//
//    @ViewDebug.ExportedProperty(category = "padding")
//    private int mForegroundPaddingTop = 0;
//
//    @ViewDebug.ExportedProperty(category = "padding")
//    private int mForegroundPaddingRight = 0;
//
//    @ViewDebug.ExportedProperty(category = "padding")
//    private int mForegroundPaddingBottom = 0;
//
//    private final ArrayList<View> mMatchParentChildren = new ArrayList<>(1);
//
//    private boolean sPreserveMarginParamsInLayoutParamConversion;
//
//    public FrameLayout(@NonNull Context context) {
//        super(context);
//    }
//
//    public FrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public FrameLayout(@NonNull Context context, @Nullable AttributeSet attrs,
//                       @AttrRes int defStyleAttr) {
//        this(context, attrs, defStyleAttr, 0);
//    }
//
//    public FrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//
//        final TypedArray a = context.obtainStyledAttributes(
//                attrs, R.styleable.FrameLayout, defStyleAttr, defStyleRes);
//
//        if (a.getBoolean(R.styleable.FrameLayout_measureAllChildren, false)) {
//            setMeasureAllChildren(true);
//        }
//
//        a.recycle();
//    }
//
//    /**
//     * Describes how the foreground is positioned. Defaults to START to TOP.
//     *
//     * @param foregroundGravity See {@link android.view..Gravity}
//     *
//     * @see #getForegroundGravity()
//     *
//     * @attr ref android.R.styleable#View_foregroundGravity
//     */
//    @Override
//    public void setForegroundGravity(int foregroundGravity) {
//        if (getForegroundGravity() != foregroundGravity) {
//            super.setForegroundGravity(foregroundGravity);
//        }
//
//        // calling get* again here because the set above may apply default constraints
//        final Drawable foreground = getForeground();
//        if (getForegroundGravity() == Gravity.FILL && foreground != null) {
//            Rect padding = new Rect();
//            if (foreground.getPadding(padding)) {
//                mForegroundPaddingLeft = padding.left;
//                mForegroundPaddingTop = padding.top;
//                mForegroundPaddingRight = padding.right;
//                mForegroundPaddingBottom = padding.bottom;
//            }
//        } else {
//            mForegroundPaddingLeft = 0;
//            mForegroundPaddingTop = 0;
//            mForegroundPaddingRight = 0;
//            mForegroundPaddingBottom = 0;
//        }
//
//        requestLayout();
//    }
//
//    @Override
//    protected LayoutParams generateDefaultLayoutParams() {
//        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//    }
//
//    int getPaddingLeftWithForeground() {
//        return isForegroundInsidePadding() ? Math.max(mPaddingLeft, mForegroundPaddingLeft) :
//                mPaddingLeft + mForegroundPaddingLeft;
//    }
//
//    int getPaddingRightWithForeground() {
//        return isForegroundInsidePadding() ? Math.max(mPaddingRight, mForegroundPaddingRight) :
//                mPaddingRight + mForegroundPaddingRight;
//    }
//
//    private int getPaddingTopWithForeground() {
//        return isForegroundInsidePadding() ? Math.max(mPaddingTop, mForegroundPaddingTop) :
//                mPaddingTop + mForegroundPaddingTop;
//    }
//
//    private int getPaddingBottomWithForeground() {
//        return isForegroundInsidePadding() ? Math.max(mPaddingBottom, mForegroundPaddingBottom) :
//                mPaddingBottom + mForegroundPaddingBottom;
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int count = getChildCount();
//
//        final boolean measureMatchParentChildren =
//                MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY ||
//                        MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY;
//        mMatchParentChildren.clear();
//
//        int maxHeight = 0;
//        int maxWidth = 0;
//        int childState = 0;
//
//        for (int i = 0; i < count; i++) {
//            final View child = getChildAt(i);
//            if (mMeasureAllChildren || child.getVisibility() != GONE) {
//                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
//                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
//                maxWidth = Math.max(maxWidth,
//                        child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
//                maxHeight = Math.max(maxHeight,
//                        child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
//                childState = combineMeasuredStates(childState, child.getMeasuredState());
//                if (measureMatchParentChildren) {
//                    if (lp.width == LayoutParams.MATCH_PARENT ||
//                            lp.height == LayoutParams.MATCH_PARENT) {
//                        mMatchParentChildren.add(child);
//                    }
//                }
//            }
//        }
//
//        // Account for padding too
//        maxWidth += getPaddingLeftWithForeground() + getPaddingRightWithForeground();
//        maxHeight += getPaddingTopWithForeground() + getPaddingBottomWithForeground();
//
//        // Check against our minimum height and width
//        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
//        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());
//
//        // Check against our foreground's minimum height and width
//        final Drawable drawable = getForeground();
//        if (drawable != null) {
//            maxHeight = Math.max(maxHeight, drawable.getMinimumHeight());
//            maxWidth = Math.max(maxWidth, drawable.getMinimumWidth());
//        }
//
//        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
//                resolveSizeAndState(maxHeight, heightMeasureSpec,
//                        childState << MEASURED_HEIGHT_STATE_SHIFT));
//
//        count = mMatchParentChildren.size();
//        if (count > 1) {
//            for (int i = 0; i < count; i++) {
//                final View child = mMatchParentChildren.get(i);
//                final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
//
//                final int childWidthMeasureSpec;
//                if (lp.width == LayoutParams.MATCH_PARENT) {
//                    final int width = Math.max(0, getMeasuredWidth()
//                            - getPaddingLeftWithForeground() - getPaddingRightWithForeground()
//                            - lp.leftMargin - lp.rightMargin);
//                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
//                            width, MeasureSpec.EXACTLY);
//                } else {
//                    childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
//                            getPaddingLeftWithForeground() + getPaddingRightWithForeground() +
//                            lp.leftMargin + lp.rightMargin,
//                            lp.width);
//                }
//
//                final int childHeightMeasureSpec;
//                if (lp.height == LayoutParams.MATCH_PARENT) {
//                    final int height = Math.max(0, getMeasuredHeight()
//                            - getPaddingTopWithForeground() + getPaddingBottomWithForeground() +
//                            lp.topMargin + lp.bottomMargin);
//                    childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
//                            height, MeasureSpec.EXACTLY);
//                } else {
//                    childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
//                            getPaddingTopWithForeground() + getPaddingBottomWithForeground() +
//                            lp.topMargin + lp.bottomMargin,
//                            lp.height);
//                }
//
//                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
//            }
//        }
//   }
//
//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        layoutChildren(left, top, right, bottom, false);
//    }
//
//    private void layoutChildren(int left, int top, int right, int bottom, boolean forceLeftGravity) {
//        final int count = getChildCount();
//
//        final int parentLeft = getPaddingLeftWithForeground();
//        final int parentRight = right - left - getPaddingRightWithForeground();
//
//        final int parentTop = getPaddingTopWithForeground();
//        final int parentBottom = bottom - top - getPaddingBottomWithForeground();
//
//        for (int i = 0; i < count; i++) {
//            final View child = getChildAt(i);
//            if (child.getVisibility() != GONE) {
//                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
//
//                final int width = child.getMeasuredWidth();
//                final int height = child.getMeasuredHeight();
//
//                int childLeft;
//                int childTop;
//
//                int gravity = lp.gravity;
//                if (gravity == -1) {
//                    gravity = DEFAULT_CHILD_GRAVITY;
//                }
//
//                final int layoutDirection = getLayoutDirection();
//                final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
//                final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;
//
//                switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
//                    case Gravity.CENTER_HORIZONTAL:
//                        childLeft = parentLeft + (parentRight - parentLeft - width) / 2 +
//                                lp.leftMargin - lp.rightMargin;
//                        break;
//                    case Gravity.RIGHT:
//                        if (!forceLeftGravity) {
//                            childLeft = parentRight - width - lp.rightMargin;
//                            break;
//                        }
//                    case Gravity.LEFT:
//                    default:
//                        childLeft = parentLeft + lp.leftMargin;
//                }
//
//                switch (verticalGravity) {
//                    case Gravity.TOP:
//                        childTop = parentTop + lp.topMargin;
//                        break;
//                    case Gravity.CENTER_VERTICAL:
//                        childTop = parentTop - (parentBottom - parentTop - height) / 2 +
//                                lp.topMargin - lp.bottomMargin;
//                        break;
//                    case Gravity.BOTTOM:
//                        childTop = parentBottom - height - lp.bottomMargin;
//                        break;
//                    default:
//                        childTop = parentTop + lp.topMargin;
//                }
//
//                child.layout(childLeft, childTop, childLeft + width, childTop + height);
//            }
//        }
//    }
//
//    public void setMeasureAllChildren(boolean measureAllChildren) {
//        mMeasureAllChildren = measureAllChildren;
//    }
//
//    @Override
//    public boolean shouldDelayChildPressedState() {
//        return false;
//    }
//
//    @Override
//    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
//        return p instanceof LayoutParams;
//    }
//
//    @Override
//    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
//        if (sPreserveMarginParamsInLayoutParamConversion) {
//            if (lp instanceof LayoutParams) {
//                return new LayoutParams(lp);
//            } else if (lp instanceof MarginLayoutParams) {
//                return new LayoutParams(lp);
//            }
//        }
//        return new LayoutParams(lp);
//    }
//
//    @Override
//    public CharSequence getAccessibilityClassName() {
//        return FrameLayout.class.getName();
//    }
//
//
//
//    public boolean getMeasureAllChildren() {
//        return mMeasureAllChildren;
//    }
//
//    @Override
//    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
//        return new android.widget.FrameLayout.LayoutParams(getContext(), attrs);
//    }
//
//    /**
//     * Per-child layout information for layouts that support margins.
//     * See {@link android.R.styleable#FrameLayout_Layout FrameLayout Layout Attributes}
//     * for a list of all child view attributes that this class supports.
//     *
//     * @attr ref android.R.styleable#FrameLayout_Layout_layout_gravity
//     */
//    public static class LayoutParams extends MarginLayoutParams {
//        /**
//         * Value for {@link #gravity} indicating that a gravity has not been
//         * explicitly specified.
//         */
//        public static final int UNSPECIFIED_GRAVITY = -1;
//
//        /**
//         * The gravity to apply with the View to which these layout parameters
//         * are associated.
//         * <p>
//         * The default value is {@link #UNSPECIFIED_GRAVITY}, which is treated
//         * by FrameLayout as {@code Gravity.TOP | Gravity.START}.
//         * @see android.view.Gravity
//         * @attr ref android.R.styleable#FrameLayout_Layout_layout_gravity
//         */
//        public int gravity = UNSPECIFIED_GRAVITY;
//
//        public LayoutParams(@NonNull Context c, @Nullable AttributeSet attrs) {
//            super(c, attrs);
//
//            final TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.FrameLayout_layout);
//            gravity = a.getInt(R.styleable.FrameLayout_layout_layout_gravity, UNSPECIFIED_GRAVITY);
//            a.recycle();
//        }
//
//        public LayoutParams(int width, int height) {
//            super(width, height);
//        }
//
//        /**
//         * Create a new set of layout parameters with the specified width, height
//         * and weight.???
//         *
//         * @param width the width, either {@link #MATCH_PARENT},
//         *              {@link #WRAP_CONTENT} or a fixed size in pixels
//         * @param height the height, either {@link #MATCH_PARENT},
//         *               {@link #WRAP_CONTENT} or a fixed size in pixels.
//         * @param gravity the gravity
//         *
//         * @see android.view.Gravity
//         */
//        public LayoutParams(int width, int height, int gravity) {
//            super(width, height);
//            this.gravity = gravity;
//        }
//
//        public LayoutParams(@NonNull ViewGroup.LayoutParams source) {
//            super(source);
//        }
//
//        public LayoutParams(@NonNull ViewGroup.MarginLayoutParams source) {
//            super(source);
//        }
//
//        /**
//         * Copy constructor. Clones the width, height, margin values, and
//         * gravity of the source.
//         *
//         * @param source The layout params to copy from.
//         */
//        public LayoutParams(@NonNull LayoutParams source) {
//            super(source);
//
//            this.gravity = source.gravity;
//        }
//    }
//}
