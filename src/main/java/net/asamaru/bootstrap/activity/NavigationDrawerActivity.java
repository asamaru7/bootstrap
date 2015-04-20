package net.asamaru.bootstrap.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.orhanobut.logger.Logger;

import net.asamaru.bootstrap.Advisor;
import net.asamaru.bootstrap.R;

import org.androidannotations.annotations.EActivity;

@EActivity
abstract public class NavigationDrawerActivity extends ActionBarActivity {
	DrawerLayout drawerLayout;
	FrameLayout fragmentContainer;
	ActionBarDrawerToggle drawerToggle;
	ListView drawerList;
	public final static int fragmentContainerId = Advisor.generateViewId();

	@Override
	protected void onResume() {
		super.onResume();

		resetDrawerView();
	}

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		drawerLayout = new DrawerLayout(this);
		fragmentContainer = new FrameLayout(this);
		fragmentContainer.setId(fragmentContainerId);
		drawerLayout.addView(fragmentContainer, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		resetDrawerView();

		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				invalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				invalidateOptionsMenu();
			}
		};
		drawerLayout.setDrawerListener(drawerToggle);

		setContentView(drawerLayout);
	}

	protected void resetDrawerView() {
		if (drawerList != null) {
			drawerLayout.removeView(drawerList);
		}
		drawerList = new ListView(this);
		DrawerLayout.LayoutParams lp = new DrawerLayout.LayoutParams(Advisor.dpToPixel(240), DrawerLayout.LayoutParams.MATCH_PARENT);
		lp.gravity = Gravity.START;
		Log.e("SSS", systemBarManager.getConfig().getPixelInsetTop() + "");
		lp.setMargins(0, systemBarManager.getConfig().getPixelInsetTop(), 0, 0);
		drawerList.setBackgroundColor(getResources().getColor(android.R.color.white));
		drawerList.setLayoutParams(lp);

		drawerList.setAdapter(getListAdapter());
		drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (onDrawerItemClick(parent, view, position, id)) {
					drawerLayout.closeDrawer(drawerList);
				}
			}
		});
		drawerLayout.addView(drawerList);
//		Logger.d("SSS");
	}

	abstract protected ListAdapter getListAdapter();

	abstract protected boolean onDrawerItemClick(AdapterView<?> parent, View view, int position, long id);

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		if (drawerToggle != null) {
			drawerToggle.syncState();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (drawerToggle != null) {
			drawerToggle.onConfigurationChanged(newConfig);
		}
	}

	public void replaceFragment(Class<?> fragmentClass) {
		if (fragmentClass != null) {
			try {
				replaceFragment((Fragment) fragmentClass.getConstructor().newInstance());
			} catch (Exception e) {
				Logger.e(e);
			}
		}
	}

	public void replaceFragment(Fragment fragment) {
		if (fragment != null) {
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(fragmentContainerId, fragment).commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {    // menu key가 onKeyDown으로 들어오지 않아 여기서 처리
			switch (event.getKeyCode()) {
				case KeyEvent.KEYCODE_MENU:
					if (drawerLayout != null) {
						if (drawerLayout.isDrawerOpen(drawerList)) {
							drawerLayout.closeDrawer(drawerList);
						} else {
							drawerLayout.openDrawer(drawerList);
						}
					}
					return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	final public void onBackPressed() {
		if ((drawerLayout != null) && (drawerLayout.isDrawerOpen(drawerList))) {
			drawerLayout.closeDrawer(drawerList);
			return;
		}
		super.onBackPressed();
	}
}