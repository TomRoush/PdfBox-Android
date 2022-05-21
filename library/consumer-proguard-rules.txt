# PdfBox-Android does reflection to instantiate SecurityHandlers.
-keep,allowobfuscation class * extends com.tom_roush.pdfbox.pdmodel.encryption.SecurityHandler {
   public <init>(...);
}
