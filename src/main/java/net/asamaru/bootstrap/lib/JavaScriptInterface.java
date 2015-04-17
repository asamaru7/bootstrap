package net.asamaru.bootstrap.lib;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.app.Fragment;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class JavaScriptInterface {
	protected Activity activity;
	protected Fragment fragment;
	protected WebView webview;
	protected String callbackFuncName;

	public JavaScriptInterface(Activity activity, WebView webview) {
		this.activity = activity;
		this.webview = webview;
	}

	public JavaScriptInterface(Fragment fragment, WebView webview) {
		this.fragment = fragment;
		this.webview = webview;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) { }

	protected void callJavaScript(String methodName, Object... params) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("javascript:try{");
		stringBuilder.append(methodName);
		stringBuilder.append("(");
		int iCnt = params.length;
		for (int i = 0; i < iCnt; i++) {
			if (i != 0) {
				stringBuilder.append(",");
			}
			if (params[i] instanceof String) {
				stringBuilder.append("'");
				stringBuilder.append(params[i]);
				stringBuilder.append("'");
			}
		}
		stringBuilder.append(")}catch(error){}");
		//stringBuilder.append(")}catch(error){Android.onError(error.message);}");
//		Logger.d(stringBuilder.toString());
		webview.loadUrl(stringBuilder.toString());
	}

	@SuppressWarnings("unused")
	@JavascriptInterface
	public void popup(String url) {
		Intent intentBrowser = new Intent();
		intentBrowser.setAction(Intent.ACTION_VIEW);
		intentBrowser.setData(Uri.parse(url));
	}
}
