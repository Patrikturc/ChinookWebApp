package com.sparta.pt.chinookwebapp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "customer", schema = "chinook")
public class Customer {
    @Id
    @Column(name = "CustomerId", nullable = false)
    private Integer id;

    @Size(max = 40)
    @NotNull
    @Column(name = "FirstName", nullable = false, length = 40)
    private String firstName;

    @Size(max = 20)
    @NotNull
    @Column(name = "LastName", nullable = false, length = 20)
    private String lastName;

    @Size(max = 80)
    @Column(name = "Company", length = 80)
    private String company;

    @Size(max = 70)
    @Column(name = "Address", length = 70)
    private String address;

    @Size(max = 40)
    @Column(name = "City", length = 40)
    private String city;

    @Size(max = 40)
    @Column(name = "State", length = 40)
    private String state;

    @Size(max = 40)
    @Column(name = "Country", length = 40)
    private String country;

    @Size(max = 10)
    @Column(name = "PostalCode", length = 10)
    private String postalCode;

    @Size(max = 24)
    @Column(name = "Phone", length = 24)
    private String phone;

    @Size(max = 24)
    @Column(name = "Fax", length = 24)
    private String fax;

    @Size(max = 60)
    @NotNull
    @Column(name = "Email", nullable = false, length = 60)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SupportRepId")
    private Employee supportRep;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Employee getSupportRep() {
        return supportRep;
    }

    public void setSupportRep(Employee supportRep) {
        this.supportRep = supportRep;
    }

}