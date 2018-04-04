package com.example.juniorf.tcc.CONSTANTS;

/**
 * Created by juniorf on 19/12/16.
 */

public interface Codes {

	////WEBSERVICE PHP
    public String url = "grainmapey.pe.hu/GranMapey/";
    public String urlJsonDetailsLocation = "http://grainmapey.pe.hu/GranMapey/details_location.php";
    public String urlJsonDetailsIdLocation = "http://grainmapey.pe.hu/GranMapey/find_details_by_id_location.php";
    public String urlJsonShowMessage = "http://grainmapey.pe.hu/GranMapey/show_message.php";
    public String urlJsonShowAnswer = "http://grainmapey.pe.hu/GranMapey/show_answer.php";
    public String urlJsonShowLocationType = "http://grainmapey.pe.hu/GranMapey/show_location_type.php";
    public String urlJsonShowType = "http://grainmapey.pe.hu/GranMapey/show_type.php";
    public String urlJsonEmailLocation = "http://grainmapey.pe.hu/GranMapey/find_email_location_by_id.php?id=";


    ////GOOGLE APIS
    public String urlGooglePlaceId = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
    public String key="&key=AIzaSyDzmS5_MB0psxCMWjPiLkJMda9VEJBzHQw";
    public String showLocation = "http://grainmapey.pe.hu/GranMapey/show_location.php";
    public String urlGoogleLocations = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
    public String urlGoogleDirection = "https://maps.googleapis.com/maps/api/directions/json?origin=";
}
