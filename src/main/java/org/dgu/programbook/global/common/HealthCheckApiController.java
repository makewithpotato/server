package org.dgu.programbook.global.common;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class HealthCheckApiController {

    @RequestMapping("/")
    public String ProgrambookServer() {
        return "Hello! Programbook Server!";
    }

}