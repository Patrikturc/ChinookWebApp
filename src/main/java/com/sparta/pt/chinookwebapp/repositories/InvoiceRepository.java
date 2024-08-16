package com.sparta.pt.chinookwebapp.repositories;

import com.sparta.pt.chinookwebapp.models.Customer;
import com.sparta.pt.chinookwebapp.models.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    Collection<Invoice> findByCustomer(Customer customer);
}