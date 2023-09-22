package POJOClassses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Location {
    String postCode;
    String country;
    String countryAbbreviation;
    ArrayList<Place> places;

    public String getPostCode() {
        return postCode;
    }

    @JsonProperty ("post code")
    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryAbbreviation() {
        return countryAbbreviation;
    }

    @JsonProperty ("country abbreviation")
    public void setCountryAbbreviation(String countryAbbreviation) {
        this.countryAbbreviation = countryAbbreviation;
    }

    public ArrayList<Place> getPlaces() {
        return places;
    }

    public void setPlaces(ArrayList<Place> places) {
        this.places = places;
    }
}
