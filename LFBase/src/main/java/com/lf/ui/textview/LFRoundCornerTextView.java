package com.lf.ui.textview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.appcompat.widget.AppCompatTextView;

import com.lf.base.R;


/**
 * @date: 2024/7/5
 */
public class LFRoundCornerTextView extends AppCompatTextView {
    public LFRoundCornerTextView(Context context) {
        this(context, null);
    }

    public LFRoundCornerTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LFRoundCornerTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);


        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundCornerTextView);

        float pressedRatio = a.getFloat(R.styleable.RoundCornerTextView_lf_rc_pressed_ratio, 0.80f);
        int cornerRadius = a.getLayoutDimension(R.styleable.RoundCornerTextView_lf_rc_corner_radius, 0);

        ColorStateList solidColor = a.getColorStateList(R.styleable.RoundCornerTextView_lf_rc_backgroud_color);
        int strokeColor = a.getColor(R.styleable.RoundCornerTextView_lf_rc_border_color, 0x0);
        int strokeWidth = a.getDimensionPixelSize(R.styleable.RoundCornerTextView_lf_rc_border_width, 0);
        int strokeDashWidth = a.getDimensionPixelSize(R.styleable.RoundCornerTextView_lf_rc_border_dash_width, 0);
        int strokeDashGap = a.getDimensionPixelSize(R.styleable.RoundCornerTextView_lf_rc_border_dash_space, 0);

        a.recycle();

        setSingleLine(true);
        setGravity(Gravity.CENTER);

        RoundDrawable rd = new RoundDrawable(cornerRadius == -1);
        rd.setCornerRadius(cornerRadius == -1 ? 0 : cornerRadius);
        rd.setStroke(strokeWidth, strokeColor, strokeDashWidth, strokeDashGap);

        if (solidColor == null) {
            solidColor = ColorStateList.valueOf(0);
        }
        if (solidColor.isStateful()) {
            rd.setSolidColors(solidColor);
        } else if (pressedRatio > 0.0001f) {
            rd.setSolidColors(csl(solidColor.getDefaultColor(), pressedRatio));
        } else {
            rd.setColor(solidColor.getDefaultColor());
        }
        setBackground(rd);
    }

    // 灰度
    private int greyer(int color) {
        int blue = (color & 0x000000FF) >> 0;
        int green = (color & 0x0000FF00) >> 8;
        int red = (color & 0x00FF0000) >> 16;
        int grey = Math.round(red * 0.299f + green * 0.587f + blue * 0.114f);
        return Color.argb(0xff, grey, grey, grey);
    }

    // 明度
    private int darker(int color, float ratio) {
        color = (color >> 24) == 0 ? 0x22808080 : color;
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= ratio;
        return Color.HSVToColor(color >> 24, hsv);
    }

    private ColorStateList csl(int normal, float ratio) {
        //        int disabled = greyer(normal);
        int pressed = darker(normal, ratio);
        int[][] states = new int[][]{{android.R.attr.state_pressed}, {}};
        int[] colors = new int[]{pressed, normal};
        return new ColorStateList(states, colors);
    }

    private static class RoundDrawable extends GradientDrawable {
        private boolean mIsStadium = false;

        private ColorStateList mSolidColors;
        private int mFillColor;

        public RoundDrawable(boolean isStadium) {
            mIsStadium = isStadium;
        }

        public void setSolidColors(ColorStateList colors) {
            mSolidColors = colors;
            setColor(colors.getDefaultColor());
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            if (mIsStadium) {
                RectF rect = new RectF(getBounds());
                setCornerRadius((rect.height() > rect.width() ? rect.width() : rect.height()) / 2);
            }
        }

        @Override
        public void setColor(int argb) {
            mFillColor = argb;
            super.setColor(argb);
        }

        @Override
        protected boolean onStateChange(int[] stateSet) {
            if (mSolidColors != null) {
                final int newColor = mSolidColors.getColorForState(stateSet, 0);
                if (mFillColor != newColor) {
                    setColor(newColor);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean isStateful() {
            return super.isStateful() || (mSolidColors != null && mSolidColors.isStateful());
        }
    }
}
