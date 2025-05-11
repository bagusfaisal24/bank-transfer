package com.account_bank.controller;

import com.account_bank.service.CheckerSvc;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/checker")
@AllArgsConstructor
public class CheckerController {

    private CheckerSvc checkerSvc;

    @PostMapping("/{id}/approve")
    public ResponseEntity<Object> approve(@PathVariable Long id) {
        return checkerSvc.approve(id);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Object> reject(@PathVariable Long id) {
        return checkerSvc.reject(id);
    }
}
