package it.unipi.lsmdb.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Beer {

    private int _id;
    private String name;
    private String state;
    private String country;
    private String style;
    private int availability;
    private int abv;
    private int volume;
    private int price;
    private int brewery_id;
    private String brewery_name;
    private String brewery_city;
    private String brewery_types;
    private int view_count;

    public Beer(int id,String name){
        this._id=id;
        this.name=name;
    }

    public Beer(int id,String name,String style,String brewery_name){
        this._id=id;
        this.name=name;
        this.style=style;
        this.brewery_name=brewery_name;
    }

    public Beer(int id, String name, String state, String country, String style, int availability, int abv, int volume, int price, int brewery_id, String brewery_name, String brewery_city,String brewery_types, int view_count) {
        this._id=id;
        this.name=name;
        this.state=state;
        this.country=country;
        this.style=style;
        this.availability=availability;
        this.abv=abv;
        this.volume=volume;
        this.price=price;
        this.brewery_id=brewery_id;
        this.brewery_name=brewery_name;
        this.brewery_city=brewery_city;
        this.brewery_types=brewery_types;
        this.view_count=view_count;
    }

    public Beer(){
    }

    public int getBrewery_id() {
        return brewery_id;
    }

    public void setBrewery_id(int brewery_id) {
        this.brewery_id = brewery_id;
    }

    public int get_id() {
        return _id;
    }

    public void setId(int id) {
        this._id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public int getAbv() {
        return abv;
    }

    public void setAbv(int abv) {
        this.abv = abv;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getBrewery_name() {
        return brewery_name;
    }

    public void setBrewery_name(String brewery_name) {
        this.brewery_name = brewery_name;
    }

    public String getBrewery_city() {
        return brewery_city;
    }

    public void setBrewery_city(String brewery_city) {
        this.brewery_city = brewery_city;
    }

    public int getView_count() {
        return view_count;
    }

    public void setView_count(int view_count) {
        this.view_count = view_count;
    }

    public String getBrewery_types(){return brewery_types; }

    public void setBrewery_types(String brewery_types){this.brewery_types=brewery_types;}

    @Override
    public String toString() {
        return "Beer{" +
                "id=" + _id +
                ", name='" + name + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", style='" + style + '\'' +
                ", availability=" + availability +
                ", abv=" + abv +
                ", volume=" + volume +
                ", price=" + price +
                ", brewery_id=" + brewery_id +
                ", brewery_name='" + brewery_name + '\'' +
                ", brewery_city='" + brewery_city + '\'' +
                ", brewery_types='" + brewery_types + '\'' +
                ", view_count=" + view_count +
                '}';
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("Brewery")
    private void unpackNestedBrewery(Map<String, Object> brewery) {
        this.brewery_id = (Integer) brewery.get("id");
        this.brewery_name = (String) brewery.get("name");
        this.brewery_city = (String) brewery.get("city");
        this.brewery_types = (String) brewery.get("types");
    }
}
