package me.lab7.common.models;

import java.io.Serializable;

/**
 * Represents an address with a street and a zip code.
 */
public record Address(String street, String zipCode) implements Serializable {
    /**
     * Returns a string representation of the address in the format "(zipCode, street)".
     *
     * @return a string representation of the address
     */
    @Override
    public String toString() {
        return "(" + this.zipCode + ", " + this.street + ")";
    }

    /**
     * Compares this address to another object for equality.
     *
     * @param o the object to compare to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return street.equals(address.street) && zipCode.equals(address.zipCode);
    }

}
