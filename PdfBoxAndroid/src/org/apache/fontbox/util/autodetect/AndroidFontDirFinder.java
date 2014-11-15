package org.apache.fontbox.util.autodetect;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AndroidFontDirFinder extends NativeFontDirFinder {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> getCommonTTFMapping() {
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("TimesNewRoman,BoldItalic","DroidSerif-BoldItalic");
		map.put("TimesNewRoman,Bold","DroidSerif-Bold");
		map.put("TimesNewRoman,Italic","DroidSerif-Italic");
		map.put("TimesNewRoman","DroidSerif-Regular");

		map.put("Arial,BoldItalic","Roboto-BoldItalic");
		map.put("Arial,Italic","Roboto-Italic");
		map.put("Arial,Bold","Roboto-Bold");
		map.put("Arial","Roboto-Regular");

		map.put("Courier,BoldItalic","DroidSansMono");
		map.put("Courier,Italic","DroidSansMono");
		map.put("Courier,Bold","DroidSansMono");
		map.put("Courier","DroidSansMono");

		map.put("Symbol", "OpenSymbol");
		map.put("ZapfDingbats", "Dingbats");
		return Collections.unmodifiableMap(map);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String[] getSearchableDirectories() {
		return new String[] {
				"/system/fonts"
				// Shouldn't be any other directories, but they can be added here
		};
	}

}
