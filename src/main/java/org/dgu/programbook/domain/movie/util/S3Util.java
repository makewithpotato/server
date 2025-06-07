package org.dgu.programbook.domain.movie.util;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dgu.programbook.global.error.ErrorCode;
import org.dgu.programbook.global.error.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Slf4j
public class S3Util {


    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.dir}")
    private String dir;

    //이미지 업로드
    public String upload(MultipartFile file) {
        try {
            String key = dir + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();
            s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));

            return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
        } catch (Exception e) {
            // 파일이 손상되었거나, 읽을 수 없는 경우
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
    }
    public List<String> upload(List<MultipartFile> files) {
        return files.stream()
                .map(this::upload)
                .collect(Collectors.toList());
    }


    // 영상 분할 + S3 업로드
    public List<String> uploadVideoParts(MultipartFile videoFile) {
        try {
            File original = toFile(videoFile);
            File splitDir = splitVideo(original);
            List<MultipartFile> parts = getSplitPartsAsMultipartFiles(splitDir);
            return upload(parts);
        } catch (Exception e) {
            log.error("영상 업로드 실패", e);
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
    }

    // 내부: Multipart → File 변환
    private File toFile(MultipartFile multipartFile) throws IOException {
        File conv = File.createTempFile("temp_", multipartFile.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(conv)) {
            fos.write(multipartFile.getBytes());
        }
        return conv;
    }

    // 내부: FFmpeg로 분할 (5분 단위)
    private File splitVideo(File originalFile) throws IOException, InterruptedException {
        File outputDir = Files.createTempDirectory("split_videos").toFile();
        String outputPattern = new File(outputDir, "part_%03d.mp4").getAbsolutePath();

        String[] command = {
                "ffmpeg", "-i", originalFile.getAbsolutePath(),
                "-c", "copy", "-map", "0",
                "-segment_time", "300",
                "-f", "segment", "-reset_timestamps", "1",
                outputPattern
        };

        Process process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while (reader.readLine() != null) { /* 생략 가능 */ }
        }

        if (process.waitFor() != 0) {
            throw new RuntimeException("영상 분할 실패");
        }

        return outputDir;
    }

    // 내부: File[] → MultipartFile[]
    private List<MultipartFile> getSplitPartsAsMultipartFiles(File splitDir) throws IOException {
        File[] files = splitDir.listFiles((dir, name) -> name.endsWith(".mp4"));
        if (files == null || files.length == 0) {
            throw new RuntimeException("분할된 영상 없음");
        }

        List<MultipartFile> result = new ArrayList<>();
        for (File f : files) {
            try (FileInputStream fis = new FileInputStream(f)) {
                result.add(new MockMultipartFile(
                        f.getName(),
                        f.getName(),
                        Files.probeContentType(f.toPath()),
                        fis
                ));
            }
            f.delete(); // 임시 분할 파일 삭제
        }
        splitDir.delete(); // 디렉토리 삭제
        return result;
    }


}