package org.apache.pdfbox.util;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class PDFBoxResourceLoader {
	/**
	 * The Context of the main app
	 */
	private static Context CONTEXT = null;
	
	/**
	 * The AssetManager used to load the resources
	 */
    private static AssetManager ASSET_MANAGER = null;
    
    /**
     * Whether an uninitialized warning has already been given
     */
    private static boolean hasWarned = false;

    /**
     * Initializes the loader
     * 
     * @param context the context of the main app
     */
    public static void init(Context context){
        if(CONTEXT == null) {
            CONTEXT = context.getApplicationContext();
            ASSET_MANAGER = CONTEXT.getAssets();
        }
    }
    
    /**
     * Checks whether the loader has been initialized
     * 
     * @return whether the loader has been initialized or not
     */
    public static boolean isReady() {
    	if(ASSET_MANAGER == null && !hasWarned) {
    		Log.w("PdfBoxAndroid", "Call PDFBoxResourceLoader.init() first to decrease resource load time");
    		hasWarned = true;
    	}
    	return ASSET_MANAGER != null;
    }

    /**
     * Loads a resource file located in the assets folder
     * 
     * @param path the path to the resource
     * @return the resource as an InputStream
     * @throws IOException if the resource cannot be found
     */
    public static InputStream getStream(String path) throws IOException {
        return ASSET_MANAGER.open(path);
    }
}
