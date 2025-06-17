package org.dgu.programbook.domain.programbook.controller;

import lombok.RequiredArgsConstructor;
import org.dgu.programbook.domain.programbook.dto.response.ProgrambookResponseDTO;
import org.dgu.programbook.domain.programbook.service.ProgrambookService;
import org.dgu.programbook.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/programbook")
public class ProgrambookController {

    private final ProgrambookService programbookService;

    //프로그램북 리스트 조회
    @GetMapping
    ResponseEntity<SuccessResponse<?>> getProgrambookList(@AuthenticationPrincipal Long userId){
        List<ProgrambookResponseDTO> programbookList = programbookService.getProgrambookList(userId);
        return SuccessResponse.ok(programbookList);
    }

    //프로그램북 상세 조회


    //프로그램북 저장(pdf)

}
