package net.asamaru.bootstrap.activity;

import android.app.FragmentTransaction;

import net.asamaru.bootstrap.fragment.WebViewAssetFragment_;
import net.asamaru.bootstrap.fragment.WebViewFragment;
import net.asamaru.bootstrap.fragment.WebViewUrlFragment_;

import org.androidannotations.annotations.EActivity;

@EActivity
abstract public class HtmlActivity extends ActionBarActivity {
	protected String getAssetPath() {
		return null;
	}

	protected String getUrl() {
		return null;
	}

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			String target = getUrl();
			WebViewFragment fragment;
			if (target != null) {
				fragment = WebViewUrlFragment_.builder().url(target).build();
			} else {
				target = getAssetPath();
				fragment = WebViewAssetFragment_.builder().path(target).build();
			}
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.add(android.R.id.content, fragment).commit();
		}
	}
}