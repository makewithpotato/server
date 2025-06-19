package org.dgu.programbook.domain.movie.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUploadResponseDto {
    private Long movieId;
    private String uploadId;
    private String objectKey;
    private List<PresignedPart> presignedParts;
}
