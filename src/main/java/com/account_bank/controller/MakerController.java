package com.account_bank.controller;

import com.account_bank.config.RateLimiterSvc;
import com.account_bank.config.ResponseMessage;
import com.account_bank.config.ResponseUtil;
import com.account_bank.form.BulkForm;
import com.account_bank.form.TransactionForm;
import com.account_bank.service.BulkImportSvc;
import com.account_bank.service.MakerSvc;
import io.github.bucket4j.Bucket;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/v1/maker")
@AllArgsConstructor
public class MakerController {

    private BulkImportSvc bulkImportSvc;
    private MakerSvc makerSvc;
    private RateLimiterSvc rateLimiterSvc;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody TransactionForm form, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        Bucket bucket = rateLimiterSvc.resolveBucket(ip);
        if (bucket.tryConsume(1)) {
            return makerSvc.create(form);
        } else {
            return ResponseUtil.build(ResponseMessage.TO_MANY_REQUEST, null, HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    @GetMapping("/generate-templates")
    public ResponseEntity<Resource> generateTemplate(@RequestBody BulkForm form) throws IOException {
        return bulkImportSvc.downloadTemplate(form);
    }

    @PostMapping("/import-bulk")
    public ResponseEntity<Object> createPaymentBulk(@RequestParam("file") MultipartFile multipartFile,
                                                    HttpServletRequest request) throws IOException {
        String ip = request.getRemoteAddr();
        Bucket bucket = rateLimiterSvc.resolveBucket(ip);
        if (bucket.tryConsume(1)) {
            File file = bulkImportSvc.convertMultipartFileToFile(multipartFile);
            return bulkImportSvc.paymentBulk(file);
        } else {
            return ResponseUtil.build(ResponseMessage.TO_MANY_REQUEST, null, HttpStatus.TOO_MANY_REQUESTS);
        }
    }
}
