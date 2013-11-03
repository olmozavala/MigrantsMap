/**
 * This is the class in charge of getting the request of the animations, palettes, etc of the layers on the ncWMS server. 
 *
 */
package com.mapviewer.business;

import com.mapviewer.model.Layer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 *
 * @author olmozavala
 */
public class NetCDFRequestManager {

	/**
	 * This functions makes a URL request to one ncWMS server to retrieve the
	 * valid animation options for the selected dates
	 *
	 * @param {Layer} layer
	 * @param {String} startStr
         * @param {String} endStr
	 * @return
	 */
	public static String getLayerTimeStepsForAnimation(Layer layer, String startStr, String endStr) {

		URL ncReq;
		String urlRequest = buildRequest(layer, "GetMetadata", "animationTimesteps");
		urlRequest += "&start=" + startStr;
		urlRequest += "&end=" + endStr;

		JSONObject timeSteps = new JSONObject();

		try {
			ncReq = new URL(urlRequest);
			ncReq.openConnection();
			InputStreamReader input = new InputStreamReader(ncReq.openStream());
			BufferedReader in = new BufferedReader(input);
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				if (!inputLine.trim().equalsIgnoreCase("")) {// TODO check for errros
					timeSteps = new JSONObject(inputLine);

//					datesWithData = (JSONObject) layerDetails.get("datesWithData");
					/*
					 * //Iterating over the years for(Enumeration years =
					 * dates.keys(); years.hasMoreElements();){ String year =
					 * (String) years.nextElement(); JSONObject yearJson =
					 * dates.getJSONObject(year); for(Enumeration months =
					 * yearJson.keys(); months.hasMoreElements();){ String month
					 * = (String) months.nextElement(); int x = 1; } }
					 */
				}
			}

		} catch (JSONException | IOException e) {
			System.out.println("Error MapViewer en RedirectServer en generateRedirect" + e.getMessage());
			return "Error getting the layerDetials:" + e.getMessage();
		}
		return timeSteps.toString();
	}

	/**
	 * This functions makes a URL request to one ncWMS server to retrieve the
	 * valid times one layer has for an specific day.
	 *
	 * @param {Layer}layer
	 * @param {String}day
	 * @return
	 */
	public static String getLayerTimeSteps(Layer layer, String day) {

		URL ncReq;
		String urlRequest = buildRequest(layer, "GetMetadata", "timesteps");
		urlRequest += "&day=" + day;

		JSONObject timeSteps = new JSONObject();

		try {
			ncReq = new URL(urlRequest);
			ncReq.openConnection();
			InputStreamReader input = new InputStreamReader(ncReq.openStream());
			BufferedReader in = new BufferedReader(input);
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				if (!inputLine.trim().equalsIgnoreCase("")) {// TODO check for errros
					timeSteps = new JSONObject(inputLine);

//					datesWithData = (JSONObject) layerDetails.get("datesWithData");
					/*
					 * //Iterating over the years for(Enumeration years =
					 * dates.keys(); years.hasMoreElements();){ String year =
					 * (String) years.nextElement(); JSONObject yearJson =
					 * dates.getJSONObject(year); for(Enumeration months =
					 * yearJson.keys(); months.hasMoreElements();){ String month
					 * = (String) months.nextElement(); int x = 1; } }
					 */
				}
			}

		} catch (JSONException | IOException e) {
			System.out.println("Error MapViewer en RedirectServer en generateRedirect" + e.getMessage());
			return "Error getting the layerDetials:" + e.getMessage();
		}
		return timeSteps.toString();
	}

	/**
	 * Obtains the layerDetails from a ncWMS server and stores the result as a
	 * string (from a JSON object) this is then the javascript variable layerDetails
	 *
	 * @param {Layer} layer
	 * @return
	 */
	public static String getLayerDetails(Layer layer) throws Exception {

		//For the moment we only obtain the datails for netCDF layers (ncWMS server)
		if (!layer.isNetCDF()) {
			return "";
		}

		URL ncReq;
		String detailsRequest = buildRequest(layer, "GetMetadata", "layerDetails");
		JSONObject layerDetails = new JSONObject();

		int maxTries = 3; //120
		int tryNumber = 0;// Current try
		boolean accepted = false;

		while (!accepted && (tryNumber < maxTries)) {// While 
			try {
				ncReq = new URL(detailsRequest);
				ncReq.openConnection();
				InputStreamReader input = null;

				// Number of times we will try to get the layer details for each layer
				// in this case we will wait at most 2 minutes

				input = new InputStreamReader(ncReq.openStream());

				BufferedReader in = new BufferedReader(input);
				String inputLine;

				while ((inputLine = in.readLine()) != null) {
					if (!inputLine.trim().equalsIgnoreCase("")) {// TODO check for errros
                                            //System.out.println("AKI" + inputLine);
						layerDetails = new JSONObject(inputLine);
					}
				}

				// If minColor and maxColor are set in the xml file, we replace 
				// the JSONObject of LayerDetails
				if (!((layer.getMinColor() == -1) && (layer.getMaxColor() == -1))) {
					JSONArray scale = layerDetails.getJSONArray("scaleRange");
					scale.put(0, layer.getMinColor());
					scale.put(1, layer.getMaxColor());
				}

				accepted = true;//If there is no exception then we accept the layer details
			} catch (JSONException | IOException e) {
				try {
					tryNumber++;
					System.out.println("Layer details try number:" + tryNumber + " Error:"+ e.getMessage());
					Thread.sleep(1000);//We wait for 1 seconds.
				} catch (InterruptedException ex) {
					System.out.println("Interrupted exception while waiting for layer details:" + ex.getMessage());
				}
			}
		}
		
		if (accepted) {
			System.out.println("layer details: " + layerDetails.toString());
			return layerDetails.toString();
		} else {
			throw new Exception("ERROR: Not able to load layer details for layer:" + layer.getDisplayName("EN"));
		}
	}
	
	/**
	 * Gets the URL used to request the image of the color palette to a ncWMS
	 * server
	 *
	 * @param{Layer} layer
	 * @param {String} palette
	 * @return
	 */
	public static String getPaletteUrl(Layer layer, String palette) {
		
		if (!layer.isNetCDF()) {
			return "";
		}
		
		String paletteUrl = layer.getServer() + "?REQUEST=GetLegendGraphic"
				+ "&LAYER=" + layer.getName()
				+ "&COLORBARONLY=true"
				+ "&PALETTE=" + palette
				+ "&WIDTH=20&HEIGHT=120"
				+ "&NUMCOLORBANDS=254";
		
		return paletteUrl;
	}
	
	/**
	 * Build the http request
	 * @param {Layer} layer
	 * @param {String} request
	 * @param {String} item
	 * @return String
	 */
	private static String buildRequest(Layer layer, String request, String item) {
		String server = layer.getServer();
		String layerName = layer.getName();
		
		String httpReq = server + "?";
		httpReq += "REQUEST=" + request;
		httpReq += "&item=" + item;
		httpReq += "&layerName=" + layerName;
		
		return httpReq;
	}
}
