package org.dgu.programbook.domain.s3.controller;

import lombok.RequiredArgsConstructor;
import org.dgu.programbook.domain.s3.dto.request.TranscribeJobRequestDTO;
import org.dgu.programbook.domain.s3.dto.response.TranscribeJobResponseDTO;
import org.dgu.programbook.domain.s3.dto.response.TranscribeResultResponseDTO;
import org.dgu.programbook.domain.s3.service.TranscribeService;
import org.dgu.programbook.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transcribe")
public class TranscribeController {

    private final TranscribeService transcribeService;

    @PostMapping("/start")
    public ResponseEntity<SuccessResponse<?>> start(@RequestBody TranscribeJobRequestDTO transcribeJobRequest) {
        TranscribeJobResponseDTO jobName = transcribeService.startJob(transcribeJobRequest);
        return SuccessResponse.ok(jobName);
    }

    @GetMapping("/result")
    public ResponseEntity<SuccessResponse<?>> result(@RequestParam String jobName) {
        TranscribeResultResponseDTO transcribeResult = transcribeService.getTranscriptUrl(jobName);
        return SuccessResponse.ok(transcribeResult);
    }
}
