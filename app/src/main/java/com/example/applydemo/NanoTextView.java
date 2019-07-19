package com.example.applydemo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

public class NanoTextView extends TextView {
    public NanoTextView(Context context) {
        super(context);
    }

    public NanoTextView(Context context,  @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NanoTextView(Context context,  @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
