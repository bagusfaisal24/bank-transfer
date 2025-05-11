package com.account_bank.service;

import com.account_bank.config.ResponseUtil;
import com.account_bank.cons.StatusAuthorization;
import com.account_bank.cons.TransactionType;
import com.account_bank.form.BulkForm;
import com.account_bank.model.AccountBank;
import com.account_bank.model.Payment;
import com.account_bank.model.PaymentDetails;
import com.account_bank.model.PortfolioAccount;
import com.account_bank.repository.AccountBankRepository;
import com.account_bank.repository.PaymentDetailsRepository;
import com.account_bank.repository.PortfolioAccountRepository;
import com.account_bank.utils.XLSXHelper;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BulkImportSvc extends XLSXHelper {

    private AccountBankRepository accountBankRepo;
    private PaymentSvc paymentSvc;
    private PaymentDetailsRepository paymentDetailsRepo;
    private PortfolioAccountRepository portfolioAccountRepo;

    public String getOutputTemplate(BulkForm form) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bulk");
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "Source Account",
                "Destination Account",
                "Amount"
        };
        for (int i = 0; i < headers.length; i++) {
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headers[i]);
        }

        int rowIndex = 1;
        for (String dest : form.getDestinationAccount()) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(form.getSourceAccount());
            row.createCell(1).setCellValue(dest);
        }

        // Write to file
        String uuid = UUID.randomUUID().toString();
        String path = "tmp/" + uuid + "_" + LocalDate.now() + "_Bulk-" + ".xlsx";
        FileOutputStream fileOut = new FileOutputStream(path);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
        return path;
    }

    public ResponseEntity<Object> paymentBulk(File file) throws IOException {
        List<PaymentDetails> paymentDetails = details(file);
        Double totalPayment = paymentDetails.stream().mapToDouble(PaymentDetails::getAmount).sum();
        AccountBank source = accountBankRepo.findByAccount_number(paymentDetails.get(0).getSourceAccount());
        PortfolioAccount portfolioAccount = portfolioAccountRepo.findPortfolioAccountByAccountBank(source.getId());
        if (totalPayment > portfolioAccount.getAmount()) {
            return ResponseUtil.build("Insufficent Funds", null, HttpStatus.BAD_REQUEST);
        }
        Payment payment = Payment.builder()
                .transactionId(paymentSvc.transactionId())
                .transactionType(TransactionType.BULK.name())
                .totalAmount(totalPayment)
                .statusAuthorization(StatusAuthorization.SUBMIT)
                .build();
        payment = paymentSvc.save(payment);
        Payment finalPayment = payment;
        paymentDetails = paymentDetails.stream().peek(v->v.setPayment(finalPayment)).collect(Collectors.toList());
        paymentDetailsRepo.saveAll(paymentDetails);
        return ResponseUtil.build("Success create bulk payment", payment, HttpStatus.CREATED);
    }

    public List<PaymentDetails> details(File file) throws IOException {
        List<PaymentDetails> details = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(file);
        Sheet sheet = workbook.getSheetAt(0);
        int i = 0;
        for (Row row : sheet) {
            if (i > 0) {
                details.add(extractRow(sheet, row));
            }
            i++;
        }
        return details;
    }

    private PaymentDetails extractRow(Sheet sheet, Row row) {
        String sourceAccount = getStringValue(getCell(sheet, row.getRowNum(), 0));
        String destinationAccount = getStringValue(getCell(sheet, row.getRowNum(), 1));
        Double amount = getDoubleValue(getCell(sheet, row.getRowNum(), 2));
        return PaymentDetails.builder()
                .sourceAccount(sourceAccount)
                .destinationAccount(destinationAccount)
                .amount(amount)
                .build();
    }

    public ResponseEntity<Resource> downloadTemplate(BulkForm form) throws IOException {
        String pathTemplate = getOutputTemplate(form);
        Path path = Paths.get(pathTemplate);
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    public File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File convFile = File.createTempFile("upload-", multipartFile.getOriginalFilename());
        multipartFile.transferTo(convFile);
        return convFile;
    }
}
