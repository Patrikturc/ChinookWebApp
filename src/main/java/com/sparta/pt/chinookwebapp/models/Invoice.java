package com.sparta.pt.chinookwebapp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "invoice", schema = "chinook")
public class Invoice {
    @Id
    @Column(name = "InvoiceId", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CustomerId", nullable = false)
    private Customer customer;

    @NotNull
    @Column(name = "InvoiceDate", nullable = false)
    private Instant invoiceDate;

    @Size(max = 70)
    @Column(name = "BillingAddress", length = 70)
    private String billingAddress;

    @Size(max = 40)
    @Column(name = "BillingCity", length = 40)
    private String billingCity;

    @Size(max = 40)
    @Column(name = "BillingState", length = 40)
    private String billingState;

    @Size(max = 40)
    @Column(name = "BillingCountry", length = 40)
    private String billingCountry;

    @Size(max = 10)
    @Column(name = "BillingPostalCode", length = 10)
    private String billingPostalCode;

    @NotNull
    @Column(name = "Total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Instant getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Instant invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getBillingCity() {
        return billingCity;
    }

    public void setBillingCity(String billingCity) {
        this.billingCity = billingCity;
    }

    public String getBillingState() {
        return billingState;
    }

    public void setBillingState(String billingState) {
        this.billingState = billingState;
    }

    public String getBillingCountry() {
        return billingCountry;
    }

    public void setBillingCountry(String billingCountry) {
        this.billingCountry = billingCountry;
    }

    public String getBillingPostalCode() {
        return billingPostalCode;
    }

    public void setBillingPostalCode(String billingPostalCode) {
        this.billingPostalCode = billingPostalCode;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

}