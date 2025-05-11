package com.account_bank.maker;

import com.account_bank.ApplicationTest;
import com.account_bank.config.RequestHandler;
import com.account_bank.cons.TransactionType;
import com.account_bank.form.TransactionForm;
import com.account_bank.model.AccountBank;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.Data;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:sql/cleandata.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:sql/account.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:sql/portfolio.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sql/cleandata.sql"),
})
public class TestController {

    private static final String maker = "/v1/maker";
    private static final String account = "/v1/account";
    private static final String checker = "/v1/checker";

    @Autowired
    private TestRestTemplate testRestTemplate;

    private static final ObjectMapper mapper = new ObjectMapper();


    @Test
    public void test0SizeOfAccount() throws JsonProcessingException {
        CollectionType javaType = RequestHandler.createCollectionType(AccountBank.class);
        ResponseEntity<String> result = testRestTemplate.getForEntity(account, String.class);
        List<AccountBank> list = mapper.readValue(result.getBody(), javaType);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(3, list.size());
    }

    @Test
    public void test1TransferSingle() throws JsonProcessingException {
        TransactionForm form = TransactionForm.builder()
                .amount(1000000d)
                .destinationAccount("111222")
                .sourceAccount("112233")
                .transactionType(TransactionType.SINGLE)
                .build();
        HttpEntity<String> request = RequestHandler.createRequest(form);
        ResponseEntity<String> result = testRestTemplate.postForEntity(maker, request, String.class);
        Response response = mapper.readValue(result.getBody(), Response.class);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("Success create payment", response.getMessage());
    }

    @Test
    public void test2OneToManyTransfer() throws JsonProcessingException {

    }

    @Test
    public void test3RejectPaymentChecker() throws JsonProcessingException {
        TransactionForm form = TransactionForm.builder()
                .amount(500000d)
                .destinationAccount("111222")
                .sourceAccount("112233")
                .transactionType(TransactionType.SINGLE)
                .build();
        HttpEntity<String> request = RequestHandler.createRequest(form);
        ResponseEntity<String> result = testRestTemplate.postForEntity(maker, request, String.class);
        Response response = mapper.readValue(result.getBody(), Response.class);
        Long id = Long.valueOf(response.getData().get("id").toString());
        String apiReject = String.format("%s/%d/%s", checker, id, "reject");
        ResponseEntity<String> reject = testRestTemplate.exchange(apiReject, HttpMethod.POST, null, String.class);
        Response responseReject = mapper.readValue(reject.getBody(), Response.class);
        assertEquals(HttpStatus.OK, reject.getStatusCode());
        assertEquals("Success Reject Payment", responseReject.getMessage());
    }

    @Test
    public void test4SaldoTidakCukup() throws JsonProcessingException {
        TransactionForm form = TransactionForm.builder()
                .amount(20000000d)
                .destinationAccount("111222")
                .sourceAccount("112233")
                .transactionType(TransactionType.SINGLE)
                .build();
        HttpEntity<String> request = RequestHandler.createRequest(form);
        ResponseEntity<String> result = testRestTemplate.postForEntity(maker, request, String.class);
        Response response = mapper.readValue(result.getBody(), Response.class);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Insufficent Funds", response.getMessage());
    }

    @Test
    public void test5DestinationAccountNotFound() throws JsonProcessingException {
        TransactionForm form = TransactionForm.builder()
                .amount(500000d)
                .destinationAccount("11111222")
                .sourceAccount("112233")
                .transactionType(TransactionType.SINGLE)
                .build();
        HttpEntity<String> request = RequestHandler.createRequest(form);
        ResponseEntity<String> result = testRestTemplate.postForEntity(maker, request, String.class);
        Response response = mapper.readValue(result.getBody(), Response.class);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Destination account data : Not Found", response.getMessage());
    }

    @Test
    public void test6ReturnTooManyRequestsWhenRateLimitExceeded() throws Exception {
        for (int i=0; i<6; i++){
            TransactionForm form = TransactionForm.builder()
                    .amount(1000000d)
                    .destinationAccount("111222")
                    .sourceAccount("112233")
                    .transactionType(TransactionType.SINGLE)
                    .build();
            HttpEntity<String> request = RequestHandler.createRequest(form);
            ResponseEntity<String> result = testRestTemplate.postForEntity(maker, request, String.class);
            Response response = mapper.readValue(result.getBody(), Response.class);
            assertEquals(HttpStatus.CREATED, result.getStatusCode());
            assertEquals("Success create payment", response.getMessage());
        }

        TransactionForm form = TransactionForm.builder()
                .amount(1000000d)
                .destinationAccount("111222")
                .sourceAccount("112233")
                .transactionType(TransactionType.SINGLE)
                .build();
        HttpEntity<String> request = RequestHandler.createRequest(form);
        ResponseEntity<String> result = testRestTemplate.postForEntity(maker, request, String.class);
        Response response = mapper.readValue(result.getBody(), Response.class);
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, result.getStatusCode());
        assertEquals("Rate limit exceeded", response.getMessage());
    }



    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Response {
        String message;
        Map<String, Object> data;
        boolean error;
    }
}
