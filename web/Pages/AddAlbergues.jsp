<%-- 
    Document   : AddAlbergues
    Created on : Nov 8, 2013, 7:49:53 PM
    Author     : olmozavala
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!--This part is used to change the texts depending on the language of the user browser-->
<fmt:setLocale value="${language}" />
<fmt:setBundle basename="com.mapviewer.messages.text" />
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
			
		<link href="common/CSS/NewShelter.css" rel="stylesheet" type="text/css"/>
			
		<!--Specific for OpenLayers3 -->
		<link rel="stylesheet" href="./common/CSS/ol.css" />
		<script src="http://ol3js.org/en/master/build/ol.js"></script>
		<!--Specific for OpenLayers3 -->
		
		<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
		<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script>
		<script src="//netdna.bootstrapcdn.com/bootstrap/3.0.0/js/bootstrap.min.js"></script>
			
		<link href="//netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
		<script>
			var vector = null;
			var style = new ol.style.Style({
							  symbolizers: [
								new ol.style.Icon({
								  url: 'common/images/pageIcons/Albergue2Smaller.png'
								})
							  ]
							});

			function initMap() {

				var _map_projection = 'EPSG:3857';
				var defCenter= ol.proj.transform([-100, 21], 'EPSG:4326', _map_projection);
				
				var ol3view = new ol.View({
					projection: _map_projection,
					center: defCenter,
					zoom: 5,
				});
				
				var map = new ol.Map({
					target: 'sheltermap', // Define 'div' that contains the map
					view: ol3view });
				
				var layerosm=  new ol.layer.Tile({
					source: new ol.source.OSM()
				});
				
				map.addLayer(layerosm);
				
				map.on('singleclick', function (evt) {

					var coordinate = evt.coordinate;
					var degcoord = ol.proj.transform(coordinate, _map_projection,'EPSG:4326');
					$("#shelterloc").val(degcoord);

					var data = {
					  type: 'FeatureCollection',
					  features: [{
						type: 'Feature',
						geometry: {
						  type: 'Point',
						  coordinates: degcoord
						}
					  }]
					};

					map.removeLayer(vector);
					vector = new ol.layer.Vector({
										source: new ol.source.Vector({
											parser: new ol.source.GeoJSON(),
											data: data
										}),
										style: style
									});

					map.addLayer(vector);
				});//Singleclick
			}//Jquery 
			/**
			 * Instructions executed when the page
			 * is ready
			 */
			jQuery(document).ready(function()
			{
				initMap();
			});
		</script>
    </head>
    <body>
		<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
			<div class="container">
				<div class="navbar-header">
					<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
						<span class="sr-only">Toggle navigation</span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
					</button>
					<a class="navbar-brand" href="./mapviewer">Beat the Beast in the Steeplechase to the U.S.</a>
					<a class="navbar-brand" href="./mapviewer">Map viewer</a>
				</div>
			</div>
		</div>
		
		<div class="container" style="position: relative; top:45px;">
			
			<div class="starter-template">
				<h1><fmt:message key="datafest.newshelter"/></h1>
				<form role="form" action="/BeatTheBeast/AddShelter" method="post" target="_blank">
					<input type="hidden" name="task" value="add">
					<div class="form-group">
						<label for="sheltername"> <fmt:message key="datafest.name" /></label>
						<input type="text" class="form-control" id="sheltername" name="name" required
							   placeholder="<fmt:message key="datafest.nameph" />">
					</div>
					<div class="form-group">
						<label for="shelteraddr"> <fmt:message key="datafest.address" /></label>
						<input type="text" class="form-control" id="shelteraddr" name="addr" required
							   placeholder="<fmt:message key="datafest.addressph" />">
					</div>
					<div class="row">
						<div class="col-sm-6">
							<div class="form-group">
								<label for="shelterphone"> <fmt:message key="datafest.phone" /></label>
								<input type="tel" class="form-control" id="shelterphone" name="phone" required
									   pattern="[0-9]{3}-[0-9]{3}-[0-9]{4}"
									   placeholder="<fmt:message key="datafest.phoneph" />">
							</div>
							<div class="form-group">
								<label for="shelterlink"> <fmt:message key="datafest.link" /></label>
								<input type="url" class="form-control" id="shelterlink" name="link"  pattern="https?://.+"
									   placeholder="<fmt:message key="datafest.linkph" />">
							</div>
							<div class="form-group">
								<label for="shelterloc"> <fmt:message key="datafest.loc" /></label>
								<input type="text" class="form-control" id="shelterloc" name="loc" required 
									   pattern="^([-+]?\d{1,3}[.]\d+),\s*([-+]?\d{1,2}[.]\d+)$"
									   placeholder="<fmt:message key="datafest.locph" />">
							</div>
							<button type="submit" class="btn btn-default"><fmt:message key="datafest.submit" /></button>
						</div>
						<div class="col-sm-6">
							<div id="sheltermap"></div>
						</div>
					</div>
				</form>
			</div>
				
		</div><!-- /.container -->
    </body>
</html>