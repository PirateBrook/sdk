//package com.piratebrook.sdk.widget;
//
//import android.content.ContentResolver;
//import android.content.Context;
//import android.content.res.ColorStateList;
//import android.content.res.Resources;
//import android.content.res.TypedArray;
//import android.graphics.Canvas;
//import android.graphics.ColorFilter;
//import android.graphics.Matrix;
//import android.graphics.PixelFormat;
//import android.graphics.PorterDuff;
//import android.graphics.Rect;
//import android.graphics.RectF;
//import android.graphics.Xfermode;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.net.Uri;
//import android.os.Build;
//import android.support.annotation.DrawableRes;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.util.AttributeSet;
//import android.view.View;
//
//import com.piratebrook.sdk.R;
//import com.piratebrook.sdk.util.LogUtils;
//
//import java.io.IOException;
//import java.io.InputStream;
//
///**
// * Created by wyy on 2017-11-22.
// */
//
//public class UrlImageView extends View {
//
//    private static final String LOG_TAG = "UrlImageView";
//
//    // Application context
//    private Context mContext;
//
//    // settable by the client
//    private Uri mUri;
//    private int mResource = 0;
//    private Matrix mMatrix;
//    private ScaleType mScaleType;
//    private boolean mHaveFrame = false;
//    private boolean mAdjustViewBounds = false;
//    private int mMaxWidth = Integer.MAX_VALUE;
//    private int mMaxHeight = Integer.MAX_VALUE;
//
//    // these are applied to the drawable
//    private ColorFilter mColorFilter = null;
//    private boolean mHasColorFilter = false;
//    private Xfermode mXfermode;
//    private int mAlpha = 255;
//    private final int mViewAlphaScale = 256;
//    private boolean mColorMod = false;
//
//    private Drawable mDrawable = null;
//    private BitmapDrawable mRecycleableBitmapDrawable = null;
//    private ColorStateList mDrawableTintList = null;
//    private PorterDuff.Mode mDrawableTintMode = null;
//    private boolean mHasDrawableTint = false;
//    private boolean mHasDrawableTintMode= false;
//
//    private int[] mState = null;
//    private boolean mMergeState = false;
//    private int mLevel = 0;
//    private int mDrawableWidth;
//    private int mDrawableHeight;
//    private Matrix mDrawMatrix = null;
//
//    // Avoid allocations...
//    private final RectF mTempSrc = new RectF();
//    private final RectF mTempDst = new RectF();
//
//    private boolean mCropToPadding;
//
//    private int mBaseline = -1;
//    private boolean mBaselineAlignBottom = false;
//
//    /** Compatibility modes dependent on targetSdkVersion of the app. */
//    private static boolean sCompatDone;
//
//    /** AdjustViewBounds behavior will be in compatibility mode for the older apps. */
//    private static boolean sCompatAdjustViewBounds;
//
//    /** Whether to pass Resources when creating the source from a stream. */
//    private static boolean sCompatUseCorrectStreamDensity;
//
//    /** Whether to use pre-Nougat drawable visibility dispatching conditions. */
//    private static boolean sCompatDrawableVisibilityDispatch;
//
//    private static final ScaleType[] sScaleTypeArray = {
//            ScaleType.MATRIX,
//            ScaleType.FIT_XY,
//            ScaleType.FIT_START,
//            ScaleType.FIT_CENTER,
//            ScaleType.FIT_END,
//            ScaleType.CENTER,
//            ScaleType.CENTER_CROP,
//            ScaleType.CENTER_INSIDE
//    };
//
//    public UrlImageView(Context context) {
//        super(context);
//        mContext = context.getApplicationContext();
//        initImageView();
//    }
//
//    public UrlImageView(Context context, @Nullable AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public UrlImageView(Context context, AttributeSet attrs, int defStyleAttr) {
//        this(context, attrs, defStyleAttr, 0);
//    }
//
//    public UrlImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        mContext = context.getApplicationContext();
//
//        initImageView();
//
//        // ImageView is not important by default, unless app developer overrode attribute.
//        if (getImportantForAutofill() == IMPORTANT_FOR_AUTOFILL_AUTO) {
//            setImportantForAutofill(IMPORTANT_FOR_AUTOFILL_NO);
//        }
//
//        final TypedArray a = context.obtainStyledAttributes(
//                attrs, R.styleable.UrlImageView, defStyleAttr, defStyleRes);
//
//        final Drawable d = a.getDrawable(R.styleable.UrlImageView_src);
//        if (d != null) {
//            setImageDrawable(d);
//        }
//
//        mBaselineAlignBottom = a.getBoolean(R.styleable.UrlImageView_baselineAlignBottom, false);
//        mBaseline = a.getDimensionPixelSize(R.styleable.UrlImageView_baseline, -1);
//
//        setAdjustViewBounds(a.getBoolean(R.styleable.UrlImageView_adjustViewBounds, false));
//        setMaxWidth(a.getDimensionPixelSize(R.styleable.UrlImageView_maxWidth, Integer.MAX_VALUE));
//        setMaxHeight(a.getDimensionPixelSize(R.styleable.UrlImageView_maxHeight, Integer.MAX_VALUE));
//
//        final int index = a.getInt(R.styleable.UrlImageView_scaleType, -1);
//        if (index >= 0) {
//            setScaleType(sScaleTypeArray[index]);
//        }
//
//        if (a.hasValue(R.styleable.UrlImageView_tint)) {
//            mDrawableTintList = a.getColorStateList(R.styleable.UrlImageView_tint);
//            mHasDrawableTint = true;
//
//            // Prior to L, this attribute would always set a color filter with
//            // blending mode SRC_ATOP. Preserve that default behavior.
//            mDrawableTintMode = PorterDuff.Mode.SRC_ATOP;
//            mHasDrawableTintMode = true;
//        }
//
////        if (a.hasValue(R.styleable.UrlImageView_tintMode)) {
////            mDrawableTintMode = Drawable.pa
////        }
//
//        applyImageTint();
//
//        final int alpha = a.getInt(R.styleable.UrlImageView_drawAlpha, 255);
//        if (alpha != 255) {
//            setImageAlpha(alpha);
//        }
//
//        mCropToPadding = a.getBoolean(
//                R.styleable.UrlImageView_cropToPadding, false);
//
//        a.recycle();
//
//        // need inflate syntax/reader fot matrix
//    }
//
//    public void setImageAlpha(int alpha) {
//        setAlpha(alpha);
//    }
//
//    public void setAlpha(int alpha) {
//        alpha &= 0xFF;      // keep it legal
//        if (mAlpha != alpha) {
//            mAlpha = alpha;
//            mColorMod = true;
//            applyColorMod();
//            invalidate();
//        }
//    }
//
//    public void setMaxHeight(int maxHeight) {
//        mMaxWidth = maxHeight;
//    }
//
//    /**
//     * An optional argument to supply a maximum width for this view. Only valid if
//     * {@link #setAdjustViewBounds(boolean)} has been set to ture. To set an image to be a maximum
//     * of 100 x 100 while preserving the original aspect ratio, do the following: 1) set
//     * adjustViewBounds to true 2) set maxWidth and maxHeight to 100 3) set the height and width
//     * layout params to WRAP_CONTENT.
//     *
//     * <p>
//     * Note that this view could be still smaller than 100 x 100 using this approach if the original
//     * image is small. To set an image to a fixed size, specify that size int the layout params and
//     * then use {@link #setScaleType(ScaleType)} to determine how to fit the image within the
//     * bounds.
//     *
//     * @param maxWidth maximum width for this view
//     */
//    public void setMaxWidth(int maxWidth) {
//        mMaxWidth = maxWidth;
//    }
//
//    private void setAdjustViewBounds(boolean adjustViewBounds) {
//        mAdjustViewBounds = adjustViewBounds;
//        if (adjustViewBounds) {
//            setScaleType(ScaleType.FIT_CENTER);
//        }
//    }
//
//    /**
//     * Controls how the image should be resized or moved to match the size
//     * of this UrlImageView.
//     *
//     * @param scaleType The desired scaling mode.
//     */
//    public void setScaleType(ScaleType scaleType) {
//        if (scaleType == null) {
//            throw  new NullPointerException();
//        }
//
//        if (mScaleType != scaleType) {
//            mScaleType = scaleType;
//
//            setWillNotCacheDrawing(mScaleType == ScaleType.CENTER);
//
//            requestLayout();
//            invalidate();
//        }
//    }
//
//    /**
//     * Sets a drawable as the content of this ImageView.
//     *
//     * @param drawable the Drawable to set, or {@code null} to clear
//     *                 the content.
//     */
//    private void setImageDrawable(@Nullable Drawable drawable) {
//        if (mDrawable != drawable) {
//            mResource = 0;
//            mUri = null;
//
//            final int oldWidth = mDrawableWidth;
//            final int oldHeight = mDrawableHeight;
//
//            updateDrawable(drawable);
//
//            if (oldWidth != mDrawableWidth || oldHeight != mDrawableHeight) {
//                requestLayout();
//            }
//            invalidate();
//        }
//    }
//
//    private void updateDrawable(Drawable d) {
//        if (d != mRecycleableBitmapDrawable && mRecycleableBitmapDrawable != null) {
//            mRecycleableBitmapDrawable.setBitmap(null);
//        }
//
//        boolean sameDrawable = false;
//
//        if (mDrawable != null) {
//            sameDrawable = mDrawable == d;
//            mDrawable.setCallback(null);
//            unscheduleDrawable(mDrawable);
//            if (!sCompatDrawableVisibilityDispatch && !sameDrawable && isAttachedToWindow()) {
//                mDrawable.setVisible(false, false);
//            }
//        }
//
//        mDrawable = d;
//
//        if (d != null) {
//            d.setCallback(this);
//            d.setLayoutDirection(getLayoutDirection());
//            if (d.isStateful()) {
//                d.setState(getDrawableState());
//            }
//            if (!sameDrawable || sCompatDrawableVisibilityDispatch) {
//                final boolean visible = sCompatDrawableVisibilityDispatch
//                        ? getVisibility() == VISIBLE
//                        : isAttachedToWindow() && getWindowVisibility() == VISIBLE && isShown();
//                d.setVisible(visible, true);
//            }
//
//            d.setLevel(mLevel);
//            mDrawableWidth = d.getIntrinsicWidth();
//            mDrawableHeight = d.getIntrinsicHeight();
//            applyImageTint();
//            applyColorMod();
//
//            configureBounds();
//        } else {
//            mDrawableWidth = mDrawableHeight = -1;
//        }
//    }
//
//    private void configureBounds() {
//        if (mDrawable == null || !mHaveFrame) {
//            return;
//        }
//
//        final int dwidth = mDrawableWidth;
//        final int dheight = mDrawableHeight;
//
//        final int vwidth = getWidth() - mPaddingLeft - mPaddingRight;
//        final int vheight = getHeight() - mPaddingTop - mPaddingBottom;
//
//        final boolean fits = (dwidth < 0 || vwidth == dwidth)
//                && (dheight < 0 || vheight == dheight);
//
//        if (dwidth <= 0 || dheight <= 0 || ScaleType.FIT_XY == mScaleType) {
//            /* If the drawable has no intrinsic size, or we're told to
//                scaletofit, then we just fill our entire view.
//            * */
//            mDrawable.setBounds(0, 0, vwidth, vheight);
//            mDrawMatrix = null;
//        } else {
//            // We need to do the scaling ourself, so have the drawable
//            // use its native size.
//            mDrawable.setBounds(0, 0, dwidth, dheight);
//
//            if (ScaleType.MATRIX == mScaleType) {
//                // Use the specified matrix as-is.
//                if (mMatrix.isIdentity()) {
//                    mDrawMatrix = null;
//                } else {
//                    mDrawMatrix = mMatrix;
//                }
//            } else if (fits) {
//                // The bitmap fits exactly, no transform needed.
//                mDrawMatrix = null;
//            } else if (ScaleType.CENTER == mScaleType) {
//                // Center bitmap in view, no scaling.
//                mDrawMatrix = mMatrix;
//                mDrawMatrix.setTranslate(Math.round(vheight - dwidth) * 0.5f,
//                                        Math.round(vheight - dheight) * 0.5f);
//            } else if (ScaleType.CENTER_CROP == mScaleType) {
//                mDrawMatrix = mMatrix;
//
//                float scale;
//                float dx = 0, dy = 0;
//
//                if (dwidth * vheight > vwidth * dheight) {
//                    scale = vheight / dheight;
//                    dx = (vwidth - dwidth * scale) * 0.5f;
//                } else {
//                    scale = vwidth / dwidth;
//                    dy = (vheight - dheight * scale) * 0.5f;
//                }
//            } else if (ScaleType.CENTER_INSIDE == mScaleType) {
//                mDrawMatrix = mMatrix;
//                float scale;
//                float dx;
//                float dy;
//
//                if (dwidth < vwidth && dheight < vheight) {
//                    scale = 1.0f;
//                } else {
//                    scale = Math.min(vwidth / dwidth, vheight / dheight);
//                }
//
//                dx = Math.round((vwidth - dwidth * scale) * 0.5f);
//                dy = Math.round((vheight - dheight * scale) * 0.5f);
//
//                mDrawMatrix.setScale(scale, scale);
//                mDrawMatrix.postTranslate(dx, dy);
//            } else {
//                // Generate the required transform.
//                mTempSrc.set(0, 0, dwidth, dheight);
//                mTempDst.set(0, 0, vwidth, vheight);
//
//                mDrawMatrix = mMatrix;
//                mDrawMatrix.setRectToRect(mTempSrc, mTempDst, scaleTypeToScaleToFit(mScaleType));
//            }
//        }
//    }
//
//    private static final Matrix.ScaleToFit[] sS2FArray = {
//            Matrix.ScaleToFit.FILL,
//            Matrix.ScaleToFit.START,
//            Matrix.ScaleToFit.CENTER,
//            Matrix.ScaleToFit.END
//    };
//
//    private Matrix.ScaleToFit scaleTypeToScaleToFit(ScaleType st) {
//        return sS2FArray[st.nativeInt - 1];
//    }
//
//    private void applyColorMod() {
//        // Only mutate and apply when modifications have occurred. This should
//        // not reset the mColorMod flag, since these filters need to be
//        // re-applied if the Drawable is changed.
//        if (mDrawable != null && mColorMod) {
//            mDrawable = mDrawable.mutate();
//            if (mHasColorFilter) {
//                mDrawable.setColorFilter(mColorFilter);
//            }
//            mDrawable.setXfermode(mXfermode);
//            mDrawable.setAlpha(mAlpha * mViewAlphaScale >> 8);
//        }
//    }
//
//    private void applyImageTint() {
//        if (mDrawable != null && (mHasDrawableTint || mHasDrawableTintMode)) {
//            mDrawable = mDrawable.mutate();
//
//            if (mHasDrawableTint) {
//                mDrawable.setTintList(mDrawableTintList);
//            }
//
//            if (mHasDrawableTintMode) {
//                mDrawable.setTintMode(mDrawableTintMode);
//            }
//
//            // The drawable (or one of its children) my not have been
//            // stateful before applying the tint, so let's try again.
//            if (mDrawable.isStateful()) {
//                mDrawable.setState(getDrawableState());
//            }
//        }
//    }
//
//
//    private void initImageView() {
//        mMatrix = new Matrix();
//        mScaleType = ScaleType.FIT_CENTER;
//
//        if (!sCompatDone) {
//            final int targetSdkVersion = mContext.getApplicationInfo().targetSdkVersion;
//            sCompatAdjustViewBounds = targetSdkVersion <= Build.VERSION_CODES.JELLY_BEAN;
//            sCompatUseCorrectStreamDensity = targetSdkVersion >= Build.VERSION_CODES.M;
//            sCompatDrawableVisibilityDispatch = targetSdkVersion < Build.VERSION_CODES.N;
//            sCompatDone = true;
//        }
//    }
//
//    @Override
//    protected boolean verifyDrawable(@NonNull Drawable dr) {
//        return mDrawable == dr || super.verifyDrawable(dr);
//    }
//
//    @Override
//    public void jumpDrawablesToCurrentState() {
//        super.jumpDrawablesToCurrentState();
//        if (mDrawable != null) mDrawable.jumpToCurrentState();
//    }
//
//    @Override
//    public void invalidateDrawable(@NonNull Drawable dr) {
//        if (dr == mDrawable) {
//            if (dr != null) {
//                // update cached drawable dimensions if they've changed
//                final int w = dr.getIntrinsicWidth();
//                final int h = dr.getIntrinsicHeight();
//                if (w != mDrawableWidth || h != mDrawableHeight) {
//                    mDrawableWidth = w;
//                    mDrawableHeight = h;
//                    // updates the matrix, which is dependent on the bounds
//                    configureBounds();
//                }
//            }
//            /* we invalidate the whole view in this case because it's very
//               hard ot know where the drawable actually is. This is made
//               complicated because of the offsets and transformations that
//               can be applied. In theory we could get the drawable's bounds
//               and run them through the transformation and offsets, but this
//               is probably not worth the effort.
//             */
//            invalidate();
//        } else {
//            super.invalidateDrawable(dr);
//        }
//    }
//
//    @Override
//    public boolean hasOverlappingRendering() {
//        return (getBackground() != null && getBackground().getCurrent() != null);
//    }
//
//    public int getMaxWidth() {
//        return mMaxWidth;
//    }
//
//    public int getMaxHeight() {
//        return mMaxHeight;
//    }
//
//    /**
//     * Get the current Drawable, or null if no Drawable has be assigned.
//     * @return
//     */
//    public Drawable getDrawable() {
//        if (mDrawable == mRecycleableBitmapDrawable) {
//            mRecycleableBitmapDrawable = null;
//        }
//        return mDrawable;
//    }
//
//    private class ImageDrawableCallback implements Runnable {
//
//        private final Drawable drawable;
//        private final Uri uri;
//        private final int resource;
//
//        public ImageDrawableCallback(Drawable drawable, Uri uri, int resource) {
//            this.drawable = drawable;
//            this.uri = uri;
//            this.resource = resource;
//        }
//
//        @Override
//        public void run() {
//            setImageDrawable(drawable);
//            mUri = uri;
//            mResource = resource;
//        }
//    }
//
//    public void setImagImageResource(@DrawableRes int resId) {
//        // The resource configuration may have changed, so we should always
//        // try to load the resource even if the resId hasn't changed.
//        final int oldWidth = mDrawableWidth;
//        final int oldHeight = mDrawableHeight;
//        updateDrawable(null);
//        mResource = resId;
//        mUri = null;
//
//        resolveUri();
//
//        if (oldWidth != mDrawableWidth || oldHeight != mDrawableHeight) {
//            requestLayout();
//        }
//        invalidate();
//    }
//
//    private void resolveUri() {
//        if (mDrawable != null) {
//            return;
//        }
//
//        if (getResources() == null) {
//            return;
//        }
//
//        Drawable d = null;
//
//        if (mResource != 0) {
//            try {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    d = mContext.getDrawable(mResource);
//                } else {
//                    d = mContext.getResources().getDrawable(mResource);
//                }
//            } catch (Resources.NotFoundException e) {
//                mResource = 0;
//            }
//        } else if (mUri != null) {
//            d = getDrawableFromUri(mUri);
//
//            if (d == null) {
//                mUri = null;
//            }
//        } else {
//            return;
//        }
//    }
//
//    private Drawable getDrawableFromUri(Uri uri) {
//        final String scheme = uri.getScheme();
//        if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme)) {
//            try {
//                // Load drawable through Resources, to get the source density information
//                ContentResolver.OpenResourceIdResult r =
//                        mContext.getContentResolver().getResourceId(uri);
//                return r.r.getDrawable(r.id, mContext.getTheme());
//            } catch (Exception e) {
//                LogUtils.d(LOG_TAG, "Unable to open content: " + uri);
//            }
//        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)
//                || ContentResolver.SCHEME_FILE.equals(scheme)) {
//            InputStream stream = null;
//            try {
//                stream = mContext.getContentResolver().openInputStream(uri);
//                return Drawable.createFromResourceStream(sCompatUseCorrectStreamDensity
//                        ? getResources() : null, null, stream, null);
//            } catch (Exception e) {
//                LogUtils.d(LOG_TAG, "Unable to open content: " + uri);
//            } finally {
//                if (stream != null) {
//                    try {
//                        stream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        } else {
//            return Drawable.createFromPath(uri.toString());
//        }
//        return null;
//    }
//
//    @Override
//    public void onRtlPropertiesChanged(int layoutDirection) {
//        super.onRtlPropertiesChanged(layoutDirection);
//
//        if (mDrawable != null) {
//            mDrawable.setLayoutDirection(layoutDirection);
//        }
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        resolveUri();
//        int w;
//        int h;
//
//        // Desired aspect ratio of the view's contents (not including padding)
//        float desiredAspect = 0.0f;
//
//        // We are allowed to change the view's width
//        boolean resizeWidth = false;
//
//        // We are allowed to change the view's height
//        boolean resizeHeight = false;
//
//        final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
//        final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
//
//        if (mDrawable == null) {
//            // If no drawable, its intrinsic size is 0.
//            mDrawableWidth = -1;
//            mDrawableHeight = -1;
//            w = h = 0;
//        } else {
//            w = mDrawableWidth;
//            h = mDrawableHeight;
//            if (w <= 0) w = 1;
//            if (h <= 0) h = 1;
//
//            // We are supposed to adjust view bounds to match the aspect
//            // ratio of our drawable. See if that is possible.
//            if (mAdjustViewBounds) {
//                resizeHeight = widthSpecMode != MeasureSpec.EXACTLY;
//                resizeHeight = heightSpecMode != MeasureSpec.EXACTLY;
//
//                desiredAspect = (float) w / (float) h;
//            }
//        }
//
//        final int pleft = mPaddingLeft;
//        final int pright = mPaddingRight;
//        final int ptop = mPaddingTop;
//        final int pbottom = mPaddingBottom;
//
//        int widthSize;
//        int heightSize;
//
//        if (resizeWidth || resizeHeight) {
//            /* If we get here, it means we want to resize to match the
//                drawables aspect ratio, and we have the freedom to change at
//                least one dimension.
//             */
//
//            // Get the max possible width given our constraints
//            widthSize = resolveAdjustedSize(w + pleft + pright, mMaxWidth, widthMeasureSpec);
//
//            // Get the max possible height given our constraints
//            heightSize = resolveAdjustedSize(h + ptop + pbottom, mMaxHeight, heightMeasureSpec);
//
//            if (desiredAspect != 0.0f) {
//                // See what our actual aspect ratio is
//                final float actualAspect = (float)(widthSize - pleft - pright) /
//                        (heightSize - ptop - pbottom);
//
//                if (Math.abs(actualAspect - desiredAspect) > 0.0000001) {
//
//                    boolean done = false;
//
//                    // Try adjusting width to be proportional
//                    if (resizeWidth) {
//                        int newWidth = (int) (desiredAspect * (heightSize - ptop - pbottom) +
//                                                        pleft + pright);
//
//                        // Allow the width to outgrow its original estimate if height is fixed.
//                        if (!resizeHeight && !sCompatAdjustViewBounds) {
//                            widthSize = resolveAdjustedSize(newWidth, mMaxWidth, widthMeasureSpec);
//                        }
//
//                        if (newWidth <= widthSize) {
//                            widthSize = newWidth;
//                            done = true;
//                        }
//                    }
//
//                    // Try adjusting height to be proportional to width
//                    if (!done && resizeHeight) {
//                        int newHeight = (int)((widthSize - pleft - pright) / desiredAspect) +
//                                ptop + pbottom;
//
//                        // Allow the height to outgrow its original estimate if width is fixed.
//                        if (!resizeWidth && !sCompatAdjustViewBounds) {
//                            heightSize = resolveAdjustedSize(newHeight, mMaxHeight,
//                                    heightMeasureSpec);
//                        }
//
//                        if (newHeight <= heightSize) {
//                            heightSize = newHeight;
//                        }
//                    }
//                } else {
//                    /* We are either don't want to preserve the drawables aspect ratio,
//                       or we are not allowed to change view dimensions. Just measure in
//                       the normal way.
//                     */
//                    w += pleft + pright;
//                    h += ptop + pbottom;
//
//                    w = Math.max(w, getSuggestedMinimumWidth());
//                    h = Math.max(h, getSuggestedMinimumHeight());
//
//                    widthSize = resolveSizeAndState(w, widthMeasureSpec, 0);
//                    heightSize = resolveSizeAndState(h, heightMeasureSpec, 0);
//                }
//
//                setMeasuredDimension(widthSize, heightSize);
//            }
//        }
//
//    }
//
//    private int resolveAdjustedSize(int desiredSize, int maxSize, int measureSpec) {
//        int result = desiredSize;
//        final int specMode = MeasureSpec.getMode(measureSpec);
//        final int specSize = MeasureSpec.getSize(measureSpec);
//        switch (specMode) {
//            case MeasureSpec.UNSPECIFIED:
//                /* Parents says we can be as big as we want. Just don't be larger
//                 *  than max size imposed on ourselves.
//                 */
//                result = Math.min(desiredSize, maxSize);
//                break;
//            case MeasureSpec.AT_MOST:
//                // Parent says we can be as big as we want, up to specSize.
//                // Don't be larger than specSize, and don't be larger than
//                // the max size imposed on ourselves.
//                result = Math.min(Math.min(desiredSize, specSize), maxSize);
//                break;
//            case MeasureSpec.EXACTLY:
//                // No choice. Do want we are told.
//                result = specSize;
//                break;
//        }
//        return result;
//    }
//
//    @Override
//    protected void drawableStateChanged() {
//        super.drawableStateChanged();
//
//        final Drawable drawable = mDrawable;
//        if (drawable != null && drawable.isStateful()
//                && drawable.setState(getDrawableState())) {
//            invalidateDrawable(drawable);
//        }
//    }
//
//    @Override
//    public void drawableHotspotChanged(float x, float y) {
//        super.drawableHotspotChanged(x, y);
//
//        if (mDrawable != null) {
//            mDrawable.setHotspot(x, y);
//        }
//    }
//
//    public void animateTransform(Matrix matrix) {
//        if (mDrawable == null) {
//            return;
//        }
//        if (matrix == null) {
//            mDrawable.setBounds(0, 0, getWidth(), getHeight());
//        } else {
//            mDrawable.setBounds(0, 0, mDrawableWidth, mDrawableHeight);
//            if (mDrawMatrix == null) {
//                mDrawMatrix = new Matrix();
//            }
//            mDrawMatrix.set(matrix);
//        }
//        invalidate();
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        if (mDrawable == null) {
//            return; // couldn't resolve the URI
//        }
//
//        if (mDrawableWidth == 0 || mDrawableHeight == 0) {
//            return;     // nothing to draw (empty bounds)
//        }
//
//        if (mDrawMatrix == null && mPaddingTop == 0 && mPaddingLeft == 0) {
//            mDrawable.draw(canvas);
//        } else {
//            final int saveCount = canvas.getSaveCount();
//            canvas.save();
//
//            if (mCropToPadding) {
//                final int scrollX = mScrollX;
//                final int scrollY = mScrollY;
//                canvas.clipRect(scrollX = mPaddingLeft, scrollY + mPaddingTop,
//                        scrollX + mRight - mLeft - mPaddingRight,
//                        scrollY + mBottom - mTop - mPaddingBottom);
//            }
//
//            canvas.translate(mPaddingLeft, mPaddingTop);
//
//            if (mDrawMatrix != null) {
//                canvas.concat(mDrawMatrix);
//            }
//            mDrawable.draw(canvas);
//            canvas.restoreToCount(saveCount);
//        }
//    }
//
//    /**
//     * <p>
//     * Return the offset of the widget's text baseline from the widget's top
//     * boundary. </p>
//     * @return the offset of the baseline within the widget's bounds or -1
//     *          if baseline alignment is not supported.
//     */
//    @Override
//    public int getBaseline() {
//        if (mBaselineAlignBottom) {
//            return getMeasuredHeight();
//        } else {
//            return mBaseline;
//        }
//    }
//
//    /**
//     * <p>Set the offset of the widget's text baseline from the widget's top
//     * boundary. This value is overridden by the {@link #setBaselineAlignBottom(boolean)}
//     * property.</p>
//     *
//     * @param baseline The baseline to use, or -1 if none is to be provided.
//     */
//    public void setBaseline(int baseline) {
//        if (mBaseline != baseline) {
//            mBaseline = baseline;
//            requestLayout();
//        }
//    }
//
//    /**
//     * Sets whether the baseline of this view to the bottom of the view.
//     * Setting this value overrides any calls to setBaseline.
//     *
//     * @param aligned If true, the image view will be baseline aligned by this bottom edge.
//     *
//     */
//    public void setBaselineAlignBottom(boolean aligned) {
//        if (mBaselineAlignBottom != aligned) {
//            mBaselineAlignBottom = aligned;
//            requestLayout();
//        }
//    }
//
//    /**
//     * Checks whether this view's baseline is considered the bottom of the view.
//     *
//     * @return True if the ImageView's baseline is considered the bottom of the view, false if otherwise.
//     */
//    public boolean getBaselineAlignBottom() {
//        return mBaselineAlignBottom;
//    }
//
//    @Override
//    public boolean isOpaque() {
//        return super.isOpaque() || mDrawable != null && mXfermode == null
//                && mDrawable.getOpacity() == PixelFormat.OPAQUE
//                && mAlpha * mViewAlphaScale >> 8 == 255
//                && isFilledByImage();
//    }
//
//    private boolean isFilledByImage() {
//        if (mDrawable == null) {
//            return false;
//        }
//
//        final Rect bounds = mDrawable.getBounds();
//        final Matrix matrix = mDrawMatrix;
//        if (matrix == null) {
//            return bounds.left <= 0 && bounds.top <= 0 && bounds.right >= getWidth()
//                    && bounds.bottom >= getHeight();
//        } else if (matrix.rectStaysRect()) {
//            final RectF boundsSrc = mTempSrc;
//            final RectF boundsDst = mTempDst;
//            boundsSrc.set(bounds);
//            matrix.mapRect(boundsDst, boundsSrc);
//            return boundsDst.left <= 0 && boundsDst.top <= 0 && boundsDst.right >= getWidth()
//                    && boundsDst.bottom >= getHeight();
//        } else {
//            // If the matrix doesn't map to a rectangle, assume the worst.
//            return false;
//        }
//    }
//
//    @Override
//    public void onVisibilityAggregated(boolean isVisible) {
//        super.onVisibilityAggregated(isVisible);
//        // Only do this for new apps post-Nougat
//        if (mDrawable != null && !sCompatDrawableVisibilityDispatch) {
//            mDrawable.setVisible(isVisible, false);
//        }
//    }
//
//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        // Only do this for old apps pre-Nougat; new apps use onVisibilityAggregated
//        if (mDrawable != null && sCompatDrawableVisibilityDispatch) {
//            mDrawable.setVisible(getVisibility() == VISIBLE, false);
//        }
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        // Only do this for old apps pre-Nougat; new apps use onVisibilityAggregate
//        if (mDrawable != null && sCompatDrawableVisibilityDispatch) {
//            mDrawable.setVisible(false, false);
//        }
//    }
//
//    @Override
//    protected int[] onCreateDrawableState(int extraSpace) {
//        if (mState == null) {
//            return super.onCreateDrawableState(extraSpace);
//        } else if (!mMergeState) {
//            return mState;
//        } else {
//            return mergeDrawableStates(
//                    super.onCreateDrawableState(extraSpace + mState.length), mState);
//        }
//    }
//
//    /**
//     * True when UrlImageVie is adjusting its bounds
//     * to preserve the aspect ratio of this drawable
//     *
//     * @return whether to adjust the bounds of this view
//     * to preserve the original aspect ration of the drawable
//     */
//    public boolean getAdjustViewBounds() {
//        return mAdjustViewBounds;
//    }
//    public enum ScaleType {
//        /**
//         * Scale using the image matrix when drawing.
//         */
//        MATRIX      (0),
//        /**
//         * Scale the image using {@link Matrix.ScaleToFit#FILL}.
//         */
//        FIT_XY      (1),
//        /**
//         * Scale the image using {@link Matrix.ScaleToFit#START}
//         */
//        FIT_START   (2),
//        FIT_CENTER  (3),
//        FIT_END     (4),
//        /**
//         * Center the image in the view, but perform no scaling.
//         */
//        CENTER      (5),
//        /**
//         * Scale the image uniformly (maintain the image's aspect ration) so
//         * that both dimensions (width and height) of the image will be equal
//         * to or larger than the corresponding dimension of the view
//         * (minus padding). The image is then centered in the view.
//         */
//        CENTER_CROP (6),
//        /**
//         * Scale the image uniformly (maintain the image's aspect ratio) so
//         * that both dimensions (width and height) of the image will be equal
//         * to or less than the corresponding dimension of the view
//         * (minus padding). The image is then centered in the view.
//         */
//        CENTER_INSIDE (7);
//
//        ScaleType(int ni) {
//            nativeInt = ni;
//        }
//        final int nativeInt;
//    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
