package com.columbusagain.camark.view;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class MyButton extends Button {

	public MyButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MyButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MyButton(Context context) {
		super(context);
		init();
	}

	private void init() {

		Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),
				"corpidcdlfbold.ttf");
		setTypeface(typeface);
		setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);

	}

}
