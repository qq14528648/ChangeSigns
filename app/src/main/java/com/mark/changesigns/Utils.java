package com.mark.changesigns;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

public class Utils {

	public static void doStartApplicationWithPackageName(Context context,
			String packagename) {

		// 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
		PackageInfo packageinfo = null;
		try {
			packageinfo = context.getPackageManager().getPackageInfo(
					packagename, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (packageinfo == null) {
			return;
		}

		// 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(packageinfo.packageName);

		// 通过getPackageManager()的queryIntentActivities方法遍历
		List<ResolveInfo> resolveinfoList = context.getPackageManager()
				.queryIntentActivities(resolveIntent, 0);

		ResolveInfo resolveinfo = resolveinfoList.iterator().next();
		if (resolveinfo != null) {
			// packagename = 参数packname
			String packageName = resolveinfo.activityInfo.packageName;
			// 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
			String className = resolveinfo.activityInfo.name;
			// LAUNCHER Intent
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);

			// 设置ComponentName参数1:packagename参数2:MainActivity路径
			ComponentName cn = new ComponentName(packageName, className);

			intent.setComponent(cn);
			context.startActivity(intent);
		}
	}

	
	private static final String SCHEME = "package";

	/**
	 * 
	 * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
	 */

	private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";

	/**
	 * 
	 * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
	 */

	private static final String APP_PKG_NAME_22 = "pkg";

	/**
	 * 
	 * InstalledAppDetails所在包名
	 */

	private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";

	/**
	 * 
	 * InstalledAppDetails类名
	 */

	private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

	public static void showInstalledAppDetails(Context context,
			String packageName) {

		Intent intent = new Intent();

		final int apiLevel = Build.VERSION.SDK_INT;

		if (apiLevel >= 9) { // 2.3（ApiLevel 9）以上，使用SDK提供的接口

			intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

			Uri uri = Uri.fromParts(SCHEME, packageName, null);

			intent.setData(uri);

		} else { // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）

			// 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。

			final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22

			: APP_PKG_NAME_21);

			intent.setAction(Intent.ACTION_VIEW);

			intent.setClassName(APP_DETAILS_PACKAGE_NAME,

			APP_DETAILS_CLASS_NAME);

			intent.putExtra(appPkgName, packageName);

		}

		context.startActivity(intent);

	}
}
