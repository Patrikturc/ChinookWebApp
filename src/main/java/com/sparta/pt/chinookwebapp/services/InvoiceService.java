package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.dtos.InvoiceDTO;
import com.sparta.pt.chinookwebapp.exceptions.InvalidInputException;
import com.sparta.pt.chinookwebapp.models.Customer;
import com.sparta.pt.chinookwebapp.models.Invoice;
import com.sparta.pt.chinookwebapp.repositories.CustomerRepository;
import com.sparta.pt.chinookwebapp.repositories.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository, CustomerRepository customerRepository) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
    }

    public List<InvoiceDTO> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<InvoiceDTO> getInvoiceById(Integer id) {
        return invoiceRepository.findById(id).map(this::convertToDTO);
    }

    public List<InvoiceDTO> getInvoicesByCustomerId(Integer customerId) {
        List<Invoice> allInvoices = invoiceRepository.findAll();

        List<Invoice> filteredInvoices = allInvoices.stream()
                .filter(invoice -> invoice.getCustomer().getId().equals(customerId))
                .toList();

        return filteredInvoices.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public InvoiceDTO createInvoice(InvoiceDTO invoiceDTO) {
        Invoice invoice = convertToEntity(invoiceDTO);

        int maxId = invoiceRepository.findAll().stream()
                .max(Comparator.comparingInt(Invoice::getId))
                .map(Invoice::getId)
                .orElse(0);
        invoice.setId(maxId + 1);

        try {
            setCustomerByName(invoice, invoiceDTO.getCustomerName());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException(e.getMessage());
        }

        Invoice savedInvoice = invoiceRepository.save(invoice);
        return convertToDTO(savedInvoice);
    }

    public Optional<InvoiceDTO> updateInvoice(Integer id, InvoiceDTO invoiceDTO) {
        return invoiceRepository.findById(id)
                .map(existingInvoice -> {
                    if (invoiceDTO.getInvoiceDate() != null) existingInvoice.setInvoiceDate(invoiceDTO.getInvoiceDate());
                    if (invoiceDTO.getBillingAddress() != null) existingInvoice.setBillingAddress(invoiceDTO.getBillingAddress());
                    if (invoiceDTO.getBillingCity() != null) existingInvoice.setBillingCity(invoiceDTO.getBillingCity());
                    if (invoiceDTO.getBillingState() != null) existingInvoice.setBillingState(invoiceDTO.getBillingState());
                    if (invoiceDTO.getBillingCountry() != null) existingInvoice.setBillingCountry(invoiceDTO.getBillingCountry());
                    if (invoiceDTO.getBillingPostalCode() != null) existingInvoice.setBillingPostalCode(invoiceDTO.getBillingPostalCode());
                    if (invoiceDTO.getTotal() != null) existingInvoice.setTotal(invoiceDTO.getTotal());

                    if (invoiceDTO.getCustomerName() != null) {
                        try {
                            setCustomerByName(existingInvoice, invoiceDTO.getCustomerName());
                        } catch (IllegalArgumentException e) {
                            throw new InvalidInputException(e.getMessage());
                        }
                    }

                    Invoice savedInvoice = invoiceRepository.save(existingInvoice);
                    return convertToDTO(savedInvoice);
                });
    }

    public InvoiceDTO upsertInvoice(Integer id, InvoiceDTO invoiceDTO) {
        Invoice invoice = convertToEntity(invoiceDTO);
        setCustomerByName(invoice, invoiceDTO.getCustomerName());

        if (!invoiceRepository.existsById(id)) {
            invoice.setId(id);
            Invoice savedInvoice = invoiceRepository.save(invoice);
            return convertToDTO(savedInvoice);
        }

        invoice.setId(id);
        updateInvoice(id, invoiceDTO);

        return convertToDTO(invoice);
    }

    public void deleteInvoice(Integer id) {
        invoiceRepository.deleteById(id);
    }

    // Conversion methods
    private InvoiceDTO convertToDTO(Invoice invoice) {
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setId(invoice.getId());
        invoiceDTO.setCustomerName(invoice.getCustomer().getFirstName() + " " + invoice.getCustomer().getLastName());
        invoiceDTO.setInvoiceDate(invoice.getInvoiceDate());
        invoiceDTO.setBillingAddress(invoice.getBillingAddress());
        invoiceDTO.setBillingCity(invoice.getBillingCity());
        invoiceDTO.setBillingState(invoice.getBillingState());
        invoiceDTO.setBillingCountry(invoice.getBillingCountry());
        invoiceDTO.setBillingPostalCode(invoice.getBillingPostalCode());
        invoiceDTO.setTotal(invoice.getTotal());
        return invoiceDTO;
    }

    private Invoice convertToEntity(InvoiceDTO invoiceDTO) {
        Invoice invoice = new Invoice();
        invoice.setId(invoiceDTO.getId());
        invoice.setInvoiceDate(invoiceDTO.getInvoiceDate());
        invoice.setBillingAddress(invoiceDTO.getBillingAddress());
        invoice.setBillingCity(invoiceDTO.getBillingCity());
        invoice.setBillingState(invoiceDTO.getBillingState());
        invoice.setBillingCountry(invoiceDTO.getBillingCountry());
        invoice.setBillingPostalCode(invoiceDTO.getBillingPostalCode());
        invoice.setTotal(invoiceDTO.getTotal());
        return invoice;
    }

    private void setCustomerByName(Invoice invoice, String customerName) {
        String[] nameParts = customerName.split(" ");
        if (nameParts.length < 2) {
            throw new IllegalArgumentException("Full customer name is required.");
        }
        String firstName = nameParts[0];
        String lastName = nameParts[1];

        List<Customer> customers = customerRepository.findAll();
        Customer customer = customers.stream()
                .filter(c -> c.getFirstName().equalsIgnoreCase(firstName) && c.getLastName().equalsIgnoreCase(lastName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        invoice.setCustomer(customer);
    }
}