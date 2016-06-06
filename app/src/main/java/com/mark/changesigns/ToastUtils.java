package com.mark.changesigns;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class ToastUtils {
	public static void make(final Context context, final String content) {

		new Handler(context.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {

				Toast.makeText(context, content, Toast.LENGTH_LONG).show();
			}
		});

	}
}
