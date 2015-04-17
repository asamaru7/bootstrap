package net.asamaru.bootstrap.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import net.asamaru.bootstrap.Advisor;
import net.asamaru.bootstrap.R;
import net.asamaru.bootstrap.ui.SystemBarTintManager;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;

import java.util.ArrayList;

// http://androidhuman.tistory.com/524
// http://www.androidhive.info/2013/11/android-sliding-menu-using-navigation-drawer/
// LayerDrawable : http://stackoverflow.com/questions/20120725/layerdrawable-programatically

@EActivity
abstract public class ActionBarActivity extends android.support.v7.app.ActionBarActivity {
	@Bean
	protected Advisor advisor;

	protected CharSequence mTitle;

	protected SystemBarTintManager systemBarManager;
	protected ProgressBar mLoadingProgressBar;

	protected boolean useActionBarOverlay;
	protected boolean useStatusBarOverlay = false;
	protected boolean useNavigationBarOverlay = false;

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		Window win = this.getWindow();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			// check theme attrs
			int[] attrs = {android.R.attr.windowTranslucentStatus, android.R.attr.windowTranslucentNavigation};
			TypedArray a = this.obtainStyledAttributes(attrs);
			try {
				useStatusBarOverlay = a.getBoolean(0, false);
				useNavigationBarOverlay = a.getBoolean(1, false);
			} finally {
				a.recycle();
			}

			// check window flags
			WindowManager.LayoutParams winParams = win.getAttributes();
			int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			if ((winParams.flags & bits) != 0) {
				useStatusBarOverlay = true;
			}
			bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
			if ((winParams.flags & bits) != 0) {
				useNavigationBarOverlay = true;
			}
		}
		useActionBarOverlay = (useStatusBarOverlay || (win.hasFeature(Window.FEATURE_ACTION_BAR_OVERLAY)));

		systemBarManager = new SystemBarTintManager(this);
		systemBarManager.setStatusBarTintEnabled(true);
		systemBarManager.setNavigationBarTintEnabled(false);
		systemBarManager.setTintColor(getResources().getColor(R.color.StatusBar));

		// 하드웨어 가속
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

		// loading bar
		mLoadingProgressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, -1 * Advisor.dpToPixel(6), 0, 0);
		mLoadingProgressBar.setLayoutParams(params);
//		mLoadingProgressBar.setIndeterminate(true);
		mLoadingProgressBar.setMax(100);
		mLoadingProgressBar.setVisibility(View.INVISIBLE);
		((ViewGroup) getWindow().getDecorView()).addView(mLoadingProgressBar);
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		addActionBarOverlayPadding();
	}

	@Override
	public void setContentView(android.view.View view) {
		super.setContentView(view);
		addActionBarOverlayPadding();
	}

	@Override
	public void setContentView(android.view.View view, android.view.ViewGroup.LayoutParams params) {
		super.setContentView(view, params);
		addActionBarOverlayPadding();
	}

	private void addActionBarOverlayPadding() {
		// overlay에 대한 공간 확보
		int topPadding = systemBarManager.getConfig().getPixelInsetTop(true);
		if (topPadding > 0) {
			//ArrayList<View> views = getViewsByTag((ViewGroup) getWindow().getDecorView(), "b9TopPadding");
			ArrayList<View> views = getViewsByTag((ViewGroup) findViewById(android.R.id.content), "b9TopPadding");
			for (View v : views) {
				v.setPadding(v.getPaddingLeft(), v.getPaddingTop() + topPadding, v.getPaddingRight(), v.getPaddingBottom());
			}
			mLoadingProgressBar.setY(systemBarManager.getConfig().getPixelInsetTopWithStatusBar(true));
		}
	}

	private static ArrayList<View> getViewsByTag(ViewGroup root, String tag) {
		ArrayList<View> views = new ArrayList<>();
		final int childCount = root.getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View child = root.getChildAt(i);
			if (child instanceof ViewGroup) {
				views.addAll(getViewsByTag((ViewGroup) child, tag));
			}

			final Object tagObj = child.getTag();
			if (tagObj != null && tagObj.equals(tag)) {
				views.add(child);
			}

		}
		return views;
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	protected void showLoadingProgressBar() {
		mLoadingProgressBar.setVisibility(View.VISIBLE);
	}

	protected void updateLoadingProgressBar(int progress) {
		mLoadingProgressBar.setProgress(progress);
	}

	protected void hideLoadingProgressBar() {
		mLoadingProgressBar.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onBackPressed() {
		onBackPressed(false);
	}

	public void onBackPressed(boolean askClose) {
		if (askClose) {            // back 버튼 클릭시 앱 종료 확인
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("종료")
					.setMessage("앱을 종료하시겠습니까?")
//					.setMessage(Html.fromHtml("<strong><font color=\"#ff0000\"> " + "Html 표현여부 " + "</font></strong><br>HTML 이 제대로 표현되는지 본다."))
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}

					})
					.setNegativeButton("No", null)
					.show();
		} else {
			finish();
		}
	}
}