package me.lab7.common.models;

import java.io.Serializable;


public class Organization implements Comparable<Organization>, Serializable {
    private Long id;
    private final String fullName;
    private final Integer annualTurnover;
    private final Long employeesCount;
    private final Address postalAddress;
    private int creatorId;

    public Organization(String fullName, Integer annualTurnover, Long employeesCount, Address postalAddress) {
        this.fullName = fullName;
        this.annualTurnover = annualTurnover;
        this.employeesCount = employeesCount;
        this.postalAddress = postalAddress;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public String getFullName() {
        return fullName;
    }
    public Long getEmployeesCount() {
        return employeesCount;
    }
    public Integer getAnnualTurnover() {
        return annualTurnover;
    }
    public Address getPostalAddress() {
        return postalAddress;
    }

    @Override
    public String toString() {
        return this.fullName + "(annual_turnover=" + this.annualTurnover + "; employee_count=" + this.employeesCount +
               "; postal_address=" + this.postalAddress + ")";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organization organization = (Organization) o;
        return fullName.equals(organization.fullName) && annualTurnover.equals(organization.annualTurnover) &&
               employeesCount.equals(organization.employeesCount) && postalAddress.equals(organization.postalAddress);
    }


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
