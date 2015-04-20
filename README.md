PdfBox-Android
==============

A port of Apache's PdfBox library to be usable on Android. Most features should be implemented by now. If you find any issues or want to request a feature that you need for your project, please use the issue tracker. The latest build can be downloaded as a jar from the root folder or built as an jar file with Gradle. These should be considered snapshots. They will have the latest features, but are prone to bugs.

The command for building the jar with Gradle is this or its equivalent in your operating system
```
./gradlew.bat clean build generateRelease
```

The main code of this project is licensed under the Apache 2.0 License, found at http://www.apache.org/licenses/LICENSE-2.0.html Code released under other licenses will be stated in the header.

#### Important notes:

-Based on PdfBox v1.8.9

-This is still a work in progress

-Unavailable classes, such as awt, are replaced with their closest Android approximation

#### Libraries:
SpongyCastle core, prov, and pkiv: https://github.com/rtyley/spongycastle/
