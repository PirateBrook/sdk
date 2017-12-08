//package com.piratebrook.sdk.widget;
//
//import android.content.Context;
//import android.content.res.ColorStateList;
//import android.content.res.Resources;
//import android.content.res.TypedArray;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Path;
//import android.graphics.PorterDuff;
//import android.graphics.Rect;
//import android.graphics.RectF;
//import android.graphics.Typeface;
//import android.graphics.drawable.Drawable;
//import android.os.Build;
//import android.support.annotation.IntDef;
//import android.support.annotation.Nullable;
//import android.text.BoringLayout;
//import android.text.Editable;
//import android.text.GetChars;
//import android.text.InputFilter;
//import android.text.Layout;
//import android.text.SpanWatcher;
//import android.text.Spannable;
//import android.text.SpannableString;
//import android.text.Spanned;
//import android.text.TextDirectionHeuristic;
//import android.text.TextPaint;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.text.method.ArrowKeyMovementMethod;
//import android.text.method.DialerKeyListener;
//import android.text.method.DigitsKeyListener;
//import android.text.method.MovementMethod;
//import android.text.method.TextKeyListener;
//import android.text.method.TransformationMethod;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.Choreographer;
//import android.view.Gravity;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.ViewConfiguration;
//import android.view.ViewTreeObserver;
//import android.view.accessibility.AccessibilityManager;
//import android.view.inputmethod.EditorInfo;
//import android.view.textclassifier.TextClassifier;
//import android.widget.Scroller;
//import android.widget.TextView;
//
//import org.xmlpull.v1.XmlPullParserException;
//
//import java.io.IOException;
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//import java.lang.ref.WeakReference;
//import java.util.ArrayList;
//import java.util.Locale;
//
///**
// * Created by wyy on 2017-11-24.
// */
//
//public class UrlTextView extends View implements ViewTreeObserver.OnPreDrawListener {
//    static final String LOG_TAG = "TextView";
//    static final boolean DEBUG_EXTRACT = false;
//    static final boolean DEBUG_AUTOFILL = false;
//    private static final float[] TEMP_POSITION = new float[2];
//
//    // Enum for the "typeface" XML parameter.
//    // TODO: How can we get this from the XML instead of hardcoding it here?
//    private static final int SANS = 1;
//    private static final int SERIF = 2;
//    private static final int MONOSPACE= 3;
//
//    // Bitfield for the "numeric" XML parameter.
//    // TODO: How can we get this from the XML instead of hardcoding it here?
//    private static final int SIGNED = 2;
//    private static final int DECIMAL = 4;
//
//    /**
//     * Draw marquee text with fading edges as usual
//     */
//    private static final int MARQUEE_FADE_NORMAL = 0;
//
//    /**
//     * Draw marquee text as ellipsize end while inactive instead of with the fade.
//     * (Useful for devices where the fade can be expensive if overdone)
//     */
//    private static final int MARQUEE_FADE_SWITCH_SHOW_ELLIPSIS = 1;
//
//    /**
//     * Draw marquee text with fading edges because it is currently active/animating.
//     */
//    private static final int MARQUEE_FADE_SWITCH_SHOW_FADE = 2;
//
//    private static final int LINES = 1;
//    private static final int EMS = LINES;
//    private static final int PIXELS = 2;
//
//    private static final RectF TEMP_RECTF = new RectF();
//
//    static final int VERY_WIDE = 1024 * 1024; // XXX should be much larger
//    private static final int ANIMATED_SCROLL_GAP = 250;
//
//    private static final InputFilter[] NO_FILTERS = new InputFilter[0];
//    private static final Spanned EMPTY_SPANNED = new SpannableString("");
//
//    private static final int CHANGE_WATCHER_PRIORITY = 100;
//
//    // New state used to change background based on whether this TextView is multiline.
//    private static final int[] MULTILINE_STATE_SET = { android.R.attr.state_multiline };
//
//    // Accessibility action to share selected text.
//    private static final int ACCESSIBILITY_ACTION_SHARE = 0x10000000;
//
//    static final int ACCESSIBILITY_ACTION_PROCESS_TEXT_START_ID = 0x10000100;
//
//    static final int PROCESS_TEXT_REQUEST_CODE = 100;
//
//    /**
//     * Return code of {@link #doKeyDown}.
//     */
//    private static final int KEY_EVENT_NOT_HANDLED = 0;
//    private static final int KEY_EVENT_HANDLED = -1;
//    private static final int KEY_DOWN_HANDLED_BY_KEY_LISTENER = 1;
//    private static final int KEY_DOWN_HANDLED_BY_MOVEMENT_METHOD = 2;
//
//    // System wide time for last cut, copy or text change action
//    static long sLastCutCopyOrTextChangedTime;
//
//    private ColorStateList mTextColor;
//    private ColorStateList mHintTextColor;
//    private ColorStateList mLinkTextColor;
//
//    private int mCurTextColor;
//    private int mCurHintTextColor;
//    private boolean mFreezesText;
//
//    private Editable.Factory mEditableFactory = Editable.Factory.getInstance();
//    private Spannable.Factory mSpanableFactory = Spannable.Factory.getInstance();
//
//    private float mShadowRadius, mShadowDx, mShadowDy;
//    private boolean mPreDrawRegistered;
//    private boolean mPreDrawListenerDetached;
//
//    private TextClassifier mTextClassifier;
//
//    // A flag to prevent repeated movements from escaping the enclosing text view. The idea here is
//    // that is a user is holding down a movement key to traverse text, we shouldn't also traverse
//    // the view hierarchy. On the other hand, if the user is using the movement key traversed
//    // view (i.e. the first movement was to traverse out of this view, or this view was traversed
//    // into by the user holding the movement key down) then we shouldn't prevent the focus from
//    // changing.
//    private boolean mPreventDefaultMovement;
//
//    private TextUtils.TruncateAt mEllipsize;
//
//    static class Drawables {
//        static final int LEFT = 0;
//        static final int TOP = 1;
//        static final int RIGHT = 2;
//        static final int BOTTOM = 3;
//
//        static final int DRAWABLE_NONE = -1;
//        static final int DRAWABLE_RIGHT = 0;
//        static final int DRAWABLE_LEFT = 1;
//
//        final Rect mCompoundRect = new Rect();
//
//        final Drawable[] mShowing = new Drawable[4];
//
//        ColorStateList mTintList;
//        PorterDuff.Mode mTintMode;
//
//        boolean mHasTint;
//        boolean mHasTintMode;
//
//        Drawable mDrawableStart, mDrawableEnd, mDrawableError, mDrawableTemp;
//        Drawable mDrawableLeftInitial, mDrawableRightInitial;
//
//        boolean mIsRtlCompatibilityMode;
//        boolean mOverride;
//
//        int mDrawableSizeTop, mDrawableSizeBottom, mDrawableSizeLeft, mDrawableSizeRight,
//                mDrawableSizeStart, mDrawableSizeEnd, mDrawableSizeError, mDrawableSizeTemp;
//
//        int mDrawableWidthTop, mDrawableWidthBottom, mDrawableHeightLeft, mDrawableHeightRight,
//                mDrawableHeightStart, mDrawableHeightEnd, mDrawableHeightError, mDrawableHeightTemp;
//
//        int mDrawablePadding;
//
//        int mDrawableSaved = DRAWABLE_NONE;
//
//        public Drawables(Context context) {
//            final int targetSdkVersion = context.getApplicationInfo().targetSdkVersion;
//            mIsRtlCompatibilityMode = targetSdkVersion < Build.VERSION_CODES.JELLY_BEAN_MR1
//                    || !context.getApplicationInfo().hasRtlSupport();
//            mOverride = false;
//        }
//
//        /**
//         * @return {@Code true} if this object contains metadata that needs to
//         *          be retained, {@code false} otherwise
//         */
//        public boolean hasMetadata() {
//            return mDrawablePadding != 0 || mHasTintMode || mHasTint;
//        }
//
//        /**
//         * Update the list of displayed drawables to account for the current
//         * layout direction.
//         *
//         * @param layoutDirection the current layout direction
//         * @return {@code true} if the displayed drawables changed
//         */
//        public boolean resloveWithLayoutDirection(int layoutDirection) {
//            final Drawable previousLeft = mShowing[Drawables.LEFT];
//            final Drawable previousRight = mShowing[Drawables.RIGHT];
//
//            // First reset "left" and "right" drawables to their initial values
//            mShowing[Drawables.LEFT] = mDrawableLeftInitial;
//            mShowing[Drawables.RIGHT] = mDrawableRightInitial;
//
//            if (mIsRtlCompatibilityMode) {
//                // Use "start" drawable as "left" drawable if the "left" drawable was not defined
//                if (mDrawableStart != null && mShowing[Drawables.LEFT] == null) {
//                    mShowing[Drawables.LEFT] = mDrawableStart;
//                    mDrawableSizeLeft = mDrawableSizeStart;
//                    mDrawableHeightLeft = mDrawableHeightStart;
//                }
//                // Use "end" drawable as "right" drawable if the "right" drawable was not defined
//                if (mDrawableEnd != null && mShowing[Drawables.RIGHT] == null) {
//                    mShowing[Drawables.RIGHT] = mDrawableEnd;
//                    mDrawableSizeRight = mDrawableSizeEnd;
//                    mDrawableHeightRight = mDrawableHeightEnd;
//                }
//            } else {
//                // JB-MR1+ normal case: "start" / "end" drawables are overriding "left" / "right"
//                // drawable if and only if they have been defined
//                switch (layoutDirection) {
//                    case LAYOUT_DIRECTION_RTL:
//                        if (mOverride) {
//                            mShowing[Drawables.RIGHT] = mDrawableStart;
//                            mDrawableSizeRight = mDrawableSizeStart;
//                            mDrawableHeightRight = mDrawableHeightStart;
//
//                            mShowing[Drawables.LEFT] = mDrawableEnd;
//                            mDrawableSizeLeft = mDrawableSizeEnd;
//                            mDrawableHeightLeft = mDrawableHeightEnd;
//                        }
//                        break;
//
//                    case LAYOUT_DIRECTION_LTR:
//                    default:
//                        if (mOverride) {
//                            mShowing[Drawables.LEFT] = mDrawableStart;
//                            mDrawableSizeLeft = mDrawableSizeStart;
//                            mDrawableHeightLeft = mDrawableHeightStart;
//
//                            mShowing[Drawables.RIGHT] = mDrawableEnd;
//                            mDrawableSizeRight = mDrawableSizeEnd;
//                            mDrawableHeightRight = mDrawableHeightEnd;
//                        }
//                        break;
//                }
//            }
//
//            applyErrorDrawableIfNeeded(layoutDirection);
//
//            return mShowing[Drawables.LEFT] != previousLeft
//                    || mShowing[Drawables.RIGHT] != previousRight;
//        }
//
//        public void setErrorDrawable(Drawable dr, TextView tv) {
//            if (mDrawableError != dr && mDrawableError != null) {
//                mDrawableError.setCallback(null);
//            }
//            mDrawableError = dr;
//
//            if (mDrawableError != null) {
//                final Rect compoundRect = mCompoundRect;
//                final int[] state = tv.getDrawableState();
//
//                mDrawableError.setState(state);
//                mDrawableError.copyBounds(compoundRect);
//                mDrawableError.setCallback(tv);
//                mDrawableSizeError = compoundRect.width();
//                mDrawableHeightError = compoundRect.height();
//            } else {
//                mDrawableSizeError = mDrawableHeightError = 0;
//            }
//        }
//
//        private void applyErrorDrawableIfNeeded(int layoutDirection) {
//            // first restore the initial state if needed
//            switch (mDrawableSaved) {
//                case DRAWABLE_LEFT:
//                    mShowing[Drawables.LEFT] = mDrawableTemp;
//                    mDrawableSizeLeft = mDrawableSizeTemp;
//                    mDrawableHeightLeft = mDrawableHeightTemp;
//                    break;
//                case DRAWABLE_RIGHT:
//                    mShowing[Drawables.RIGHT] = mDrawableTemp;
//                    mDrawableSizeRight = mDrawableSizeTemp;
//                    mDrawableHeightRight = mDrawableHeightTemp;
//                    break;
//                case DRAWABLE_NONE:
//                default:
//            }
//            // then, if needed, assign the Error drawable to the correct location
//            if (mDrawableError != null) {
//                switch (layoutDirection) {
//                    case LAYOUT_DIRECTION_RTL:
//                        mDrawableSaved = DRAWABLE_LEFT;
//
//                        mDrawableTemp = mShowing[Drawables.LEFT];
//                        mDrawableSizeTemp = mDrawableSizeLeft;
//                        mDrawableHeightTemp = mDrawableHeightLeft;
//
//                        mShowing[Drawables.LEFT] = mDrawableError;
//                        mDrawableSizeLeft = mDrawableSizeError;
//                        mDrawableHeightLeft = mDrawableHeightError;
//                        break;
//                    case LAYOUT_DIRECTION_LTR:
//                    default:
//                        mDrawableSaved = DRAWABLE_RIGHT;
//
//                        mDrawableTemp = mShowing[Drawables.RIGHT];
//                        mDrawableSizeTemp = mDrawableSizeRight;
//                        mDrawableHeightTemp = mDrawableHeightRight;
//
//                        mShowing[Drawables.RIGHT] = mDrawableError;
//                        mDrawableSizeRight = mDrawableSizeError;
//                        mDrawableHeightRight = mDrawableHeightError;
//                        break;
//                }
//            }
//        }
//    }
//
//    Drawables mDrawables;
//
//    private CharWrapper mCharWrapper;
//
//    private Marquee mMarquee;
//    private boolean mRestartMarquee;
//
//    private int mMarqueeRepeatLimit = 3;
//
//    private int mLastLayoutDirection = -1;
//
//    /**
//     * On some devices the fading edges add a performance penalty is used
//     * extensively in the same layout. This mode indicates how the marquee
//     * is currently being shown, if applicable. (mEllipsize will == MARQUEE)
//     */
//    private int mMarqueeFadeMode = MARQUEE_FADE_NORMAL;
//
//    /**
//     * Whe mMarqueeFadeMode is not MARQUEE_FADE_NORMAL, this stores
//     * the layout that should be used when the mode switches.
//     */
//    private Layout mSavedMarqueeModeLayout;
//
//    private CharSequence mText;
//    private CharSequence mTransformed;
//    private BufferType mBufferType = BufferType.NORMAL;
//
//    private CharSequence mHint;
//    private Layout mHintLayout;
//
//    private MovementMethod mMovement;
//
//    private TransformationMethod mTransformation;
//    private boolean mAllowTransformationLengthChange;
//    private ChangeWatcher mChangeWatcher;
//
//    private ArrayList<TextWatcher> mListeners;
//
//    // display attributes
//    private final TextPaint mTextPaint;
//    private boolean mUserSetTextScaleX;
//    private Layout mLayout;
//    private boolean mLocalesChanged = false;
//
//    // True is the internationalized input should be used for numbers and date and time.
//    private final boolean mUseInternationalizedInput;
//
//    private int mGravity = Gravity.TOP | Gravity.START;
//    private boolean mHorizontallyScrolling;
//
//    private int mAutoLinkMask;
//    private boolean mLinksClickable = true;
//
//    private float mSpacingMult = 1.0f;
//    private float mSpacingAdd = 0.0f;
//
//    private int mBreakStrategy;
//    private int mHyphenationFrequency;
//    private int mJustificationMode;
//
//    private int mMaximum = Integer.MAX_VALUE;
//    private int mMaxMode = LINES;
//    private int mMinimum = 0;
//    private int mMinMode = LINES;
//
//    private int mOldMaximum = mMaximum;
//    private int mOldMaxMode = mMaxMode;
//
//    private int mMaxWidth = Integer.MAX_VALUE;
//    private int mMaxWidthMode = PIXELS;
//    private int mMinWidth = 0;
//    private int mMinWidthMode = PIXELS;
//
//    private boolean mSingleLine;
//    private int mDesiredHeightAtMeasure = -1;
//    private boolean mIncludePad = true;
//    private int mDeferScroll = -1;
//
//    // temp primitives, so we don't alloc them on each draw
//    private Rect mTempRect;
//    private long mLastScroll;
//    private Scroller mScroller;
//    private TextPaint mTempTextPaint;
//
//    private BoringLayout.Metrics mBoring, mHintBoring;
//    private BoringLayout mSavedLayout, mSavedHintLayout;
//
//    private TextDirectionHeuristic mTextDir;
//
//    private InputFilter[] mFilters = NO_FILTERS;
//
//    private volatile Locale mCurrentSpellCheckerLocaleCache;
//
//    // It is possible to have a selection even when mEditor is null (programmatically set, like when
//    // a link is pressed). These highlight-related fields do not go in mEditor.
//    int mHighlightColor = 0x6633B5E5;
//    private Path mHighlightPath;
//    private final Paint mHighlightPaint;
//    private boolean mHighlightPathBogus = true;
//
//    // Although these fields are specific to editable text, they are not added to Editor because
//    // they are defined by the TextView's style and are theme-dependent.
//    int mCursorDrawableRes;
//    // These six fields, could be moved to Editor, since we know their default values and we
//    // could condition the creation of the Editor to a non standard value. This is however
//    // brittle since the hardcoded values here (such as
//    // com.android.internal.R.drawable.text_select_handle_left) would have to be updated if the
//    // default style is modified.
//    int mTextSelectHandleLeftRes;
//    int mTextSelectHandleRightRes;
//    int mTextSelectHandleRes;
//    int mTextEditSuggestionItemLayout;
//    int mTextEditSuggestionContainerLayout;
//    int mTextEditSuggestionHighlightStyle;
//
//    /**
//     * {@link android.widget.EditText} specific data, created on demand when one of the Editor
//     * fields is used.
//     * See {@link #createEditorIfNeeded()}
//     */
//    private Editor mEditor;
//
//    private static final int DEVICE_PROVISIONED_UNKNOWN = 0;
//    private static final int DEVICE_PROVISIONED_NO = 1;
//    private static final int DEVICE_PROVISIONED_YES= 2;
//
//    /**
//     * Some special options such as sharing selected text should only be shown if the device
//     * is provisioned. Only check the provisioned state once for a given view instance.
//     */
//    private int mDeviceProvisionedState = DEVICE_PROVISIONED_UNKNOWN;
//
//    /**
//     * The TextView does not auto-size text (default).
//     */
//    private static final int AUTO_SIZE_TEXT_TYPE_NONE = 0;
//
//    /**
//     * The TextView scales text size both horizontally and vertically to fit within the
//     * container.
//     */
//    public static final int AUTO_SIZE_TEXT_TYPE_UNIFORM = 1;
//
//    @IntDef({AUTO_SIZE_TEXT_TYPE_NONE, AUTO_SIZE_TEXT_TYPE_UNIFORM})
//    @Retention(RetentionPolicy.SOURCE)
//    public @interface AutoSizeTextType {}
//    // Default minimum size for auto-sizing text in scaled pixels.
//    private static final int DEFAULT_AUTO_SIZE_MIN_TEXT_SIZE_IN_SP = 12;
//    // Default maximum size for auto-sizing text in scaled pixels.
//    private static final int DEFAULT_AUTO_SIZE_MAX_TEXT_SIZE_IN_SP = 122;
//    // Default value for the step size in pixels.
//    private static final int DEFAULT_AUTO_SIZE_GRANULARITY_IN_PX = 1;
//    // Use this to specify that any of the auto-size configuration int values have not been set.
//    private static final float UNSET_AUTO_SIZE_UNIFORM_CONFIGURATION_VALUE = -1f;
//    // Auto-size text type.
//    private int mAutoSizeTextType = AUTO_SIZE_TEXT_TYPE_NONE;
//    // Specify if auto-size text is need.
//    private boolean mNeedsAutoSizeText = false;
//    // Step size for auto-sizing in pixels.
//    private float mAutoSizeStepGranularityInPx = UNSET_AUTO_SIZE_UNIFORM_CONFIGURATION_VALUE;
//    // Minimum text size for auto-sizing in pixels.
//    private float mAutoSizeMinTextSizeInPx = UNSET_AUTO_SIZE_UNIFORM_CONFIGURATION_VALUE;
//    // Maximum text size for auto-sizing in pixels.
//    private float mAutoSizeMaxTextSizeInPx = UNSET_AUTO_SIZE_UNIFORM_CONFIGURATION_VALUE;
//    // Contains a (specified or computed) distinct sorted set of text sizes in pixels to pick from
//    // when auto-sizing text.
//    private int mAutoSizeTextSizesInPx = EmptyArray.INT;
//    // Specifies whether auto-size should use the provided auto size steps set or is it should
//    // build the steps set using mAutoSizeMinTextSizeInPx, mAutoSizeMaxTextSizeInPx and
//    // mAutoSizeStepGranularityInPx.
//    private boolean mHasPresetAutoSizeValues = false;
//
//    // Indicate whether the text was set from resources or dynamically, so it can be used to
//    // sanitize autofill requests.
//    private boolean mTextFromResource = false;
//
//    /**
//     * Kick-start the font cache for the zygote process (to pay the cost of
//     * initializing freetype for our default font only once).
//     * @hide
//     */
//    public static void preloadFontCache() {
//        Paint p = new Paint();
//        p.setAntiAlias(true);
//        // Ensure that the Typeface is loaded here.
//        // Typically, Typeface is preloaded by zygote but not on all devices, e.g. Android Auto.
//        // So, sets Typeface.DEFAULT explicitly here for ensuring that the Typeface is loaded here
//        // since Paint.measureText can not be called without Typeface static initializer.
//        p.setTypeface(Typeface.DEFAULT);
//        // We don't care about the result, just the side-effect of measuring.
//        p.measureText("H");
//    }
//
//    /**
//     * Interface definition for a callback to be invoked when an action is
//     * performed on the editor.
//     */
//    public interface OnEditorActionListener {
//        /**
//         * Called when an action is being performed.
//         *
//         * @param v The view that was clicked.
//         * @param actionId Identifier of the action. This will be either the identifier
//         *                 you supplied, or {@link android.view.inputmethod.EditorInfo#IME_NULL
//         *                 EditorInfo.IME_NULL} if being called due to the enter key
//         *                 being pressed.
//         * @param event If triggered by an enter key, this is the event;
//         *              otherwise, this is null.
//         * @return Return true if you have consumed the action, else false.
//         */
//        boolean onEditorAction(UrlTextView v, int actionId, KeyEvent event);
//    }
//
//    public UrlTextView(Context context) {
//        this(context, null);
//    }
//
//    public UrlTextView(Context context, @Nullable AttributeSet attrs) {
//        this(context, attrs, com.android.internal.R.attr.textViewStyle);
//    }
//
//    public UrlTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        this(context, attrs, defStyleAttr, 0);
//
//    }
//
//    public UrlTextView(
//            Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//
//        // TextView is important by default, unless app developer overrode attribute.
//        if (getImportantForAutofill() == IMPORTANT_FOR_AUTOFILL_AUTO) {
//            setImportantForAutofill(IMPORTANT_FOR_AUTOFILL_YES);
//        }
//
//        mText = "";
//
//        final Resources res = getResources();
//        final CompatibilityInfo compat = res.getCompatibilityInfo();
//
//        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
//        mTextPaint.density = res.getDisplayMetrics().density;
//        mTextPaint.setCompatibilityScaling(compat.applicationScale);
//
//        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mHighlightPaint.setCompatibilityScaling(compat.applicationScale);
//
//        mMovement = getDefaultMovementMethod();
//
//        mTransformation = null;
//
//        int textColorHighlight = 0;
//        ColorStateList textColor = null;
//        ColorStateList textColorHint = null;
//        ColorStateList textColorLink = null;
//        int textSize = 15;
//        String fontFamily = null;
//        Typeface fontTypeface = null;
//        boolean fontFamilyExplicit = false;
//        int typefaceIndex = -1;
//        int styleIndex = -1;
//        boolean allCaps = false;
//        int shadowcolor = 0;
//        float dx = 0, dy = 0, r = 0;
//        boolean elegant = false;
//        float letterSpacing = 0;
//        String fontFeatureSettings = null;
//        mBreakStrategy = Layout.BREAK_STRATEGY_SIMPLE;
//        mHyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE;
//        mJustificationMode = Layout.JUSTIFICATION_MODE_NONE;
//
//        final Resources.Theme theme = context.getTheme();
//
//        /* Look the appearance up without checking first if the exists because
//         * almost every TextView has one and it greatly simplifies the logic
//         * to be able to parse the appearance first and then let specific tags
//         * for this View override it.
//         */
//        TypedArray a = theme.obtainStyledAttributes(attrs,
//                com.android.internal.R.styleable.TextViewAppearance, defStyleAttr, defStyleRes);
//            TypedArray appearance = null;
//            int ap = a.getResourceId(
//                    com.android.internal.R.styleable.TextViewAppearance_textAppearance, -1);
//            a.recycle();
//            if (ap != -1) {
//                appearance = theme.obtainStyledAttributes(
//                        ap, com.android.internal.R.styleable.TextAppearance);
//            }
//            if (appearance != null) {
//                int n = appearance.getIndexCount();
//                for (int i = 0; i < n; i++) {
//                    int attr = appearance.getIndex(i);
//
//                    switch (attr) {
//                        case com.android.internal.R.styleable.TextAppearance_textColorHighlight:
//                            textColorHighlight = appearance.getColor(attr, textColorHighlight);
//                            break;
//
//                        case com.android.internal.R.styleable.TextAppearance_textColor:
//                            textColor = appearance.getColorStateList(attr);
//                            break;
//
//                        case com.android.internal.R.styleable.TextAppearance_textColorHint:
//                            textColorHint = appearance.getColorStateList(attr);
//                            break;
//
//                        case com.android.internal.R.styleable.TextAppearance_textColorLink:
//                            textColorLink = appearance.getColorStateList(attr);
//                            break;
//
//                        case com.android.internal.R.styleable.TextAppearance_textSize:
//                            textSize = appearance.getDimensionPixelSize(attr, textSize);
//                            break;
//
//                        case com.android.internal.R.styleable.TextAppearance_typeface:
//                            typefaceIndex = appearance.getInt(attr, -1);
//                            break;
//
//                        case com.android.internal.R.styleable.TextAppearance_fontFamily:
//                            if (!context.isRestricted() && context.canLoadUnsafeResources()) {
//                                try {
//                                    fontTypeface = appearance.getFont(attr);
//                                } catch (UnsupportedOperationException
//                                        | Resources.NotFoundException e) {
//                                    // Expected if it is not a font resource.
//                                }
//                            }
//                            if (fontTypeface == null) {
//                                fontFamily = appearance.getString(attr);
//                            }
//                            break;
//
//                        case com.android.internal.R.styleable.TextAppearance_textStyle:
//                            styleIndex = appearance.getInt(attr, -1);
//                            break;
//
//                        case com.android.internal.R.styleable.TextAppearance_textAllCaps:
//                            allCaps = appearance.getBoolean(attr, false);
//                            break;
//
//                        case com.android.internal.R.styleable.TextAppearance_shadowColor:
//                            shadowcolor = appearance.getInt(attr, 0);
//                            break;
//
//                        case com.android.internal.R.styleable.TextAppearance_shadowDx:
//                            dx = appearance.getFloat(attr, 0);
//                            break;
//
//                        case com.android.internal.R.styleable.TextAppearance_shadowDy:
//                            dy = appearance.getFloat(attr, 0);
//                            break;
//
//                        case com.android.internal.R.styleable.TextAppearance_shadowRadius:
//                            r = appearance.getFloat(attr, 0);
//                            break;
//
//                        case com.android.internal.R.styleable.TextAppearance_elegantTextHeight:
//                            elegant = appearance.getBoolean(attr, false);
//                            break;
//
//                        case com.android.internal.R.styleable.TextAppearance_letterSpacing:
//                            letterSpacing = appearance.getFloat(attr, 0);
//                            break;
//
//                        case com.android.internal.R.styleable.TextAppearance_fontFeatureSettings:
//                            fontFeatureSettings = appearance.getString(attr);
//                            break;
//                    }
//                }
//
//                appearance.recycle();
//            }
//
//            boolean editable = getDefaultEditable();
//            CharSequence inputMethod = null;
//            int numeric = 0;
//            CharSequence digits = null;
//            boolean phone = false;
//            boolean autotext = false;
//            int autocap = -1;
//            int buffertype = 0;
//            boolean selectallonfocus = false;
//            Drawable drawableLeft = null, drawableTop = null, drawableRight = null,
//                    drawableBottom = null, drawableStart = null, drawableEnd = null;
//            ColorStateList drawableTint= null;
//            PorterDuff.Mode drawableTintMode = null;
//            int drawablePadding = 0;
//            int ellipsize = -1;
//            boolean singleLine = false;
//            int maxLength = -1;
//            CharSequence text = "";
//            CharSequence hint = null;
//            boolean password = false;
//            float autoSizeMinTextSizeInPx = UNSET_AUTO_SIZE_UNIFORM_CONFIGURATION_VALUE;
//            float autoSizeMaxTextSizeInPx = UNSET_AUTO_SIZE_UNIFORM_CONFIGURATION_VALUE;
//            float autoSizeStepGranularityInPx = UNSET_AUTO_SIZE_UNIFORM_CONFIGURATION_VALUE;
//            int inputType = EditorInfo.TYPE_NULL;
//            a = theme.obtainStyledAttributes(
//                    attrs, com.android.internal.R.styleable.TextView, defStyleAttr, defStyleRes);
//
//            int n = a.getIndexCount();
//
//            boolean fromResourceId = false;
//        for (int i = 0; i < n; i++) {
//            int attr = a.getIndex(i);
//
//            switch (attr) {
//                case com.android.internal.R.styleable.TextView_editable:
//                    editable = a.getBoolean(attr, editable);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_inputMethod:
//                    inputMethod = a.getText(attr);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_numeric:
//                    numeric = a.getInt(attr, numeric);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_digits:
//                    digits = a.getText(attr);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_phoneNumber:
//                    phone = a.getBoolean(attr, phone);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_autoText:
//                    autotext = a.getBoolean(attr, autotext);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_capitalize:
//                    autocap = a.getInt(attr, autocap);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_bufferType:
//                    buffertype = a.getInt(attr, buffertype);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_selectAllOnFocus:
//                    selectallonfocus = a.getBoolean(attr, selectallonfocus);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_autoLink:
//                    mAutoLinkMask = a.getInt(attr, 0);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_linksClickable:
//                    mLinksClickable = a.getBoolean(attr, true);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_drawableLeft:
//                    drawableLeft = a.getDrawable(attr);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_drawableTop:
//                    drawableTop = a.getDrawable(attr);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_drawableRight:
//                    drawableRight = a.getDrawable(attr);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_drawableBottom:
//                    drawableBottom = a.getDrawable(attr);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_drawableStart:
//                    drawableStart = a.getDrawable(attr);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_drawableEnd:
//                    drawableEnd = a.getDrawable(attr);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_drawableTint:
//                    drawableTint = a.getColorStateList(attr);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_drawableTintMode:
//                    drawableTintMode = Drawable.parseMode(a.getInt(attr, -1), drawableTintMode);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_drawablePadding:
//                    drawablePadding = a.getDimensionPixelSize(attr, drawablePadding);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_maxLines:
//                    setMaxLines(a.getInt(attr, -1));
//                    break;
//
//                case com.android.internal.R.styleable.TextView_maxHeight:
//                    setMaxHeight(a.getDimensionPixelSize(attr, -1));
//                    break;
//
//                case com.android.internal.R.styleable.TextView_lines:
//                    setLines(a.getInt(attr, -1));
//                    break;
//
//                case com.android.internal.R.styleable.TextView_height:
//                    setHeight(a.getDimensionPixelSize(attr, -1));
//                    break;
//
//                case com.android.internal.R.styleable.TextView_minLines:
//                    setMinLines(a.getInt(attr, -1));
//                    break;
//
//                case com.android.internal.R.styleable.TextView_minHeight:
//                    setMinHeight(a.getDimensionPixelSize(attr, -1));
//                    break;
//
//                case com.android.internal.R.styleable.TextView_maxEms:
//                    setMaxEms(a.getInt(attr, -1));
//                    break;
//
//                case com.android.internal.R.styleable.TextView_maxWidth:
//                    setMaxWidth(a.getDimensionPixelSize(attr, -1));
//                    break;
//
//                case com.android.internal.R.styleable.TextView_ems:
//                    setEms(a.getInt(attr, -1));
//                    break;
//
//                case com.android.internal.R.styleable.TextView_width:
//                    setWidth(a.getDimensionPixelSize(attr, -1));
//                    break;
//
//                case com.android.internal.R.styleable.TextView_minEms:
//                    setMinEms(a.getInt(attr, -1));
//                    break;
//
//                case com.android.internal.R.styleable.TextView_minWidth:
//                    setMinWidth(a.getInt(attr, -1));
//                    break;
//
//                case com.android.internal.R.styleable.TextView_gravity:
//                    setGravity(a.getInt(attr, -1));
//                    break;
//
//                case com.android.internal.R.styleable.TextView_hint:
//                    hint = a.getText(attr);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_text:
//                    fromResourceId = true;
//                    text = a.getText(attr);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_scrollHorizontally:
//                    if (a.getBoolean(attr, false)) {
//                        setHorizontallyScrolling(true);
//                    }
//                    break;
//
//                case com.android.internal.R.styleable.TextView_singleLine:
//                    singleLine = a.getBoolean(attr, singleLine);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_ellipsize:
//                    ellipsize = a.getInt(attr, ellipsize);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_marqueeRepeatLimit:
//                    setMarqueeRepeatLimit(a.getInt(attr, mMarqueeRepeatLimit));
//                    break;
//
//                case com.android.internal.R.styleable.TextView_includeFontPadding:
//                    if (!a.getBoolean(attr, true)) {
//                        setIncludeFontPadding(false);
//                    }
//                    break;
//
//                case com.android.internal.R.styleable.TextView_cursorVisible:
//                    if (!a.getBoolean(attr, true)) {
//                        setCursorVisible(false);
//                    }
//                    break;
//
//                case com.android.internal.R.styleable.TextView_maxLength:
//                    maxLength = a.getInt(attr, -1);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_textScaleX:
//                    setTextScaleX(a.getFloat(attr, 1.0f));
//                    break;
//
//                case com.android.internal.R.styleable.TextView_freezesText:
//                    mFreezesText = a.getBoolean(attr, false);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_shadowColor:
//                    shadowcolor = a.getInt(attr, 0);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_shadowDx:
//                    dx = a.getFloat(attr, 0);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_shadowDy:
//                    dy = a.getFloat(attr, 0);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_shadowRadius:
//                    r = a.getFloat(attr, 0);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_enabled:
//                    setEnabled(a.getBoolean(attr, isEnabled()));
//                    break;
//
//                case com.android.internal.R.styleable.TextView_textColorHighlight:
//                    textColorHighlight = a.getColor(attr, textColorHighlight);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_textColor:
//                    textColor = a.getColorStateList(attr);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_textColorHint:
//                    textColorHint = a.getColorStateList(attr);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_textColorLink:
//                    textColorLink = a.getColorStateList(attr);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_textSize:
//                    textSize = a.getDimensionPixelSize(attr, textSize);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_typeface:
//                    typefaceIndex = a.getInt(attr, typefaceIndex);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_textStyle:
//                    styleIndex = a.getInt(attr, styleIndex);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_fontFamily:
//                    if (!context.isRestricted() && context.canLoadUnsafeResource()) {
//                        try {
//                            fontTypeface = a.getFont(attr);
//                        } catch (UnsupportedOperationException | Resources.NotFoundException e) {
//                            // Expected if it is not a resource reference or if it is a reference to
//                            // another resource type.
//                        }
//                    }
//                    if (fontTypeface == null) {
//                        fontFamily = a.getString(attr);
//                    }
//                    fontFamilyExplicit = true;
//                    break;
//
//                case com.android.internal.R.styleable.TextView_password:
//                    password = a.getBoolean(attr, password);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_lineSpacingExtra:
//                    mSpacingAdd = a.getDimensionPixelSize(attr, (int) mSpacingAdd);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_lineSpacingMultiplier:
//                    mSpacingMult = a.getFloat(attr, mSpacingMult);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_inputType:
//                    inputType = a.getInt(attr, EditorInfo.TYPE_NULL);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_allowUndo:
//                    createEditorIfNeeded();
//                    mEditor.mAllowUndo = a.getBoolean(attr, true);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_imeOptions:
//                    createEditorIfNeeded();
//                    mEditor.createInputContentTypeIfNeeded();
//                    mEditor.mInputContentType.imeOptions = a.getInt(attr,
//                            mEditor.mInputContentType.imeOptions);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_imeActionLabel:
//                    createEditorIfNeeded();
//                    mEditor.createInputContentTypeIfNeeded();
//                    mEditor.mInputContentType.imeActionLabel = a.getText(attr);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_imeActionId:
//                    createEditorIfNeeded();
//                    mEditor.createInputContentTypeIfNeeded();
//                    mEditor.mInputContentType.imeActionId = a.getInt(attr,
//                            mEditor.mInputContentType.imeActionId);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_privateImeOptions:
//                    setPrivateImeOptions(a.getString(attr));
//                    break;
//
//                case com.android.internal.R.styleable.TextView_editorExtras:
//                    try {
//                        setInputExtras(a.getResourceId(attr, 0));
//                    } catch (XmlPullParserException e) {
//                        Log.w(LOG_TAG, "Failure reading input extras", e);
//                    } catch (IOException e) {
//                        Log.w(LOG_TAG, "Failure reading input extras", e);
//                    }
//                    break;
//
//                case com.android.internal.R.styleable.TextView_textCursorDrawable:
//                    mCursorDrawableRes = a.getResourceId(attr, 0);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_textSelectHandleLeft:
//                    mTextSelectHandleLeftRes = a.getResourceId(attr, 0);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_textSelectHandleRight:
//                    mTextSelectHandleRightRes = a.getResourceId(attr, 0);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_textSelectHandle:
//                    mTextSelectHandleRes = a.getResourceId(attr, 0);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_textEditSuggestionContainerLayout:
//                    mTextEditSuggestionContainerLayout = a.getResourceId(attr, 0);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_textEditSuggestionHighlightStyle:
//                    mTextEditSuggestionHighlightStyle = a.getResourceId(attr, 0);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_textIsSelectable:
//                    setTextIsSelectable(a.getBoolean(attr, false));
//                    break;
//
//                case com.android.internal.R.styleable.TextView_textAllCaps:
//                    allCaps = a.getBoolean(attr, false);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_elegantTextHeight:
//                    elegant = a.getBoolean(attr, false);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_letterSpacing:
//                    letterSpacing = a.getFloat(attr, 0);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_fontFeatureSettings:
//                    fontFeatureSettings = a.getString(attr);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_breakStrategy:
//                    mBreakStrategy = a.getInt(attr, Layout.BREAK_STRATEGY_SIMPLE);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_hyphenationFrequency:
//                    mHyphenationFrequency = a.getInt(attr, Layout.HYPHENATION_FREQUENCY_NONE);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_autoSizeTextType:
//                    mAutoSizeTextType = a.getInt(attr, AUTO_SIZE_TEXT_TYPE_NONE);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_autoSizeStepGranularity:
//                    autoSizeStepGranularityInPx = a.getDimension(attr,
//                            UNSET_AUTO_SIZE_UNIFORM_CONFIGURATION_VALUE);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_autoSizeMinTextSize:
//                    autoSizeMinTextSizeInPx = a.getDimension(attr,
//                            UNSET_AUTO_SIZE_UNIFORM_CONFIGURATION_VALUE);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_autoSizeMaxTextSize:
//                    autoSizeMaxTextSizeInPx = a.getDimension(attr,
//                            UNSET_AUTO_SIZE_UNIFORM_CONFIGURATION_VALUE);
//                    break;
//
//                case com.android.internal.R.styleable.TextView_autoSizePresetSizes:
//                    final int autoSizeStepSizeArrayResId = a.getResourceId(attr, 0);
//                    if (autoSizeStepSizeArrayResId > 0) {
//                        final TypedArray autoSizePresetTextSizes = a.getResources()
//                                .obtainTypedArray(autoSizeStepSizeArrayResId);
//                        setupAutoSizeUniformPresetSizes(autoSizePresetTextSizes);
//                        autoSizePresetTextSizes.recycle();
//                    }
//                    break;
//                case com.android.internal.R.styleable.TextView_justificationMode:
//                    mJustificationMode = a.getInt(attr, Layout.JUSTIFICATION_MODE_NONE);
//                    break;
//            }
//        }
//
//        a.recycle();
//
//        BufferType bufferType = BufferType.EDITABLE;
//
//        final int variation =
//                inputType & (EditorInfo.TYPE_MASK_CLASS | EditorInfo.TYPE_MASK_VARIATION);
//        final boolean passwordInputType = variation
//                == (EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
//        final boolean webPasswordInputType = variation
//                == (EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD);
//        final boolean numberPasswordInputType = variation
//                == (EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD);
//
//        mUseInternationalizedInput =
//                context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.O;
//
//        if (inputMethod != null) {
//            Class<?> c;
//
//            try {
//                c = Class.forName(inputMethod.toString());
//            } catch (ClassNotFoundException ex) {
//                throw new RuntimeException(ex);
//            }
//
//            try {
//                createEditorIfNeeded();
//                mEditor.mKeyListener = c.newInstance();
//            } catch (InstantiationException ex) {
//                throw new RuntimeException(ex);
//            } catch (IllegalAccessException ex) {
//                throw new RuntimeException(ex);
//            }
//            try {
//                mEditor.mInputType = inputType != EditorInfo.TYPE_NULL
//                        ? inputType
//                        : mEditor.mKeyListener.getInputType();
//            } catch (IncompatibleClassChangeError e) {
//                mEditor.mInputType = EditorInfo.TYPE_CLASS_TEXT;
//            }
//        } else if (digits != null) {
//            createEditorIfNeeded();
//            mEditor.mKeyListener = DigitsKeyListener.getInstance(digits.toString());
//            // If no input type was specified, we will default to generic
//            // text, since we can't tell the IME about the set of digits
//            // that was selected.
//            mEditor.mInputType = inputType != EditorInfo.TYPE_NULL
//                    ? inputType : EditorInfo.TYPE_CLASS_TEXT;
//        } else if (inputType != EditorInfo.TYPE_NULL) {
//            setInputType(inputType, true);
//            // If set, the input type overrides what was set using the deprecated singleLine flag.
//            singleLine = !isMultilineInputType(inputType);
//        } else if (phone) {
//            createEditorIfNeeded();
//            mEditor.mKeyListener = DialerKeyListener.getInstance();
//            mEditor.mInputType = inputType = EditorInfo.TYPE_CLASS_PHONE;
//        } else if (numeric != 0) {
//            createEditorIfNeeded();
//            mEditor.mKeyListener = DigitsKeyListener.getInstance(
//                    null,  // locale
//                    (numeric & SIGNED) != 0,
//                    (numeric & DECIMAL) != 0);
//            inputType = mEditor.mKeyListener.getInputType();
//            mEditor.mInputType = inputType;
//        } else if (autotext || autocap != -1) {
//            TextKeyListener.Capitalize cap;
//
//            inputType = EditorInfo.TYPE_CLASS_TEXT;
//
//            switch (autocap) {
//                case 1:
//                    cap = TextKeyListener.Capitalize.SENTENCES;
//                    inputType |= EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES;
//                    break;
//
//                case 2:
//                    cap = TextKeyListener.Capitalize.WORDS;
//                    inputType |= EditorInfo.TYPE_TEXT_FLAG_CAP_WORDS;
//                    break;
//
//                case 3:
//                    cap = TextKeyListener.Capitalize.CHARACTERS;
//                    inputType |= EditorInfo.TYPE_TEXT_FLAG_CAP_CHARACTERS;
//                    break;
//
//                default:
//                    cap = TextKeyListener.Capitalize.NONE;
//                    break;
//            }
//
//            createEditorIfNeeded();
//            mEditor.mKeyListener = TextKeyListener.getInstance(autotext, cap);
//            mEditor.mInputType = inputType;
//        } else if (editable) {
//            createEditorIfNeeded();
//            mEditor.mKeyListener = TextKeyListener.getInstance();
//            mEditor.mInputType = EditorInfo.TYPE_CLASS_TEXT;
//        } else if (isTextSelectable()) {
//            // Prevent text changes from keyboard.
//            if (mEditor != null) {
//                mEditor.mKeyListener = null;
//                mEditor.mInputType = EditorInfo.TYPE_NULL;
//            }
//            bufferType = BufferType.SPANNABLE;
//            // So that selection can be changed using arrow keys and touch is handled.
//            setMovementMethod(ArrowKeyMovementMethod.getInstance());
//        } else {
//            if (mEditor != null) mEditor.mKeyListener = null;
//
//            switch (buffertype) {
//                case 0:
//                    bufferType = BufferType.NORMAL;
//                    break;
//                case 1:
//                    bufferType = BufferType.SPANNABLE;
//                    break;
//                case 2:
//                    bufferType = BufferType.EDITABLE;
//                    break;
//            }
//        }
//
//        if (mEditor != null) {
//            mEditor.adjustInputType(password, passwordInputType, webPasswordInputType,
//                    numberPasswordInputType);
//        }
//
//        if (selectallonfocus) {
//            createEditorIfNeeded();
//            mEditor.mSelectAllOnFocus = true;
//
//            if (bufferType == TextView.BufferType.NORMAL) {
//                bufferType = TextView.BufferType.SPANNABLE;
//            }
//        }
//
//        // Set up the tint (if needed) before setting the drawables so that it
//        // gets applied correctly.
//        if (drawableTint != null || drawableTintMode != null) {
//            if (mDrawables == null) {
//                mDrawables = new Drawables(context);
//            }
//            if (drawableTint != null) {
//                mDrawables.mTintList = drawableTint;
//                mDrawables.mHasTint = true;
//            }
//            if (drawableTintMode != null) {
//                mDrawables.mTintMode = drawableTintMode;
//                mDrawables.mHasTintMode = true;
//            }
//        }
//
//        // This call will save the initial left/right drawables
//        setCompoundDrawablesWithIntrinsicBounds(
//                drawableLeft, drawableTop, drawableRight, drawableBottom);
//        setRelativeDrawablesIfNeeded(drawableStart, drawableEnd);
//        setCompoundDrawablePadding(drawablePadding);
//
//        // Same as setSingleLine(), but make sure the transformation method and maximum number
//        // of lines of height are unchanged for multi-line UrlTextView.
//        setInputTypeSingleLine(singleLine);
//        applySingleLine(singleLine, singleLine, singleLine);
//
//        if (singleLine && getKeyListener() == null && ellipsize < 0) {
//            ellipsize = 3; // END
//        }
//
//        switch (ellipsize) {
//            case 1:
//                setEllipsize(TextUtils.TruncateAt.START);
//                break;
//            case 2:
//                setEllipsize(TextUtils.TruncateAt.MIDDLE);
//                break;
//            case 3:
//                setEllipsize(TextUtils.TruncateAt.END);
//                break;
//            case 4:
//                if (ViewConfiguration.get(context).isFadingMarqueeEnabled()) {
//                    setHorizontalFadingEdgeEnabled(true);
//                    mMarqueeFadeMode = MARQUEE_FADE_NORMAL;
//                } else {
//                    setHorizontalFadingEdgeEnabled(false);
//                    mMarqueeFadeMode = MARQUEE_FADE_SWITCH_SHOW_ELLIPSIS;
//                }
//                setEllipsize(TextUtils.TruncateAt.MARQUEE);
//                break;
//        }
//    }
//
//    /**
//     * Subclasses override this to specify a default movement method.
//     */
//    protected MovementMethod getDefaultMovementMethod() {
//        return null;
//    }
//
//    public enum BufferType {
//        NORMAL, SPANNABLE, EDITABLE
//    }
//    private static class CharWrapper implements CharSequence, GetChars, GraphicsOperations {
//        private char[] mChars;
//        private int mStart, mLength;
//
//        public CharWrapper(char[] chars, int start, int len) {
//            mChars = chars;
//            mStart = start;
//            mLength = len;
//        }
//
//        void set(char[] chars, int start, int len) {
//            mChars = chars;
//            mStart = start;
//            mLength = len;
//        }
//
//        public int length() {
//            return mLength;
//        }
//
//        public char charAt(int off) {
//            return mChars[off + mStart];
//        }
//
//        @Override
//        public String toString() {
//            return new String(mChars, mStart, mLength);
//        }
//
//        public CharSequence subSequence(int start, int end) {
//            if (start < 0 || end < 0 || start > mLength || end > mLength) {
//                throw new IndexOutOfBoundsException(start + ", " + end);
//            }
//
//            return new String(mChars, start + mStart, end - start);
//        }
//
//        public void getChars(int start, int end, char[] buff, int off) {
//            if (start < 0 || end < 0 || start > mLength || end > mLength) {
//                throw new IndexOutOfBoundsException(start + ", " + end);
//            }
//
//            System.arraycopy(mChars, start + mStart, buff, off, end - start);
//        }
//
//        @Override
//        public void drawText(BasCanvas c, int start, int end,
//                             float x, float y, Paint p) {
//            c.drawText(mChars, start + mStart, end - start, x, y, p);
//        }
//
//        public void drawTextRun(BaseCanvas c, int start, int end,
//                                int contextStart, int contextEnd, float x, float y, boolean isRtl,
//                                Paint p) {
//            int count = end - start;
//            int contextCount = contextEnd - contextStart;
//            c.drawTextRun(mChars, start + mStart, count, contextStart + mStart,
//                    contextCount, x, y, isRtl, p);
//        }
//
//        public float measureText(int start, int end, Paint p) {
//            return p.measureText(mChars, start + mStart, end - start);
//        }
//
//        public int getTextWidths(int start, int end, float[] widths, Paint p) {
//            return p.getTextWidths(mChars, start + mStart, end - start, widths);
//        }
//
//        public float getTextRunAdvances(int start, int end, int contextStart,
//                                        int contextEnd, boolean isRtl, float[] advances,
//                                        int advancesIndex, Paint p) {
//            int count = end - start;
//            int contextCount = contextEnd - contextStart;
//            return p.getTextRunAdvances(mChars, start + mStart, count,
//                    contextStart + mStart, contextCount, isRtl, advances,
//                    advancesIndex);
//        }
//
//        public int getTextRunCursor(int contextStart, int contextEnd, int dir,
//                                    int offset, int cursorOpt, Paint p) {
//            int contextCount = contextEnd - contextStart;
//            return p.getTextRunCursor(mChars, contextStart + mStart,
//                    contextCount, dir, offset + mStart, cursorOpt);
//        }
//    }
//
//    private static final class Marquee {
//        // TODO: Add an option to configure this
//        private static final float MARQUEE_DELTA_MAX = 0.07f;
//        private static final int MARQUEE_DELAY = 1200;
//        private static final int MARQUEE_DP_PER_SECOND = 30;
//
//        private static final byte MARQUEE_STOPPED = 0x0;
//        private static final byte MARQUEE_STARTING = 0x1;
//        private static final byte MARQUEE_RUNNING = 0x2;
//
//        private final WeakReference<TextView> mView;
//        private final Choreographer mChoreographer;
//
//        private byte mStatus = MARQUEE_STOPPED;
//        private final float mPixelsPreSecond;
//        private float mMaxScroll;
//        private float mMaxFadeScroll;
//        private float mGhostStart;
//        private float mGhostOffset;
//        private float mFadeStop;
//        private int mRepeatLimit;
//
//        private float mScroll;
//        private long mLastAnimationMs;
//
//        Marquee(TextView v) {
//            final float density = v.getContext().getResources().getDisplayMetrics().density;
//            mPixelsPreSecond = MARQUEE_DP_PER_SECOND * density;
//            mView = new WeakReference<TextView>(v);
//            mChoreographer = Choreographer.getInstance();
//        }
//
//        private Choreographer.FrameCallback mTickCallback = new Choreographer.FrameCallback() {
//            @Override
//            public void doFrame(long frameTimeNanos) {
//                tick();
//            }
//        };
//
//        private Choreographer.FrameCallback mStartCallback = new Choreographer.FrameCallback() {
//
//            @Override
//            public void doFrame(long frameTimeNanos) {
//                mStatus = MARQUEE_RUNNING;
//                mLastAnimationMs = mChoreographer.getFrameTime();
//                tick();
//            }
//        };
//
//        private Choreographer.FrameCallback mRestartCallback = new Choreographer.FrameCallback() {
//            @Override
//            public void doFrame(long frameTimeNanos) {
//                if (mStatus == MARQUEE_RUNNING) {
//                    if (mRepeatLimit >= 0) {
//                        mRepeatLimit--;
//                    }
//                    start(mRepeatLimit);
//                }
//            }
//        };
//
//        void tick() {
//            if (mStatus != MARQUEE_RUNNING) {
//                return;
//            }
//
//            mChoreographer.removeFrameCallback(mTickCallback);
//
//            final TextView textView = mView.get();
//            if (textView != null && (textView.isFocused() || textView.isSelected())) {
//                long currentMs = mChoreographer.getFrameTime();
//                long deltaMs = currentMs - mLastAnimationMs;
//                mLastAnimationMs = currentMs;
//                float deltaPx = deltaMs / 1000f * mPixelsPreSecond;
//                mScroll += deltaPx;
//                if (mScroll > mMaxScroll) {
//                    mScroll = mMaxScroll;
//                    mChoreographer.postFrameCallbackDelayed(mRestartCallback, MARQUEE_DELAY);
//                } else {
//                    mChoreographer.postFrameCallback(mTickCallback);
//                }
//                textView.invalidate();
//            }
//        }
//
//        void stop() {
//            mStatus = MARQUEE_STOPPED;
//            mChoreographer.removeFrameCallback(mStartCallback);
//            mChoreographer.removeFrameCallback(mRestartCallback);
//            mChoreographer.removeFrameCallback(mTickCallback);
//            resetScroll();
//        }
//
//        private void resetScroll() {
//            mScroll = 0.0f;
//            final TextView textView = mView.get();
//            if (textView != null) textView.invalidate();
//        }
//
//        void start(int repeatLimit) {
//            if (repeatLimit == 0) {
//                stop();
//                return;
//            }
//            mRepeatLimit = repeatLimit;
//            final TextView textView = mView.get();
//            if (textView != null && textView.mLayout != null) {
//                mStatus = MARQUEE_STARTING;
//                mScroll = 0.0f;
//                final int textWidth = textView.getWidth() - textView.getCompoundPaddingLeft()
//                        - textView.getCompoundPaddingLeft();
//                final float lineWidth = textView.mLayout.getLineWidth(0);
//                final float gap = textWidth / 3.0f;
//                mGhostStart = lineWidth - textWidth + gap;
//                mMaxScroll = mGhostStart + textWidth;
//                mGhostOffset = lineWidth + gap;
//                mFadeStop = lineWidth + textWidth / 6.0f;
//                mMaxFadeScroll = mGhostStart + lineWidth + lineWidth;
//
//                textView.invalidate();
//                mChoreographer.postFrameCallback(mStartCallback);
//            }
//        }
//
//        float getGhostOffset() {
//            return mGhostOffset;
//        }
//
//        float getScroll() {
//            return mScroll;
//        }
//
//        float getMaxFadeScroll() {
//            return mMaxFadeScroll;
//        }
//
//        boolean shouldDrawLeftFade() {
//            return mScroll <= mFadeStop;
//        }
//
//        boolean shouldDrawGhost() {
//            return mStatus == MARQUEE_RUNNING && mScroll > mGhostStart;
//        }
//
//        boolean isRunning() {
//            return mStatus == MARQUEE_RUNNING;
//        }
//
//        boolean isStopped() {
//            return mStatus == MARQUEE_STOPPED;
//        }
//    }
//
//    private class ChangeWatcher implements TextWatcher, SpanWatcher {
//
//        private CharSequence mBeforeText;
//
//        @Override
//        public void beforeTextChanged(CharSequence buffer, int start, int before, int after) {
//            if (AccessibilityManager.getInstance(mContext).isEnabled()
//                    && !isPasswordInputType(getInputType()) && !hasPasswordTransformationMethod()) {
//                mBeforeText = buffer.toString();
//            }
//
//            UrlTextView.this.sendBeforeTextChanged(buffer, start, before, after);
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//
//        }
//
//        @Override
//        public void onSpanAdded(Spannable text, Object what, int start, int end) {
//
//        }
//
//        @Override
//        public void onSpanRemoved(Spannable text, Object what, int start, int end) {
//
//        }
//
//        @Override
//        public void onSpanChanged(Spannable text, Object what, int ostart, int oend, int nstart, int nend) {
//
//        }
//
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
