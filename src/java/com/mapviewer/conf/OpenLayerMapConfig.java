/*
* Copyright (c) 2013 Olmo Zavala
* Permission is hereby granted, free of charge, to any person obtaining a copy of 
* this software and associated documentation files (the "Software"), to deal in the 
* Software without restriction, including without limitation the rights to use, copy, 
* modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and 
* to permit persons to whom the Software is furnished to do so, subject to the following conditions: 
* The above copyright notice and this permission notice shall be included in all copies or substantial 
* portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
* PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
* FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, 
* ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
*/
package com.mapviewer.conf;

import com.mapviewer.tools.StringAndNumbers;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

/**
 * This SINGLETON class represent the OpenLayer configuration of the map. For
 * example, it contains the maximun and minimum resolution of the map, the
 * default center and zoom and the boundary box of the map.
 *
 * @author Olmo Zavala Romero
 */
public class OpenLayerMapConfig {
	
	private String center = null;
	private String zoom = null;
	private double maxResolution = 0.05;//Controls the maximum resolution of the map
	private double minResolution = 0.01;//Controls the minimum resolution of the map
	private String restrictedExtent = "-180.0,-90.0,180.0,90.0";//Restricts the extent of the map
	private String mapBounds="-180.0,-90.0,180.0,90.0";
	String[] tilesOrigin;
	//This contains the properties used on JavaScript, which are not the same
	//as the attributes. 
	private Properties prop;
	private String mapProj;
	private static OpenLayerMapConfig instance;//Singleton
	
	protected OpenLayerMapConfig() {
	}
	
	public static synchronized OpenLayerMapConfig getInstance() {
		if (instance == null) {//Only the first time we initialize
			instance = new OpenLayerMapConfig();
		}
		return instance;
	}
	
	/**
	 * This function creates a JSON text with the information of each of the
	 * variables. This text is used by JSP files
	 * @return json string
	 */
	public String toJSONObject() {
		String jsonText = "{";
		
		Enumeration e = prop.propertyNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = prop.getProperty(key);
			if(StringAndNumbers.isNumeric(value)){
				jsonText += "'"+key+"':"+ prop.getProperty(key)+",";
			} else{
				jsonText += "'"+key+"':'"+ prop.getProperty(key)+"',";
			}
		}
		jsonText = jsonText.substring(0, jsonText.length()-1);
		jsonText +="}";
		return jsonText;
	}

	/**
	 * This function is used to update one of the properties externally. Most
	 * of the properties in this class should remain the same at all times because they are 
	 * the same for all users.
	 * @param key
	 * @param value 
	 */
	public void updateProperty(String key, String value){
		prop.setProperty(key, value);
	}

	/**
	 * Reads an specific property
	 * @param key 
	 */
	public void readProperty(String key){
		prop.getProperty(key);
	}
	
	/**
	 *Read the property file.
	 *@param{InputStream} stream
	 */
	public void updateProperties(InputStream stream) {
		
		if (prop == null) {//It will only reload the properties file if is the first time
			try {
				prop = new Properties();
				prop.load(stream);//Loads all the properties from the Properties file
				//This property gets initialized when reading the XML files or 
				// by directly in JavaScript
				prop.setProperty("mapProjection", "");
				initializeVariables();
			} catch (IOException ex) {
				System.out.println("EXCEPTION can't load the properties file" + ex.getMessage());
			}
		}
	}
	
	/**
	 * Search a key inside the properties file and returns its value
	 *
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		return prop.getProperty(key);
	}
	
	/**
	 * Initializes the required variables for the display of the map
	 */
	public void initializeVariables() {
		
		this.setTilesOriginFromStr(getProperty("tilesOrigin"));
		center = getProperty("mapcenter");
		zoom = getProperty("zoom");
		minResolution = Float.parseFloat(getProperty("minResolution"));
		maxResolution = Float.parseFloat(getProperty("maxResolution"));
		restrictedExtent = getProperty("maxExtent");
		mapBounds=getProperty("mapBounds");
		mapProj=getProperty("mapProjection");
	}
	
	public String getCenter() {
		return center;
	}
	
	public void setCenter(String center) {
		this.center = center;
	}
	
	public String getZoom() {
		return zoom;
	}
	
	public void setZoom(String zoom) {
		this.zoom = zoom;
	}
	
	public String[] getTilesOrigin() {
		return tilesOrigin;
	}
	
	public void setTilesOrigin(String[] tilesOrigin) {
		this.tilesOrigin = tilesOrigin;
	}
	
	public void setTilesOriginFromStr(String tilesOriginStr) {
		String[] localTilesOrigin = tilesOriginStr.split(",");
		setTilesOrigin(localTilesOrigin);
	}
	
	public double getMaxResolution() {
		return maxResolution;
	}
	
	public void setMaxResolution(double maxResolution) {
		this.maxResolution = maxResolution;
	}
	
	public double getMinResolution() {
		return minResolution;
	}
	
	public void setMinResolution(double minResolution) {
		this.minResolution = minResolution;
	}
	
	public String getRestrictedExtent() {
		return restrictedExtent;
	}
	public void setRestrictedExtent(String restrictedExtent) {
		this.restrictedExtent = restrictedExtent;
	}
	public String getmapBounds() {
		return mapBounds;
	}
	public void setmapBounds(String mapBounds) {
		this.mapBounds=mapBounds;
	}
	
	public String getMapProjection() {
		return mapProj;
	}
	
	public void setMapProjection(String mapProj) {
		this.mapProj = mapProj;
	}
}
