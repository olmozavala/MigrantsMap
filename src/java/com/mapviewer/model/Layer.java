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
package com.mapviewer.model;

import com.mapviewer.model.menu.MenuEntry;
import com.mapviewer.tools.StringAndNumbers;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 * This class represent a layer of the map either raster or vector type
 */
public class Layer {

	private BoundaryBox bbox;//Boundary limits of the layer
	private String style;//Style of the layer different from the default of the map server
	private String format;//Image format of the request. ('image/jpg' or 'image/png')
	private String name;//name of the layer in the server itself
	private Map<String, String> layerDisplayNames;//Array of names to display for layer (different languages)
	private String projection;//Is the projection of the layer commonly 'EPSG:4326'
	private String server;
	//This layer helps to display data from other layer. For example we can
	//show an RGB map and obtain data from other layer
	private String featureInfoLayer;
	private boolean displayTitle;//This variable indicate if we whould display the name of the layer
	private String layout;//indicates if the layer should load with additional information. 
	private boolean vectorLayer;//check to see if it a vector layer type
	private boolean tiled = false;//Indicates if the layer should be displayed has tiled or as a single image
	private String[] tilesOrigin = {"0", "0"};//Latitude and longitude where the tiles of the image start.
	private MenuEntry[] idLayer;//Array of menus that this layer represent
	private int width;//Number of columns that this layer has. This property is very important
	//because affects the way WCS request the layers. It is also used to define the
	// resolution of the animation for netCDF layers. It is not used to request normal maps,
	// for this case the size of the browser window is used and required.
	private int height;//Number of rows that this layer has. This property is very important
	//becaouse affects the way WCS request the layers.It is also used to define the
	// resolution of the animation for netCDF layers. It is not used to request normal maps,
	// for this case the size of the browser window is used and required.
	private String[] tituloCapasDatos;//titles of the layers data
	private boolean netCDF;//Indicates if the layer is a netCDF (contains temporal data)
	private JSONObject layerDetails;
	private String palette;
	private float minColor;//Minimum color used for the palette colors
	private float maxColor;//Max color used for the palette colors
	//Used for the optional layers to define if it is selected by default.
	//For the 'base layers' it is used to force one specific menu being selected
	private boolean selected;
	//Define the transition effect used by OpenLayers when zoom is used.
	// Currently it can be 'none' or 'resize' (default)
	private String transEffect;
	private String maxTimeLayer; //it defines the maximum time range the user can select (week, month, year)
	//------- CQL
	private String cql;
	private String cql_cols;
	private boolean jsonp;//Itentifies if the layer is a json layer (dynamic vector layer)

	/**
	 * Verify that the input MenuEntry correspond to this layer
	 *
	 * @param {MenuEntry} selectedIndex MenuEntry[]Array that has the optons of the wanted
	 * menus
	 * @return boolean depending if the slected index has the menu entry.
	 */
	public boolean isThisLayer(MenuEntry[] selectedIndex) {
		boolean thisIsTheLayer = true;
		if (selectedIndex.length == 0) {
			thisIsTheLayer = false;
		}
		for (int nivelMenu = 0; nivelMenu < idLayer.length; nivelMenu++) {

			if (!idLayer[nivelMenu].getId().equals(selectedIndex[nivelMenu].getId())) {
				return false;
			}
		}
		return thisIsTheLayer;
	}

	public boolean isThisLayer(String[] selectedIndex) {
		boolean thisIsTheLayer = true;
		if (selectedIndex.length == 0) {
			thisIsTheLayer = false;
		}
		for (int nivelMenu = 0; nivelMenu < idLayer.length; nivelMenu++) {

			if (!idLayer[nivelMenu].getId().equals(selectedIndex[nivelMenu])) {
				return false;
			}
		}
		return thisIsTheLayer;
	}
	/**
	 *
	 * * In this constructor we specified all the properties
	 *
	 * @param {BoundryBox} bbox BoundaryBox Geographics limits of the layer
	 * @param {String} style String Style name of the layer. Only if the default style of
	 * the layer is different
	 * @param {String} format String Image format
	 * @param {String}name String Name of the layer
	 * @param {HashMap}layerDisplayNames HasMap<Sring,String> Title of the layer that
	 * should be displayed on the map for different languages
	 * @param {String} projection String Projection of the layer, example EPSG:4223
	 * @param {MenuEntry}idLayer MenuEntry[] Menus that this layer represent
	 * @param {String}server String Server where this layer its stored
	 * @param {int}width int Number of columns of the layers (if it is raster layer)
	 * @param {int}height int Number of rows of the layer (if it is raster layer)
	 * @param {String}featureInfoLayer String Name of the layer where the specific request
	 * are made. (When the user clicks on the map)
	 * @param {boolean}isTiled boolean Indicates if the layers should be displayed as
	 * tiled
	 * @param {String[2]}tilesOrigin String[2] Lon,Lat Of the star of the tiles (if this
	 * layer is displayed tiled)
	 * @param {boolean} displayTitle boolean Indicates if the title of this layer should
	 * be displayed (This is used in the optional layers, checkboxes, when there is one
	 * layer above the other)
	 * @param {String} layout String Indicates if the layer has a specific layout
	 * @param {boolean} vectorLayer boolean Indicates if this is a vector layer
	 * @param {boolean} netCDF boolean Indicates if the layer is a netCDF (contains
	 * temporal data)
	 * @param {String} maxTimeLayer string that defines the maximum time range the user
	 * can select (week, month, year)
	 * @param {boolan} jsonp Boolean value that indicates if the layer is a dynamic
	 * vector layer served using jsonp
	 */
	public Layer(BoundaryBox bbox,
			String style,
			String format,
			String layerName,
			Map<String, String> layerDisplayNames,
			String projection,
			MenuEntry[] idLayer,
			String serverName,
			int width,
			int height,
			String featureInfoLayer,
			boolean isTiled,
			String[] tilesOrigin,
			boolean displayTitle,
			String layout,
			boolean vectorLayer,
			String palette,
			boolean netCDF, String maxTimeLayer, boolean jsonp) {

		this.bbox = bbox;
		this.style = style;
		this.format = format;
		this.name = layerName;
		this.layerDisplayNames = layerDisplayNames;
		this.projection = projection;
		this.idLayer = idLayer;
		this.server = serverName;
		this.width = width;
		this.height = height;
		this.featureInfoLayer = featureInfoLayer;
		this.tiled = isTiled;
		this.tilesOrigin = tilesOrigin;
		this.displayTitle = displayTitle;
		this.layout = layout;
		this.vectorLayer = vectorLayer;
		this.tituloCapasDatos = null;
		this.netCDF = netCDF;
		this.palette = palette;
		this.selected = false;//By default none of the optional layers is selected
		this.transEffect = "resize";//By default we use the 'resize' effect when zooming 
		this.jsonp = jsonp;

		// Default min and max color is -1
		// they have to be modified by external getter and setter. 
		this.minColor = -1;
		this.maxColor = -1;
		this.maxTimeLayer = maxTimeLayer;

		// By default the layer doesn't have any cql parameter
		this.cql = "";
		this.cql_cols = "";// By default the layers doe not have cql columns
	}
	//Geters    

	public MenuEntry[] getIdLayer() {
		return idLayer;
	}

	/**
	 * This function adds the layer details but also verifies that the min and max color
	 * values don't be -1. If they are then it replaces them by the default color range of
	 * the layer.
	 *
	 * @param {String} layerDetails
	 */
	public void setLayerDetails(String layerDetailsStr) {

		// In this case we have to update the min and max color with the
		// default color range of the layer. 
		// We add the 'server' and the 'name' into the layer Details
		try {
			JSONObject layDet;
			if (layerDetailsStr.equals("")) {
				this.layerDetails = new JSONObject();
			} else {
				this.layerDetails = new JSONObject(layerDetailsStr);
				if ((this.minColor == -1) && (this.maxColor == -1)) {
					this.minColor = Float.parseFloat((String) (((JSONArray) layerDetails.get("scaleRange")).get(0)));
					this.maxColor = Float.parseFloat((String) (((JSONArray) layerDetails.get("scaleRange")).get(1)));
				}
			}

			layerDetails.accumulate("server", server);
			layerDetails.accumulate("name", name);
			layerDetails.accumulate("srs", this.getProjection());
			layerDetails.accumulate("bbox", this.getBbox().toString());
			
			String layerType = "raster"; //By default all layer are  raster

			if(this.isVectorLayer()){ layerType =  "vector"; }
			if(this.isNetCDF()){ layerType = "ncwms"; }

			layerDetails.accumulate("layerType", layerType); 

		} catch (JSONException ex) {
			System.out.println("ERROR: The layerdetails JSON object can't be created on Layer class");
		}
	}

	public String getLayerDetails() {
		return layerDetails.toString();
	}

	public BoundaryBox getBbox() {
		return bbox;
	}

	public String getFormat() {
		return format;
	}

	public String getServer() {
		return server;
	}

	public String getName() {
		return name;
	}

	public String getProjection() {
		return projection;
	}

	public String getStyle() {
		return style;
	}

	public String getFeatureInfoLayer() {
		return featureInfoLayer;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public boolean isTiled() {
		return tiled;
	}

	public boolean isConfGeoWebCache() {
		return false;
	}

	public String getTilesOrigin() {
		return StringAndNumbers.arrregloSeparadoPorComas(tilesOrigin);
	}

	public String[] getTilesOriginArr() {
		return tilesOrigin;
	}

	public String getDisplayName(String language) {
		if (layerDisplayNames == null) {
			return "Title not defined for layer:" + this.getName();
		} else {
			String txt = layerDisplayNames.get(language);
			//If the node doesn't have the requiered language then we
			// return it in english
			if (txt == null) {
				txt = layerDisplayNames.get("EN");
			}
			if (txt == null) {
				//TODO Send an exception that the language 'EN' is not found
				System.out.println("ERROR: the title in language EN is not defined for layer:" + this.getName());
			}
			return txt;
		}
	}

	public Map<String, String> getDisplayNames() {
		return layerDisplayNames;
	}

	public boolean isDisplayTitle() {
		return displayTitle;
	}

	public Map<String, String> getLayerDisplayNames() {
		return layerDisplayNames;
	}

	public String[] getTilesOriginArray() {
		return tilesOrigin;
	}

	public boolean getTitle() {
		return displayTitle;
	}

	public String getLayout() {
		return layout;
	}

	public boolean isVectorLayer() {
		return vectorLayer;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}
	//Seters

	public void setBbox(BoundaryBox val) {
		this.bbox = val;
	}

	public void setStyle(String val) {
		this.style = val;
	}

	public void setServer(String serverName) {
		this.server = serverName;
	}

	public void setFormat(String val) {
		this.format = val;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setProjection(String val) {
		this.projection = val;
	}

	public void setFeatureInfoLayer(String featureInfoLayer) {
		this.featureInfoLayer = featureInfoLayer;
	}

	public void setName(String val) {
		this.name = val;
	}

	public void setAddDisplayName(String text, String language) {
		layerDisplayNames.put(language, text);
	}

	public void setDisplayNames(Map<String, String> layerDisplayNames) {
		this.layerDisplayNames = layerDisplayNames;
	}

	public void setDisplayTitle(boolean displayTitle) {
		this.displayTitle = displayTitle;
	}

	public void setIdLayer(MenuEntry[] idLayer) {
		this.idLayer = idLayer;
	}

	public void setTiled(boolean tiled) {
		this.tiled = tiled;
	}

	public void setTilesOrigin(String[] tilesOrigin) {
		this.tilesOrigin = tilesOrigin;
	}

	public void setTitle(boolean displayTitle) {
		this.displayTitle = displayTitle;
	}

	public void setIsVectorLayer(boolean isVectorLayer) {
		this.vectorLayer = isVectorLayer;
	}

	public String[] getTituloCapasDatos() {
		return tituloCapasDatos;
	}

	public void setTituloCapasDatos(String[] tituloCapasDatos) {
		this.tituloCapasDatos = tituloCapasDatos;
	}

	public boolean isNetCDF() {
		return netCDF;
	}

	public void setNetCDF(boolean netCDF) {
		this.netCDF = netCDF;
	}

	public String getPalette() {
		return palette;
	}

	public void setPalette(String palette) {
		this.palette = palette;
	}

	public float getMaxColor() {
		return maxColor;
	}

	public void setMaxColor(float maxColor) {
		this.maxColor = maxColor;
	}

	public float getMinColor() {
		return minColor;
	}

	public void setMinColor(float minColor) {
		this.minColor = minColor;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getTransEffect() {
		return transEffect;
	}

	public void setTransEffect(String transEffect) {
		//If the layer is not tiled then we
		//force to have 'none' as transition effect.
		//The reason is that if not, the layer keeps been tiled
		if(isTiled()){
			this.transEffect = transEffect;
		} else{
			this.transEffect = "none";
		}
	}

	public String getMaxTimeLayer() {
		return maxTimeLayer;
	}

	public void setMaxTimeLayer(String maxTimeLayer) {
		this.maxTimeLayer = maxTimeLayer;
	}

	public String getCql() {
		return cql;
	}

	public void setCql(String cql) {
		this.cql = cql;
	}

	public String getCql_cols() {
		return cql_cols;
	}

	public void setCql_cols(String cql_cols) {
		this.cql_cols = cql_cols;
	}

	public boolean isJsonp() {
		return jsonp;
	}

	public void setJsonp(boolean jsonp) {
		this.jsonp = jsonp;
	}

	
}
