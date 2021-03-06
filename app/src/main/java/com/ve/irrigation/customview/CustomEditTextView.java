package com.ve.irrigation.customview;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.ve.irrigation.irrigation.R;

/**
 * Created by laxmi on 27/4/18.
 */

public class CustomEditTextView extends android.support.v7.widget.AppCompatEditText {
    public CustomEditTextView(Context context) {
        super(context);
        init(context);
    }

    public CustomEditTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomEditTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);


    }


    private void init(Context context) {
        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/cambria.ttf");
        setTypeface(tf);
        setTextColor(ContextCompat.getColor(context, R.color.textcolor));
        setTextSize(18f);

    }


}
