package com.piratebrook.sdk.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.piratebrook.sdk.R;

/**
 * Created by wyy on 2017-11-22.
 */

public class UrlImageView extends View {

    private static final String LOG_TAG = "UrlImageView";

    // Application context
    private Context mContext;

    // settable by the client
    private Uri mUri;
    private int mResource = 0;
    private Matrix mMatrix;
    private ScaleType mScaleType;
    private boolean mHaveFrame = false;
    private boolean mAdjustViewBounds = false;
    private int mMaxWidth = Integer.MAX_VALUE;
    private int mMaxHeight = Integer.MAX_VALUE;

    // these are applied to the drawable
    private ColorFilter mColorFilter = null;
    private boolean mHasColorFilter = false;
    private Xfermode mXfermode;
    private int mAlpha = 255;
    private final int mViewAlphaScale = 256;
    private boolean mColorMod = false;

    private Drawable mDrawable = null;
    private BitmapDrawable mRecycleableBitmapDrawable = null;
    private ColorStateList mDrawableTintList = null;
    private PorterDuff.Mode mDrawableTintMode = null;
    private boolean mHasDrawableTint = false;
    private boolean mHasDrawableTintMode= false;

    private int[] mState = null;
    private boolean mMergeState = false;
    private int mLevel = 0;
    private int mDrawableWidth;
    private int mDrawableHeight;
    private Matrix mDrawMatrix = null;

    // Avoid allocations...
    private final RectF mTempSrc = new RectF();
    private final RectF mTempDst = new RectF();

    private boolean mCropToPadding;

    private int mBaseline = -1;
    private boolean mBaselineAlignBottom = false;

    /** Compatibility modes dependent on targetSdkVersion of the app. */
    private static boolean sCompatDone;

    /** AdjustViewBounds behavior will be in compatibility mode for the older apps. */
    private static boolean sCompatAdjustViewBounds;

    /** Whether to pass Resources when creating the source from a stream. */
    private static boolean sCompatUseCorrectStreamDensity;

    /** Whether to use pre-Nougat drawable visibility dispatching conditions. */
    private static boolean sCompatDrawableVisibilityDispatch;

    private static final ScaleType[] mScaleTypeArray = {
            ScaleType.MATRIX,
            ScaleType.FIT_XY,
            ScaleType.FIT_START,
            ScaleType.FIT_CENTER,
            ScaleType.FIT_END,
            ScaleType.CENTER,
            ScaleType.CENTER_CROP,
            ScaleType.CENTER_INSIDE
    };

    public UrlImageView(Context context) {
        super(context);
        mContext = context.getApplicationContext();
        initImageView();
    }

    public UrlImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UrlImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public UrlImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context.getApplicationContext();

        initImageView();

        // ImageView is not important by default, unless app developer overrode attribute.
        if (getImportantForAutofill() == IMPORTANT_FOR_AUTOFILL_AUTO) {
            setImportantForAutofill(IMPORTANT_FOR_AUTOFILL_NO);
        }

//        final TypedArray a = context.obtainStyledAttributes(
//                attrs, R.styleable.
//        )
    }

    private void initImageView() {
        mMatrix = new Matrix();
        mScaleType = ScaleType.FIT_CENTER;

        if (!sCompatDone) {
            final int targetSdkVersion = mContext.getApplicationInfo().targetSdkVersion;
            sCompatAdjustViewBounds = targetSdkVersion <= Build.VERSION_CODES.JELLY_BEAN;
            sCompatUseCorrectStreamDensity = targetSdkVersion >= Build.VERSION_CODES.M;
            sCompatDrawableVisibilityDispatch = targetSdkVersion < Build.VERSION_CODES.N;
            sCompatDone = true;
        }
    }

    public enum ScaleType {
        /**
         * Scale using the image matrix when drawing.
         */
        MATRIX      (0),
        /**
         * Scale the image using {@link Matrix.ScaleToFit#FILL}.
         */
        FIT_XY      (1),
        /**
         * Scale the image using {@link Matrix.ScaleToFit#START}
         */
        FIT_START   (2),
        FIT_CENTER  (3),
        FIT_END     (4),
        /**
         * Center the image in the view, but perform no scaling.
         */
        CENTER      (5),
        /**
         * Scale the image uniformly (maintain the image's aspect ration) so
         * that both dimensions (width and height) of the image will be equal
         * to or larger than the corresponding dimension of the view
         * (minus padding). The image is then centered in the view.
         */
        CENTER_CROP (6),
        /**
         * Scale the image uniformly (maintain the image's aspect ratio) so
         * that both dimensions (width and height) of the image will be equal
         * to or less than the corresponding dimension of the view
         * (minus padding). The image is then centered in the view.
         */
        CENTER_INSIDE (7);

        ScaleType(int ni) {
            nativeInt = ni;
        }
        final int nativeInt;
    }
}





























