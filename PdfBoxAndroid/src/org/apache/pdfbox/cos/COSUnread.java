package org.apache.pdfbox.cos;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdfparser.ConformingPDFParser;

/**
*
* @author adam
*/
public class COSUnread extends COSBase {
   private long objectNumber;
   private long generation;
   private ConformingPDFParser parser;

   public COSUnread() {
       super();
   }

   public COSUnread(long objectNumber, long generation) {
       this();
       this.objectNumber = objectNumber;
       this.generation = generation;
   }

   public COSUnread(long objectNumber, long generation, ConformingPDFParser parser) {
       this(objectNumber, generation);
       this.parser = parser;
   }

   @Override
   public Object accept(ICOSVisitor visitor) throws COSVisitorException {
       // TODO: read the object using the parser (if available) and visit that object
       throw new UnsupportedOperationException("COSUnread can not be written/visited.");
   }

   @Override
   public String toString() {
       return "COSUnread{" + objectNumber + "," + generation + "}";
   }

   /**
    * @return the objectNumber
    */
   public long getObjectNumber() {
       return objectNumber;
   }

   /**
    * @param objectNumber the objectNumber to set
    */
   public void setObjectNumber(long objectNumber) {
       this.objectNumber = objectNumber;
   }

   /**
    * @return the generation
    */
   public long getGeneration() {
       return generation;
   }

   /**
    * @param generation the generation to set
    */
   public void setGeneration(long generation) {
       this.generation = generation;
   }

   /**
    * @return the parser
    */
   public ConformingPDFParser getParser() {
       return parser;
   }

   /**
    * @param parser the parser to set
    */
   public void setParser(ConformingPDFParser parser) {
       this.parser = parser;
   }

}
