package net.asamaru.bootstrap;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import org.acra.ACRA;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

//@EApplication	// abstract application을 현재는 지원하지 않음 3.2, 이후 버전에서 테스트가 필요함
@EBean
abstract public class Application extends android.app.Application {
	@Bean
	protected Advisor advisor;

	@Override
	public void onCreate() {
		super.onCreate();

		//if (2 != (this.getApplicationContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
		if (!BuildConfig.DEBUG) {
//		if (!Advisor.isDebugable()) {
			ACRA.init(this); // The following line triggers the initialization of ACRA
			Logger.init().setLogLevel(LogLevel.NONE);  // default : LogLevel.FULL
		} else {
			Logger.init("XDEV")               // default tag : PRETTYLOGGER or use just init()
					.setMethodCount(3)            // default 2
					.hideThreadInfo()             // default it is shown
					.setLogLevel(LogLevel.FULL);  // default : LogLevel.FULL
		}

		advisor.setApp(this);    // advisor에 app 세팅
	}
}
