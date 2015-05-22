package net.asamaru.bootstrap.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebView;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

@EFragment
public class WebViewUrlFragment extends WebViewFragment {
	@FragmentArg
	protected String url;
	boolean needsInjectCss = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		webView.loadUrl(url);
		return webView;
	}

	public void reloadHtml() {
		webView.reload();
	}

	// ---------------------------------------------------------
	// webview client
	@Override
	public boolean onClientJsAlert(WebView view, String url, String message, final JsResult result) {
		if (message.startsWith("asAppJs://")) {
			if (message.endsWith("cssInjected")) {
				needsInjectCss = false;
			}
			result.confirm();
			return true;
		}
		return super.onClientJsAlert(view, url, message, result);
	}

	@Override
	public void onClientReceivedTitle(WebView view, String title) {
		injectCss();
	}

	// ---------------------------------------------------------
	// interface AdvancedWebView.Listener
	@Override
	public void onPageFinished(String url) {
		injectCss();
	}

	private void injectCss() {
		if (needsInjectCss) {
			String html = "javascript:(function() {";
			html += "var head = document.head || document.getElementsByTagName('head')[0];";
//			css += "alert('asAppJs://'+document.readyState); if ((head) && (document.readyState != 'uninitialized') && (document.readyState != 'complete')) { alert('asAppJs://cssInjected'); ";
			html += "alert('asAppJs://'+document.readyState); if (head) { alert('asAppJs://cssInjected'); ";
			html += WebViewFragment.getInjectHead();
			html += "}})();";
			webView.loadUrl(html);
		}
	}
}