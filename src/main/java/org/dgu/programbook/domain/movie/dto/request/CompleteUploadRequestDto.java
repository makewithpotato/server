package org.dgu.programbook.domain.movie.dto.request;

import lombok.Builder;
import java.util.List;


@Builder
public record CompleteUploadRequestDto(
        String title,
        String uploadId,
        String objectKey,
        List<PartEtagDto> presignedParts
) {
}
