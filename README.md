PdfBox-Android
==============

A port of Apache's PdfBox library to be usable on Android. Most features should be implemented by now. If you find any issues or want to request a feature that you need for your project, please use the issue tracker. Stable releases can be added as a Gradle by declaring ``` com.tom_roush:pdfbox-android:1.8.9.1 ``` as a dependency with jcenter set as a repository. The latest releases can be built by cloning the repo and running Gradle. The tasks for building are set as default, so running ```gradlew.bat``` or ```./gradlew``` will take care of all necessary tasks. [The Bintray project is located here.](https://bintray.com/birdbrain2/PdfBox-Android/PdfBox-Android/view)

The main code of this project is licensed under the Apache 2.0 License, found at http://www.apache.org/licenses/LICENSE-2.0.html Code released under other licenses will be stated in the header.

#### Important notes:

-Based on PdfBox v1.8.9

-This is still a work in progress

-Unavailable classes, such as awt, are replaced with their closest Android approximation

#### Libraries:
SpongyCastle core, prov, and pkiv: https://github.com/rtyley/spongycastle/
