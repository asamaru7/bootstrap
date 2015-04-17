package net.asamaru.bootstrap.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;

import com.orhanobut.logger.Logger;

import net.asamaru.bootstrap.Advisor;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

import java.io.IOException;
import java.util.Arrays;

@EFragment
public class WebViewAssetFragment extends WebViewFragment {
	@FragmentArg
	protected String path;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		WebSettings webSettings = webView.getSettings();
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

		String pathInAssets = "file:///android_asset/html/" + path;
		Logger.d(pathInAssets);
		try {
			if (Arrays.asList(getResources().getAssets().list("html")).contains(path)) {
				webView.loadUrl(pathInAssets);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			Advisor.showToast("파일을 찾을 수 없습니다.");
		}
		return webView;
	}
}