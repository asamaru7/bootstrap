package net.asamaru.bootstrap.fragment;

import android.annotation.SuppressLint;
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

import java.util.Locale;


@EFragment
abstract public class WebViewFragment extends Fragment implements AdvancedWebView.Listener {
	static String appScheme = Advisor.getJsScheme();

	protected AdvancedWebView webView;
	protected JavaScriptInterface jsInterface;

//	@Override
//	public void onAttach(android.app.Activity activity) {
//		super.onAttach(activity);
//	}

	@SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
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
		webSettings.setUserAgentString(webSettings.getUserAgentString() + ";" + Advisor.getUserAgentName() + " " + Advisor.getAppVersion() + " (android)");    // User Agent 설정
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
				if (onClientJsAlert(view, url, message, result)) {
					return true;
				}
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

			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				onClientReceivedTitle(view, title);
			}

//			@Override
//			public void onProgressChanged(WebView view, int progress) {}
		});

		jsInterface = getJavaScriptInterface();
		webView.addJavascriptInterface(jsInterface, "ASInterface");

		webView.setClipChildren(false);
		webView.setClipToPadding(false);
//		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {	// stock browser에서 input에 대한 입력 영역이 아래로 밀림
//		webView.setPadding(0, systemBarManager.getConfig().getPixelInsetTop(true), 0, 0);

		// webview scroll bug 개선
		// WebView adjustResize windowSoftInputMode breaks when activity is fullscreen
		// AndroidBug5497Workaround.assistActivity(this);

		return webView;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	// interface AdvancedWebView.Listener
	public void onPageStarted(String url, Bitmap favicon) {}

	public void onPageFinished(String url) {}

	public void onPageError(int errorCode, String description, String failingUrl) {
		Logger.e("code : " + errorCode + " desc : " + description + " fail : " + failingUrl);
	}

	public void onDownloadRequested(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {}

	public void onExternalPageRequest(String url) {}

	abstract public void reloadHtml();


	// -----------------------------------------------------------------------------
	public boolean onClientJsAlert(WebView view, String url, String message, final JsResult result) {
		return false;
	}

	public void onClientReceivedTitle(WebView view, String title) {
	}

	// -----------------------------------------------------------------------------
	protected JavaScriptInterface getJavaScriptInterface() {
		return new JavaScriptInterface(this, webView);
	}

	protected void onAction(String action, String arguments) {}

	// -----------------------------------------------------------------------------
	static protected String getInjectHead() {
//			int top = (int) systemBarManager.getConfig().getDpInsetTop(true);
		int top = 0;
		String html = "";
		if (top > 0) {
			String marginCss = "";
			marginCss += "html { padding-top:" + top + "px !important; } .appTopMargin { margin-top:" + top + "px; }";
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
				// webView.setPadding(0, systemBarManager.getConfig().getPixelInsetTop(true), 0, 0); 여기서 사용되는 높이에 대한 보정
				marginCss += "input[type=text],"
						+ "input[type=password],"
						+ "input[type=email],"
						+ "input[type=url],"
						+ "input[type=time],"
						+ "input[type=date],"
						+ "input[type=datetime-local],"
						+ "input[type=tel],"
						+ "input[type=number],"
						+ "input[type=search],"
						+ "textarea { [; position:relative; ]; [; top:-" + top + "px; ]; [; -webkit-transform:translate3d(0, " + top + "px, 0); ];";
			}
			html += "var cssText = \"" + marginCss + "\";";
			html += "var css = document.createElement(\"style\"); css.type = \"text/css\";";
			html += "if (css.styleSheet) { css.styleSheet.cssText = cssText; } else { css.appendChild(document.createTextNode(cssText)); }";
			html += "head.appendChild(css); css=null; head=null;";
			html += "window.appTopMargin = " + top + ";";
		}
		html += "window.appLanguage = '" + Locale.getDefault().getLanguage() + "';";
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {    // stock browser
			html += "try { document.documentElement.className += ' STOCK_BROWSER'; } catch(e) {}";    // input wrong position bug 개선을 위한 처리
		}
		return html;
	}
}