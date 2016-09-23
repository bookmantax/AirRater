package com.example.brandon.airrater;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by curtishoover on 1/21/15.
 */
public class SFSportNightNS extends TextView {
    public SFSportNightNS(Context context, AttributeSet attrs) {
        super(context, attrs);
        if( this.isInEditMode() )
            this.setTypeface(Typeface.createFromAsset(context.getAssets(), "raw/sfsportsnightns.ttf"));
    }

}
