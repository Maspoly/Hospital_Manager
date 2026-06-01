package br.edu.ufersa.hospital_manager.model.entities;

public class Address {
    private long id;
    private String street;
    private String number;
    private String neighborhood;
    private String city;
    private String state;

    public Address(String street, String number, String neighborhood, String city, String state) {
        id = 0; // default id, should be set by the database
        setStreet(street);
        setNumber(number);
        setNeighborhood(neighborhood);
        setCity(city);
        setState(state);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        if (id <= 0) {
            throw new RuntimeException("ID must be positive.");
        }
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        if (street == null || street.isBlank()) {
            throw new RuntimeException("Street cannot be empty.");
        }
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        if (number == null || number.isBlank()) {
            throw new RuntimeException("Number cannot be empty.");
        }
        this.number = number;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        if (neighborhood == null || neighborhood.isBlank()) {
            throw new RuntimeException("Neighborhood cannot be empty.");
        }
        this.neighborhood = neighborhood;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        if (city == null || city.isBlank()) {
            throw new RuntimeException("City cannot be empty.");
        }
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        if (state == null || state.isBlank()) {
            throw new RuntimeException("State cannot be empty.");
        }
        this.state = state;
    }
}
