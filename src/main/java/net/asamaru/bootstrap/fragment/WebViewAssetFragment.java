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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EFragment
public class WebViewAssetFragment extends WebViewFragment {
	final static String header = "<!DOCTYPE html>\n" +
			"<html lang=\"ko\">\n" +
			"<head>\n" +
			"    <meta charset=\"utf-8\">\n" +
			"    <meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\"/>\n" +
			"    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
//			"    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
			"    <meta name=\"viewport\" content=\"width=320, user-scalable=no, initial-scale=1, maximum-scale=2, minimum-scale=1\">\n" +
			"    <title></title>\n" +
			"    <script type=\"text/javascript\">(function() { var head = document.head || document.getElementsByTagName('head')[0]; " + WebViewFragment.getInjectHead() + " })();</script>" +
			"    <link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"res/bootstrap3/css/bootstrap.min.css\" />\n" +
			"    <link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"res/common/base.css\" />\n" +
			"<!--</head><body>-->";
	final static String footer = "<script src=\"res/jquery/jquery-2.1.3.min.js\"></script>\n" +
			"<script type=\"text/javascript\" src=\"res/bootstrap3/js/bootstrap.min.js\"></script>\n" +
			"<script type=\"text/javascript\" src=\"res/common/base.js\"></script>\n" +
			"<!--</body></html>-->";

	@FragmentArg
	protected String path;
	protected List<String> js = new ArrayList<>();
	protected List<String> css = new ArrayList<>();

	public void addJs(String path) {
		js.add(path);
	}

	public void addJs(String[] path) {
		if ((path != null) && (path.length > 0)) {
			js.addAll(new ArrayList<>(Arrays.asList(path)));
		}
	}

	public void addCss(String path) {
		css.add(path);
	}

	public void addCss(String[] path) {
		if ((path != null) && (path.length > 0)) {
			css.addAll(new ArrayList<>(Arrays.asList(path)));
		}
	}

	protected String getHeader() {
		if (css.size() <= 0) {
			return header;
		}
		String headerHtml = header;
		for (String cssPath : css) {
			headerHtml += "<link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"" + cssPath + "\" />\n";
		}
		return headerHtml;
	}

	protected String getFooter() {
		if (js.size() <= 0) {
			return footer;
		}
		String footerHtml = footer;
		for (String jsPath : js) {
			footerHtml += "<script type=\"text/javascript\" src=\"" + jsPath + "\"></script>\n";
		}
		return footerHtml;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		WebSettings webSettings = webView.getSettings();
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

		loadHtml();

		return webView;
	}

	protected void loadHtml() {
//		Logger.d("file:///android_asset/html/" + path);
		try {
			if (Arrays.asList(getResources().getAssets().list("html")).contains(path)) {
//				webView.loadUrl(pathInAssets);
				String html = getHeader();
				html += "</head><body>";
				html += readText("html/" + path);
				html += getFooter();
				html += "</body></html>";
				webView.loadDataWithBaseURL("file:///android_asset/html/", html, "text/html", "UTF-8", null);
				Logger.d("Load html : " + html);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			Advisor.showToast("파일을 찾을 수 없습니다.");
		}
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	protected String readText(String file) throws IOException {
		InputStream is = getResources().getAssets().open(file);
		byte[] buffer = new byte[is.available()];
		is.read(buffer);
		is.close();
		return new String(buffer);
	}

	public void reloadHtml() {
		loadHtml();
	}
}