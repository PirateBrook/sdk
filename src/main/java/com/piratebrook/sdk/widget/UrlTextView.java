package com.piratebrook.sdk.widget;

import android.graphics.RectF;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by wyy on 2017-11-24.
 */

public class UrlTextView extends View implements ViewTreeObserver.OnPreDrawListener {
    static final String LOG_TAG = "TextView";
    static final boolean DEBUG_EXTRACT = false;
    static final boolean DEBUG_AUTOFILL = false;
    private static final float[] TEMP_POSITION = new float[2];

    // Enum for the "typeface" XML parameter.
    // TODO: How can we get this from the XML instead of hardcoding it here?
    private static final int SANS = 1;
    private static final int SERIF = 2;
    private static final int MONOSPACE= 3;

    // Bitfield for the "numeric" XML parameter.
    // TODO: How can we get this from the XML instead of hardcoding it here?
    private static final int SIGNED = 2;
    private static final int DECIMAL = 4;

    /**
     * Draw marquee text with fading edges as usual
     */
    private static final int MARQUEE_FADE_NORMAL = 0;

    /**
     * Draw marquee text as ellipsize end while inactive instead of with the fade.
     * (Useful for devices where the fade can be expensive if overdone)
     */
    private static final int MARQUEE_FADE_SWITCH_SHOW_ELLIPSIS = 1;

    /**
     * Draw marquee text with fading edges because it is currently active/animating.
     */
    private static final int MARQUEE_FADE_SWITCH_SHOW_FADE = 2;

    private static final int LINES = 1;
    private static final int EMS = LINES;
    private static final int PIXELS = 2;

    private static final RectF TEMP_RECTF = new RectF();

    static final int VERY_WIDE = 1024 * 1024; // XXX should be much larger
    private static final int ANIMATED_SCROLL_GAP = 250;

    private static final InputFilter[] NO_FILTERS = new InputFilter[0];
    private static final Spanned EMPTY_SPANNED = new SpannableString("");

    private static final int CHANGE_WATCHER_PRIORITY = 100;

    // New state used to change background based on whether this TextView is multiline.
    private static final int[] MULTILINE_STATE_SET = { android.R.attr.state_multiline };

    // Accessibility action to share selected text.
    private static final int ACCESSIBILITY_ACTION_SHARE = 0x10000000;

}


















