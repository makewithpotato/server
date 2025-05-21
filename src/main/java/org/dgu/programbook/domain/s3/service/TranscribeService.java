package org.dgu.programbook.domain.s3.service;

import lombok.RequiredArgsConstructor;
import org.dgu.programbook.domain.s3.controller.TranscribeJobRequestDTO;
import org.dgu.programbook.domain.s3.dto.response.TranscribeJobResponseDTO;
import org.dgu.programbook.domain.s3.dto.response.TranscribeResultResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.transcribe.TranscribeClient;
import software.amazon.awssdk.services.transcribe.model.*;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TranscribeService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final TranscribeClient transcribeClient;

    //Transcribe Job 처리 요청
    public TranscribeJobResponseDTO startJob(TranscribeJobRequestDTO transcribeJobRequest) {
        String jobName = "job-" + UUID.randomUUID();

        // 필요한 AWS Transcribe 설정을 추가
        StartTranscriptionJobRequest request = StartTranscriptionJobRequest.builder()
                .transcriptionJobName(jobName)
                .languageCode(LanguageCode.KO_KR)
                .mediaFormat("mp4")
                .outputBucketName(bucketName)
                .media(Media.builder()
                        .mediaFileUri(transcribeJobRequest.s3Uri())
                        .build())
                .settings(Settings.builder()
                        .showSpeakerLabels(true)    // 화자 분리 기능 활성화
                        .maxSpeakerLabels(10)       // 최대 화자 수 지정 (일치하면 정확도 향상. 실제 화자수 보다 커도 상관은 X. 작으면 오류)
                        .build())
                .build();

        transcribeClient.startTranscriptionJob(request);
        return new TranscribeJobResponseDTO(jobName);
    }


    // Transcribe Job에 대한 처리 결과 조회
    public TranscribeResultResponse getTranscriptUrl(String jobName) {
        GetTranscriptionJobResponse response = transcribeClient.getTranscriptionJob(
                GetTranscriptionJobRequest.builder()
                        .transcriptionJobName(jobName)
                        .build()
        );

        TranscriptionJob job = response.transcriptionJob();
        TranscriptionJobStatus status = job.transcriptionJobStatus();

        String resultUrl = switch (status) {
            case COMPLETED -> job.transcript().transcriptFileUri();
            case FAILED    -> "처리 실패: " + job.failureReason();
            default        -> "진행 상태: " + status.name();
        };
        return new TranscribeResultResponse(resultUrl);
    }
}
