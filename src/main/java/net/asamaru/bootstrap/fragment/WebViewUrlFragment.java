package net.asamaru.bootstrap.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

@EFragment
public class WebViewUrlFragment extends WebViewFragment {
	@FragmentArg
	protected String url;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		webView.loadUrl(url);
		return webView;
	}

	public void reloadHtml() {
		webView.reload();
	}
}