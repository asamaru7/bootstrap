package net.asamaru.bootstrap.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.orhanobut.logger.Logger;

import net.asamaru.bootstrap.Advisor;
import net.asamaru.bootstrap.lib.JavaScriptInterface;
import net.asamaru.bootstrap.ui.web.AdvancedWebView;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

import java.io.IOException;
import java.util.Arrays;


@EFragment
abstract public class WebViewFragment extends Fragment implements AdvancedWebView.Listener {
	static String appScheme = Advisor.getJsScheme();

	protected AdvancedWebView webView;
	protected JavaScriptInterface jsInterface;

//	@Override
//	public void onAttach(android.app.Activity activity) {
//		super.onAttach(activity);
//	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		webView = new AdvancedWebView(this.getActivity());
		if (Advisor.isDebugable()) {
			webView.clearCache(true);    // 개발을 위한 캐시 제거
		}
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		webView.setCookiesEnabled(true);
		webView.setListener(this, this);
		WebSettings webSettings = webView.getSettings();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		}
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(false);
		webSettings.setSupportZoom(false);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
			webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
		}
		webSettings.setUserAgentString(webSettings.getUserAgentString() + ";B9App " + Advisor.getAppVersion() + " (android)");    // User Agent 설정
		if (Advisor.isDebugable()) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				WebView.setWebContentsDebuggingEnabled(true);
			}
		}
		//webSettings.setSaveFormData(false);
		//webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		//webView.setInitialScale(1);

		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith(appScheme)) {    // b9app action
					String parseUrl = url.substring(appScheme.length());
					int pos = parseUrl.indexOf("/");
					onAction(parseUrl.substring(0, pos), parseUrl.substring(pos + 1));
					return true;
				}
				return false;
			}
		});

		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
				new AlertDialog.Builder(view.getContext())
						.setTitle("알림")
						.setMessage(message)
						.setPositiveButton(android.R.string.ok,
								new AlertDialog.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										result.confirm();
									}
								})
						.setCancelable(false)
						.create()
						.show();
				return true;
			}

//			@Override
//			public void onProgressChanged(WebView view, int progress) {}
		});

		jsInterface = getJavaScriptInterface();

		return webView;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	// interface AdvancedWebView.Listener
	public void onPageStarted(String url, Bitmap favicon) {
	}

	public void onPageFinished(String url) {
	}

	public void onPageError(int errorCode, String description, String failingUrl) {
		Logger.e("code : " + errorCode + " desc : " + description + " fail : " + failingUrl);
	}

	public void onDownloadRequested(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {}

	public void onExternalPageRequest(String url) {}

	// -----------------------------------------------------------------------------
	protected JavaScriptInterface getJavaScriptInterface() {
		return new JavaScriptInterface(this, webView);
	}

	protected void onAction(String action, String arguments) {}
}