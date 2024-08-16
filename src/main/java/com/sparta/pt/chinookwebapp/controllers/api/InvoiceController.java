package com.sparta.pt.chinookwebapp.controllers.api;

import com.sparta.pt.chinookwebapp.dtos.InvoiceDTO;
import com.sparta.pt.chinookwebapp.services.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public List<InvoiceDTO> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable Integer id) {
        Optional<InvoiceDTO> invoiceDTO = invoiceService.getInvoiceById(id);
        return invoiceDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/customers/by-id/{customerId}")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesByCustomerId(@PathVariable Integer customerId) {
        List<InvoiceDTO> invoices = invoiceService.getInvoicesByCustomerId(customerId);
        if (invoices.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/customers/by-name/{fullName}")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesByCustomerFullName(@PathVariable String fullName) {
        List<InvoiceDTO> invoices = invoiceService.getInvoicesByCustomerFullName(fullName);
        if (invoices.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(invoices);
    }

    @PostMapping
    public InvoiceDTO createInvoice(@RequestBody InvoiceDTO invoiceDTO) {
        return invoiceService.createInvoice(invoiceDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceDTO> upsertInvoice(@PathVariable Integer id, @RequestBody InvoiceDTO invoiceDTO) {
        InvoiceDTO savedInvoice = invoiceService.upsertInvoice(id, invoiceDTO);
        return ResponseEntity.ok(savedInvoice);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<InvoiceDTO> updateInvoice(@PathVariable Integer id, @RequestBody InvoiceDTO invoiceDTO) {
        Optional<InvoiceDTO> updatedInvoice = invoiceService.updateInvoice(id, invoiceDTO);
        return updatedInvoice.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Integer id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }
}