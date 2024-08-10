package com.sparta.pt.chinookwebapp.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class EmployeeDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private String title;
    private LocalDate birthDate;
    private LocalDate hireDate;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String phone;
    private String fax;
    private String email;
    private Integer reportsToId;
    private String reportsToName;

    public EmployeeDTO() {
    }

    public EmployeeDTO(Integer id, String firstName, String lastName, String title, LocalDate birthDate, LocalDate hireDate,
                       String address, String city, String state, String country, String postalCode, String phone,
                       String fax, String email, Integer reportsToId, String reportsToName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
        this.birthDate = birthDate;
        this.hireDate = hireDate;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
        this.phone = phone;
        this.fax = fax;
        this.email = email;
        this.reportsToId = reportsToId;
        this.reportsToName = reportsToName;
    }

    public EmployeeDTO(Integer id, String firstName, String lastName, String title, LocalDate birthDate, LocalDate hireDate,
                       String address, String city, String state, String country, String postalCode, String phone,
                       String fax, String email, String reportsToName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
        this.birthDate = birthDate;
        this.hireDate = hireDate;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
        this.phone = phone;
        this.fax = fax;
        this.email = email;
        this.reportsToName = reportsToName;
    }
    @JsonIgnore
    public Instant getBirthDateAsInstant() {
        return birthDate != null ? birthDate.atStartOfDay(ZoneId.systemDefault()).toInstant() : null;
    }
    @JsonIgnore
    public Instant getHireDateAsInstant() {
        return hireDate != null ? hireDate.atStartOfDay(ZoneId.systemDefault()).toInstant() : null;
    }

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
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

    public Integer getReportsToId() {
        return reportsToId;
    }

    public void setReportsToId(Integer reportsToId) {
        this.reportsToId = reportsToId;
    }

    public String getReportsToName() {
        return reportsToName;
    }

    public void setReportsToName(String reportsToName) {
        this.reportsToName = reportsToName;
    }
}