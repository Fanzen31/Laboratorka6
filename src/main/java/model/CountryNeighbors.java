package model;

import java.util.List;

public class CountryNeighbors {
    private String country;
    private List<String> neighbors;

    public CountryNeighbors(String country, List<String> neighbors) {
        this.country = country;
        this.neighbors = neighbors;
    }

    public String getCountry() {
        return country;
    }

    public List<String> getNeighbors() {
        return neighbors;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setNeighbors(List<String> neighbors) {
        this.neighbors = neighbors;
    }

    @Override
    public String toString() {
        return country + " -> " + String.join(", ", neighbors);
    }
}
