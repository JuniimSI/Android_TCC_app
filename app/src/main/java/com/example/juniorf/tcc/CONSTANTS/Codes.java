package com.example.juniorf.tcc.CONSTANTS;

/**
 * Created by juniorf on 19/12/16.
 */

public interface Codes {

	////WEBSERVICE PHP
    public String url = "grainmapey.pe.hu/GranMapey/"; 

    public String showLocation = "http://grainmapey.pe.hu/GranMapey/show_location.php";
    public String urlJsonShowMessage = "http://grainmapey.pe.hu/GranMapey/show_message.php";
    public String urlJsonShowAnswer = "http://grainmapey.pe.hu/GranMapey/show_answer.php";
    public String urlJsonShowLocationType = "http://grainmapey.pe.hu/GranMapey/show_location_type.php";
    public String urlJsonShowType = "http://grainmapey.pe.hu/GranMapey/show_type.php";

    	////DAOS
    public String urlJsonInsertMessage = "http://grainmapey.pe.hu/GranMapey/insert_message.php";
    public String urlJsonInsertAnswer = "http://grainmapey.pe.hu/GranMapey/insert_answer.php";
    public String urlJsonUpdateMessage = "http://grainmapey.pe.hu/GranMapey/update_message.php";
    public String urlJsonUpdateAnswer = "http://grainmapey.pe.hu/GranMapey/update_answer.php";
    public String urlJsonDeleteMessage = "http://grainmapey.pe.hu/GranMapey/delete_message.php";
	public String urlJsonDeleteAnswer = "http://grainmapey.pe.hu/GranMapey/delete_answer.php";
	public String urlJsonEmailOrigemMessage = "http://grainmapey.pe.hu/GranMapey/find_emailOrigem_message.php";


    public String urlJsonInsertLocation = "http://grainmapey.pe.hu/GranMapey/insert_location.php";
    public String urlJsonDetailsLocation = "http://grainmapey.pe.hu/GranMapey/details_location.php";
    public String urlJsonEmailLocation = "http://grainmapey.pe.hu/GranMapey/find_email_location_by_id.php?id=";
    public String urlJsonDetailsIdLocation = "http://grainmapey.pe.hu/GranMapey/find_details_by_id_location.php";
    public String urlJsonDeleteLocation = "http://grainmapey.pe.hu/GranMapey/delete_location.php";
    public String urlJsonUpdateLocation = "http://grainmapey.pe.hu/GranMapey/update_location.php";

    public String urlJsonInsertType = "http://grainmapey.pe.hu/GranMapey/insert_tipo.php";

    ////GOOGLE APIS
    public String key="&key=AIzaSyDzmS5_MB0psxCMWjPiLkJMda9VEJBzHQw";
    public String urlGooglePlaceId = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
    public String urlGoogleLocations = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
    public String urlGoogleDirection = "https://maps.googleapis.com/maps/api/directions/json?origin=";
}
