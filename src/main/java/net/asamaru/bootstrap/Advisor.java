package net.asamaru.bootstrap;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.CookieSyncManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import java.util.concurrent.atomic.AtomicInteger;

@EBean(scope = EBean.Scope.Singleton)
public class Advisor implements Application.ActivityLifecycleCallbacks {
	static public String deviceUuid;

	static private Application mApp;
	static private Dialog mLoading;
	static private android.app.Activity mActiveActivity;
	static private boolean mDebugable;
	static private float density;
	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

	public void setApp(Application app) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			CookieSyncManager.createInstance(app.getApplicationContext());
		}

		Advisor.deviceUuid = Settings.Secure.getString(app.getContentResolver(), Settings.Secure.ANDROID_ID);
		mApp = app;
		mApp.registerActivityLifecycleCallbacks(this);

		// mDebugable = (0 != (app.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
		// https://code.google.com/p/android/issues/detail?id=52962
		// library project의 경우 DEBUG가 항상 false로 나오는 문제가 있음
		mDebugable = BuildConfig.DEBUG;
		try {
			PackageManager pm = mApp.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(mApp.getPackageName(), 0);
			mDebugable = ((pi.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		Log.e("BOOTSTRAP", "debugable : " + mDebugable);
		density = app.getResources().getDisplayMetrics().density;
	}

	static public String getDeviceUUID() {
		return Advisor.deviceUuid;
	}

	static public Application getApp() {
		return mApp;
	}

	static public String getAppVersion() {
		Context context = getAppContext();
		String appVersion;
		try {
			PackageInfo i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			appVersion = i.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			appVersion = "undefined";
		}
		return appVersion;
	}

	static public int dpToPixel(int dp) {
		//return TypedValue.complexToDimensionPixelSize(dp, mApp.getResources().getDisplayMetrics());
		return (int) (dp * density);
	}

	static public boolean isDebugable() {
		return mDebugable;
	}

	static public Context getAppContext() {
		return mApp.getApplicationContext();
	}

	static public String getJsScheme() {
		return "app://";
	}

	// ---------------------------------------------------------------

	/**
	 * 로딩중 표시
	 */
	public void showLoading() {
		showCancelableLoading(false);
	}

	public void showCancelableLoading() {
		showCancelableLoading(true);
	}

	@UiThread
	protected void showCancelableLoading(boolean cancelable) {
		if (mLoading == null) {
			mLoading = new Dialog(mActiveActivity, R.style.NewDialog);
			mLoading.setCancelable(cancelable);
			mLoading.addContentView(
					new ProgressBar(mActiveActivity),
					new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			mLoading.show();
		}
	}

	/**
	 * 로딩중 숨김
	 */
	@UiThread
	public void hideLoading() {
		if (mLoading != null) {
			mLoading.dismiss();
			mLoading = null;
		}
	}

	static public void showToast(final String message) {
		Handler handler_ = new Handler(Looper.getMainLooper());
		handler_.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(mApp.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
			}
		});
	}

	static public void startActivity(Intent intent) {
		mActiveActivity.startActivity(intent);
	}

	public static int generateViewId() {
		for (; ; ) {
			final int result = sNextGeneratedId.get();
			// aapt-generated IDs have the high byte nonzero; clamp to the range under that.
			int newValue = result + 1;
			if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
			if (sNextGeneratedId.compareAndSet(result, newValue)) {
				return result;
			}
		}
	}

	// ---------------------------------------------------------------
	// interface Application.ActivityLifecycleCallbacks

	public void onActivityCreated(android.app.Activity activity, Bundle savedInstanceState) {
		// Logger.d("activity created " + activity.toString());
	}

	public void onActivityStarted(android.app.Activity activity) {
		// Logger.d("activity started " + activity.toString());
	}

	public void onActivityResumed(android.app.Activity activity) {
		// Logger.d("activity resumed " + activity.toString());
		mActiveActivity = activity;
	}

	public void onActivityPaused(android.app.Activity activity) {
		// Logger.d("activity paused " + activity.toString());
	}

	public void onActivityStopped(android.app.Activity activity) {
		// Logger.d("activity stopped " + activity.toString());
	}

	public void onActivitySaveInstanceState(android.app.Activity activity, Bundle outState) {
		// Logger.d("activity save instance " + activity.toString());
	}

	public void onActivityDestroyed(android.app.Activity activity) {
		// Logger.d("activity destroyed " + activity.toString());
	}
}
