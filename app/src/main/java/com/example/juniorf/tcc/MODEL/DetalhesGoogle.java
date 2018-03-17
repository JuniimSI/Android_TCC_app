package com.example.juniorf.tcc.MODEL;

/**
 * Created by juniorf on 16/03/18.
 */

public class DetalhesGoogle {

	private String address;
	private String priceLevel;
	private String webSite;
	private Boolean openNow;

	public DetalhesGoogle(){

	}

	public void setWebSite(String webSite){
		this.webSite = webSite;
	}

	public String getWebSite(){
		return this.webSite;
	}

	public void setPriceLevel(String priceLevel){
		this.priceLevel = priceLevel;
	}

	public String getPriceLevel(){
		return this.priceLevel;
	}

	public void setAddress(String address){
		this.address = address;
	}

	public String getAddress(){
		return this.address;
	}

	public void setOpenNow(Boolean openNow){
		this.openNow = openNow;
	}

	public Boolean getOpenNow(){
		return this.openNow;
	}



}
