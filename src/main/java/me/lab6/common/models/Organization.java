package me.lab6.common.models;

import java.io.Serializable;

/**
 * Represents an organization with a full name, annual turnover, employees count, and postal address.
 * Implements the Comparable interface for sorting based on annual turnover, employees count, and full name.
 */
public record Organization(String fullName, Integer annualTurnover, Long employeesCount,
                           Address postalAddress) implements Comparable<Organization>, Serializable {
    /**
     * Returns a string representation of the organization in the format "fullName(annual_turnover=annualTurnover;
     * employee_count=employeesCount; postal_address=postalAddress)".
     *
     * @return a string representation of the organization
     */
    @Override
    public String toString() {
        return this.fullName + "(annual_turnover=" + this.annualTurnover + "; employee_count=" + this.employeesCount +
                "; postal_address=" + this.postalAddress + ")";
    }

    /**
     * Compares this organization to another object for equality.
     *
     * @param o the object to compare to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organization organization = (Organization) o;
        return fullName.equals(organization.fullName) && annualTurnover.equals(organization.annualTurnover) &&
                employeesCount.equals(organization.employeesCount) && postalAddress.equals(organization.postalAddress);
    }

    /**
     * Compares this organization to another organization based on annual turnover, employees count, and full name.
     *
     * @param org the organization to compare to
     * @return a negative integer, zero, or a positive integer as this organization is less than, equal to,
     * or greater than the specified organization
     */
    @Override
    public int compareTo(Organization org) {
        if (this.annualTurnover > org.annualTurnover) {
            return 1;
        }
        if (this.annualTurnover < org.annualTurnover) {
            return -1;
        }
        if (this.employeesCount > org.employeesCount) {
            return 1;
        }
        if (this.employeesCount < org.employeesCount) {
            return -1;
        }
        return Character.compare(this.fullName.charAt(0), org.fullName.charAt(0));
    }
}
