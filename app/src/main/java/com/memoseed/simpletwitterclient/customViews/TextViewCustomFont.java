package com.memoseed.simpletwitterclient.customViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.memoseed.simpletwitterclient.R;


public class TextViewCustomFont extends AppCompatTextView {

	public TextViewCustomFont(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}
	
	public TextViewCustomFont(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
		
	}
	
	public TextViewCustomFont(Context context) {
		super(context);
		init(null);
	}
	
	private void init(AttributeSet attrs) {
		if (attrs!=null) {
			 TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomFontView);
			 String fontName = a.getString(R.styleable.CustomFontView_fontName);
			 if (fontName!=null) {
				 Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/"+fontName);
				 setTypeface(myTypeface);
			 }
			 a.recycle();
		}
	}

}
