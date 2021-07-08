package com.hospital.kpfhirserver;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api")
public class TestController {

    @GetMapping("/test")
    public String getMessage()
    {
        return "Test message";
    }
}
