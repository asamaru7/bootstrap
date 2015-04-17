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
import java.io.InputStream;
import java.util.Arrays;

@EFragment
public class WebViewAssetFragment extends WebViewFragment {
	final static String header = "<!DOCTYPE html>\n" +
			"<html lang=\"ko\">\n" +
			"<head>\n" +
			"    <meta charset=\"utf-8\">\n" +
			"    <meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\"/>\n" +
			"    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
			"    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
			"    <title></title>\n" +
			"    <link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"res/bootstrap3/css/bootstrap.min.css\" />\n" +
			"    <link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"res/common/base.css\" />\n" +
			"<!--</head><body>-->";
	final static String footer = "<script src=\"res/jquery/jquery-2.1.3.min.js\"></script>\n" +
			"<script src=\"res/bootstrap3/js/bootstrap.min.js\"></script>\n" +
			"<script src=\"res/common/base.js\"></script>\n" +
			"<!--</body></html>-->";

	@FragmentArg
	protected String path;

	protected String getHeader() {
		return header;
	}

	protected String getFooter() {
		return footer;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		WebSettings webSettings = webView.getSettings();
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

		String pathInAssets = "file:///android_asset/html/" + path;
		Logger.d(pathInAssets);
		try {
			if (Arrays.asList(getResources().getAssets().list("html")).contains(path)) {
//				webView.loadUrl(pathInAssets);
				String html = getHeader();
				html += "</head><body>";
				html += readText("html/" + path);
				html += getFooter();
				html += "</body></html>";
				webView.loadDataWithBaseURL("file:///android_asset/html/", html, "text/html", "UTF-8", null);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			Advisor.showToast("파일을 찾을 수 없습니다.");
		}
		return webView;
	}

	private String readText(String file) throws IOException {
		InputStream is = getResources().getAssets().open(file);

		int size = is.available();
		byte[] buffer = new byte[size];
		is.read(buffer);
		is.close();

		return new String(buffer);
	}
}