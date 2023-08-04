package com.example.storeproject.controller;

import com.example.storeproject.model.ArrivalForm;
import com.example.storeproject.service.KioskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KioskController {
    private final KioskService kioskService;

    @PutMapping("/kiosk/arrival")
    public ResponseEntity<?> confirmArrival(@RequestBody ArrivalForm form) {
        return ResponseEntity.ok(kioskService.confirmArrival(form));
    }
}
