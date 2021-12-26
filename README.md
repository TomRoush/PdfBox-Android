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
    implementation 'com.tom-roush:pdfbox-android:2.0.11.0'
}
```

Before calls to PDFBox are made it is required to initialize the library's resource loader. Add the following line before calling PDFBox methods:

```java
PDFBoxResourceLoader.init(getApplicationContext());
```

An example app is located in the `sample` directory and includes examples of common tasks.

Important notes
==============

-Currently based on PdfBox v2.0.11

-Requires API 19 or greater for full functionality
