package com.account_bank.controller;

import com.account_bank.form.BulkForm;
import com.account_bank.form.TransactionForm;
import com.account_bank.model.Payment;
import com.account_bank.service.BulkImportSvc;
import com.account_bank.service.MakerSvc;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/v1/maker")
@AllArgsConstructor
public class MakerController {

    private MakerSvc makerSvc;
    private BulkImportSvc bulkImportSvc;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody TransactionForm form) {
        return makerSvc.create(form);
    }

    @GetMapping("/generate-templates")
    public ResponseEntity<Resource> generateTemplate(@RequestBody BulkForm form) throws IOException {
        return bulkImportSvc.downloadTemplate(form);
    }

    @PostMapping("/import-bulk")
    public ResponseEntity<Object> createPaymentBulk(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        File file = bulkImportSvc.convertMultipartFileToFile(multipartFile);
        return bulkImportSvc.paymentBulk(file);
    }
}
