PdfBox-Android
==============
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.tom-roush/pdfbox-android/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.tom-roush/pdfbox-android/)
[![Build Status](https://github.com/TomRoush/PdfBox-Android/actions/workflows/android-ci.yml/badge.svg?branch=master)](https://github.com/TomRoush/PdfBox-Android/actions)

A port of Apache's PdfBox library to be usable on Android. Most features should be implemented by now. Feature requests can be added to the issue tracker. Stable releases can be added as a Gradle dependency from Maven Central.

The main code of this project is licensed under the Apache 2.0 License, found at http://www.apache.org/licenses/LICENSE-2.0.html

Usage
==============

Add the following to dependency to `build.gradle`:

```gradle
dependencies {
    implementation 'com.tom-roush:pdfbox-android:2.0.27.0'
}
```

Before calls to PDFBox are made it is required to initialize the library's resource loader. Add the following line before calling PDFBox methods:

```java
PDFBoxResourceLoader.init(getApplicationContext());
```

An example app is located in the `sample` directory and includes examples of common tasks.

Optional Dependencies
==============

PdfBox-Android can optionally make use of additional features provided by third-party libraries. These libraries are not included by default to reduce the size of the PdfBox-Android. See the `dependencies` section in the`build.gradle` of the Sample project for examples of including the optional dependencies.

Reading JPX Images
-------------

Android does not come with native support for handling JPX images. These images can be read using the [JP2Android library](https://github.com/ThalesGroup/JP2ForAndroid). As JPX is not a common image format, this library is not included with PdfBox-Android by default. If the JP2Android library is not on the classpath of your application, JPX images will be ignored and a warning will be logged.

To include the JP2Android library, add the following to your project's Gradle `dependencies` section. Note that this library is available in JCenter only, so you will need to add `jcenter()` to your repository list.
```gradle
dependencies {
    implementation 'com.gemalto.jp2:jp2-android:1.0.3'
}
```

Important notes
==============

* Currently based on PDFBox v2.0.27

* Requires API 19 or greater for full functionality
