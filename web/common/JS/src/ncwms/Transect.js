/*
Document   : Transect
Created on : Sep 10, 2012, 6:39:38 PM
Author     : khan

This file creates the transect tool, it is basically using javascript to create
lines over the map using some libraries from OpenLayers. 
 */
goog.provide('owgis.ncwms.transect');

goog.require('ol.Map');
goog.require('ol.View');
goog.require('ol.interaction');
goog.require('ol.interaction.Draw');
goog.require('ol.layer.Tile');
goog.require('ol.layer.Vector');
goog.require('ol.source.Vector');
goog.require('ol.style.Circle');
goog.require('ol.style.Fill');
goog.require('ol.style.Stroke');
goog.require('ol.style.Style');

var draw;
var transectOn = false; //boolean for transect tool if the netcdf has the option or not

// Initilizes a vector layer that will contain the 'drawing' for the transects
if(netcdf){
	
	var transectStyle = new ol.style.Style({
		fill: new ol.style.Fill({
			color: 'rgba(255, 255, 255, 0.2)'
		}),
		stroke: new ol.style.Stroke({
			color: '#ffcc33',
			width: 2
		}),
		image: new ol.style.Circle({
			radius: 7,
			fill: new ol.style.Fill({
				color: '#ffcc33'
			})
		})
	});
	
	
	var transectSource = new ol.source.Vector();
	var transectLayer = new ol.layer.Vector({
		source: transectSource,
		style: transectStyle });
}

/**
 *This is the function called when the transect tool button is selected, 
 *once selected it calls this function and lets the map know that this control 
 *is now activated. 
 */
function toggleControl() 
{
    if(!transectOn){
		//Initializes source and 
		transectSource = new ol.source.Vector();
		transectLayer = new ol.layer.Vector({
			source: transectSource,
			style: transectStyle });

        document.getElementById('lineToggle').innerHTML = unselectTransect.toString();
		draw = new ol.interaction.Draw({
			source: transectSource,
			type: "LineString"
		});
		draw.on("drawend",getVerticalTransect);
		draw.on("drawstart",cleanPreviousTransect);
		// Do nothing with single click
		map.unByKey(singleClickKey);
		map.addLayer(transectLayer);
		map.addInteraction(draw);
    } else {
        document.getElementById('lineToggle').innerHTML= transect.toString();
		map.removeInteraction(draw);
		map.removeLayer(transectLayer);
		draw.un("drawend",getVerticalTransect);
		//Recover the original behaviour of single click
		singleClickKey = map.on('singleclick',punctualData);
    }
    transectOn = !transectOn;        
}

/*
 * When we start creating a new transect we first clear all the previous geoms
 * @param {type} event
 * @returns {undefined}
 */
function cleanPreviousTransect(event){
	transectSource.clear();
}

/** This function process the user's drawing and contacts the ncWMS server and the godiva program to
 * produce graphs depending on the variables selected and the dates. It is used by transect.jsp
 * @param {event} event Event object
 */
function getVerticalTransect(event){   
	var features = transectSource.getFeatures();
	var geom = features[0].getGeometry();
//	console.log(geom.getFlatCoordinates());
//	console.log(geom.getCoordinates());
	var coords = geom.getCoordinates();
	var coordsTxt = "";

	for (i = 0; i < coords.length; i++) {
		if(i>0){
			coordsTxt+=",";
		}
		coordsTxt+= coords[i].toString().replace(',',' ');
	}

    try{
        time = calStart.selection.get();//get the selected time in the start calendar
        time = Calendar.intToDate(time);    
        time = Calendar.printDate(time, '%Y-%m-%d');    
    }
    catch(err) {
        time = layerDetails['nearestTimeIso'];
    }        
    
	var mainLayer = owgis.layers.getMainLayer();
	var mainSource = mainLayer.getSource();
	//TODO not if this function can returnmore than one result
    var url = mainSource.getUrls().toString();
    url = url + '?REQUEST=GetTransect&LAYER=';
    url = url + mainSource.getParams().LAYERS + "&CRS=" + _map_projection + "&TIME=" + time;
    url = url +"&LINESTRING=" + coordsTxt + "&FORMAT=image/png&COLORSCALERANGE=auto";
    url = url + "&NUMCOLORBANDS=250&LOGSCALE=false&PALETTE=" + mappalette;
	
    popUp(url,400,600);
}