package net.asamaru.bootstrap.fragment;

import net.asamaru.bootstrap.Advisor;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;

@EFragment
public class Fragment extends android.support.v4.app.Fragment {
	@Bean
	protected Advisor advisor;

	@Override
	public void onAttach(android.app.Activity activity) {
		super.onAttach(activity);

//		if (activity instanceof Activity) {
//			activity.getActionBar().setTitle();
//		}
	}
}