<%-- 
    Document   : RequiredDivs
    Created on : Aug 3, 2012, 5:58:26 PM
    Author     : olmozavala
--%>
<!-- Map title -->
<div class="mapTitle" id="layerTitle"> 
    <div class="row">
        <div class="col-sm-8 col-sm-offset-2 ">
<<<<<<< HEAD
            <p id="pTitleText"> <span style="font-size:28px"><fmt:message key="main.title" /></span> <br> ${layerTitle} </p> 
=======
            <p id="pTitleText"> ${layerTitle} </p> 
>>>>>>> 164248b9b2dfb775e5f6f6bc514da0e170462cb1
        </div>
    </div>
</div>

<!-- This div contains all the map --> 
<div id="map"> </div>      
<div class="layersLonLat">
	<!-- This div displays the specific data of the map -->
	<fmt:message key="main.lon" />:&nbsp;&nbsp;<fmt:message key="main.lat" />: &nbsp;</b>
	<div id="location" ></div>
</div>    
	
<div id="popup" class="ol-popup">
	<a href="#" id="popup-closer" class="ol-popup-closer"></a>
	<div id="popup-content"></div>
</div>

<!-- This div is only used to contain the movement of the
draggable windows -->
<div id="draggable-container"></div>
