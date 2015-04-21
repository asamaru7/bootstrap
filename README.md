# bootstrap

## 프로젝트 초기세팅

### 신규 프로젝트 생성
android stuio에서 신규 프로젝트 생성
  
#### git 프로젝트 import
* VCS > Import into Version Control > Share Project on Github
* 아래의 항목은 추가시 제외

```
.idea
gradle/
gradlew
gradlew.bat
```

* shell에서 .gitignore를 편집 : 아래의 내용을 추가

```
	vi .gitignore 
```

```
.idea/
gradle/
gradlew
gradlew.bat
```

* 변경 사항 반영

```
	git add .gitignore 
	git status
```

#### 라이브러리 연동

* 모듈 추가

```
git submodule add https://github.com/asamaru7/bootstrap externals/bootstrap
```

* settings.gradle 파일 수정

```gradle
include ':app', ':externals:bootstrap'
```

* app/build.gradle 파일 수정

```gradle
// --------------------------------------------------------
// bootstrap
// --------------------------------------------------------
buildscript {
    repositories { mavenCentral() }
    dependencies { classpath 'com.neenbedankt.gradle.plugins:android-apt:1.4' }
}
apply from: '../externals/bootstrap/androidannotation.gradle'
dependencies {
    compile project(':externals:bootstrap')
}
// --------------------------------------------------------
```

#### 프로젝트 시작

* Application 객체를 생성하고 아래와 같이 수정

```java
import org.androidannotations.annotations.EApplication;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(
	formKey = "", // This is required for backward compatibility but not used
	formUri = "tracker url"
)
@EApplication
public class Application extends net.asamaru.bootstrap.Application {
}
```

* AndroidManifest.xml 파일에 Application 연결

```java
    <application
        android:name=".Application_"
        ...
```

* make
