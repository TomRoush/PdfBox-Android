package org.apache.pdfbox.pdmodel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.ConformingPDFParser;
import org.apache.pdfbox.persistence.util.COSObjectKey;

/**
 *
 * @author adam
 */
public class ConformingPDDocument extends PDDocument {
    /**
     * Maps ObjectKeys to a COSObject. Note that references to these objects
     * are also stored in COSDictionary objects that map a name to a specific object.
     */
    private final Map<COSObjectKey, COSBase> objectPool =
        new HashMap<COSObjectKey, COSBase>();
    private ConformingPDFParser parser = null;

    public ConformingPDDocument() throws IOException {
        super();
    }

    public ConformingPDDocument(COSDocument doc) throws IOException {
        super(doc);
    }

    /**
     * This will load a document from an input stream.
     * @param input The File which contains the document.
     * @return The document that was loaded.
     * @throws IOException If there is an error reading from the stream.
     */
    public static PDDocument load(File input) throws IOException {
        ConformingPDFParser parser = new ConformingPDFParser(input);
        parser.parse();
        return parser.getPDDocument();
    }

    /**
     * This will get an object from the pool.
     * @param key The object key.
     * @return The object in the pool or a new one if it has not been parsed yet.
     * @throws IOException If there is an error getting the proxy object.
     */
    public COSBase getObjectFromPool(COSObjectKey key) throws IOException {
        return objectPool.get(key);
    }

    /**
     * This will get an object from the pool.
     * @return The object in the pool or a new one if it has not been parsed yet.
     * @throws IOException If there is an error getting the proxy object.
     */
    public List<COSObjectKey> getObjectKeysFromPool() throws IOException {
        List<COSObjectKey> keys = new ArrayList<COSObjectKey>();
        for(COSObjectKey key : objectPool.keySet())
            keys.add(key);
        return keys;
    }

    /**
     * This will get an object from the pool.
     * @param number the object number
     * @param generation the generation of this object you wish to load
     * @return The object in the pool
     * @throws IOException If there is an error getting the proxy object.
     */
    public COSBase getObjectFromPool(long number, long generation) throws IOException {
        return objectPool.get(new COSObjectKey(number, generation));
    }

    public void putObjectInPool(COSBase object, long number, long generation) {
        objectPool.put(new COSObjectKey(number, generation), object);
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
