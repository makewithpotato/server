package org.dgu.programbook.domain.movie.util;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dgu.programbook.domain.movie.dto.request.PartEtagDto;
import org.dgu.programbook.domain.movie.dto.response.CreateUploadResponseDto;
import org.dgu.programbook.domain.movie.dto.response.PresignedPart;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.awscore.presigner.PresignedRequest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.time.Duration;
import java.util.*;

@RequiredArgsConstructor
@Component
@Slf4j
public class S3Util {


    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.s3.dir}")
    private String dir;

//    //이미지 업로드
//    public String upload(MultipartFile file) {
//        try {
//            String key = dir + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
//            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(key)
//                    .contentType(file.getContentType())
//                    .build();
//            s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));
//
//            return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
//        } catch (Exception e) {
//            // 파일이 손상되었거나, 읽을 수 없는 경우
//            throw new BusinessException(ErrorCode.BAD_REQUEST);
//        }
//    }
//    public List<String> upload(List<MultipartFile> files) {
//        return files.stream()
//                .map(this::upload)
//                .collect(Collectors.toList());
//    }
//
//
//    // 영상 분할 + S3 업로드
//    public List<String> uploadVideoParts(MultipartFile videoFile) {
//        try {
//            File original = toFile(videoFile);
//            File splitDir = splitVideo(original);
//            List<MultipartFile> parts = getSplitPartsAsMultipartFiles(splitDir);
//            return upload(parts);
//        } catch (Exception e) {
//            log.error("영상 업로드 실패", e);
//            throw new BusinessException(ErrorCode.BAD_REQUEST);
//        }
//    }
//
//    // 내부: Multipart → File 변환
//    private File toFile(MultipartFile multipartFile) throws IOException {
//        File conv = File.createTempFile("temp_", multipartFile.getOriginalFilename());
//        try (FileOutputStream fos = new FileOutputStream(conv)) {
//            fos.write(multipartFile.getBytes());
//        }
//        return conv;
//    }
//
//    // 내부: FFmpeg로 분할 (5분 단위)
//    private File splitVideo(File originalFile) throws IOException, InterruptedException {
//        File outputDir = Files.createTempDirectory("split_videos").toFile();
//        String outputPattern = new File(outputDir, "part_%03d.mp4").getAbsolutePath();
//
//        String[] command = {
//                "ffmpeg", "-i", originalFile.getAbsolutePath(),
//                "-c", "copy", "-map", "0",
//                "-segment_time", "300",
//                "-f", "segment", "-reset_timestamps", "1",
//                outputPattern
//        };
//
//        Process process = new ProcessBuilder(command)
//                .redirectErrorStream(true)
//                .start();
//
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//            while (reader.readLine() != null) { /* 생략 가능 */ }
//        }
//
//        if (process.waitFor() != 0) {
//            throw new RuntimeException("영상 분할 실패");
//        }
//
//        return outputDir;
//    }
//
//    // 내부: File[] → MultipartFile[]
//    private List<MultipartFile> getSplitPartsAsMultipartFiles(File splitDir) throws IOException {
//        File[] files = splitDir.listFiles((dir, name) -> name.endsWith(".mp4"));
//        if (files == null || files.length == 0) {
//            throw new RuntimeException("분할된 영상 없음");
//        }
//
//        List<MultipartFile> result = new ArrayList<>();
//        for (File f : files) {
//            try (FileInputStream fis = new FileInputStream(f)) {
//                result.add(new MockMultipartFile(
//                        f.getName(),
//                        f.getName(),
//                        Files.probeContentType(f.toPath()),
//                        fis
//                ));
//            }
//            f.delete(); // 임시 분할 파일 삭제
//        }
//        splitDir.delete(); // 디렉토리 삭제
//        return result;
//    }
//================================ S3 멀티 파트 + PresignedUrl 도입 ===================================//

    public CreateUploadResponseDto initiateMultipartUpload(Long totalParts) {

        // S3에 업로드될 파일 경로 지정
        String objectKey = dir + "/" + UUID.randomUUID(); // 예: dir/uuid/

        // 멀티파트 업로드 시작 요청
        CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
        CreateMultipartUploadResponse response = s3Client.createMultipartUpload(createRequest);

        String uploadId = response.uploadId();
        List<PresignedPart> presignedParts = new ArrayList<>();

        for (int i = 0; i < totalParts; i++) {
            int partNumber = i + 1;
            UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .uploadId(uploadId)
                    .partNumber(partNumber)
                    .build();

            PresignedRequest presigned = s3Presigner.presignUploadPart(
                    b -> b.signatureDuration(Duration.ofMinutes(10)).uploadPartRequest(uploadPartRequest)
            );

            presignedParts.add(
                    PresignedPart.builder()
                            .partNumber(partNumber)
                            .presignedUrl(presigned.url().toString())
                            .build()
            );
        }

        return CreateUploadResponseDto.builder()
                .uploadId(uploadId)
                .objectKey(objectKey)
                .presignedParts(presignedParts)
                .build();
    }


    public String completeMultipartUpload(String uploadId, String key, List<PartEtagDto> parts) {
        CompletedMultipartUpload completedUpload = CompletedMultipartUpload.builder()
                .parts(parts.stream()
                        .map(p -> CompletedPart.builder()
                                .partNumber(p.partNumber())
                                .eTag(p.etag())
                                .build())
                        .toList())
                .build();

        CompleteMultipartUploadRequest request = CompleteMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(key)
                .uploadId(uploadId)
                .multipartUpload(completedUpload)
                .build();

        s3Client.completeMultipartUpload(request);
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
    }

}