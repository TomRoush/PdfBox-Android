package com.tom_roush.pdfbox.pdmodel.common;

/**
*
* @author adam
*/
public class XrefEntry {
   private int objectNumber = 0;
   private int byteOffset = 0;
   private int generation = 0;
   private boolean inUse = true;

   public XrefEntry() {
   }

   public XrefEntry(int objectNumber, int byteOffset, int generation, String inUse) {
       this.objectNumber = objectNumber;
       this.byteOffset = byteOffset;
       this.generation = generation;
       this.inUse = "n".equals(inUse);
   }

   public int getByteOffset() {
       return byteOffset;
   }
}
