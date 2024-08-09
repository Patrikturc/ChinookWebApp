package com.sparta.pt.chinookwebapp.repositories;

import com.sparta.pt.chinookwebapp.models.Invoiceline;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoicelineRepository extends JpaRepository<Invoiceline, Integer> {
}