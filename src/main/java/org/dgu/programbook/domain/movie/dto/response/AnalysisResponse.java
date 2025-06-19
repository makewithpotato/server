package org.dgu.programbook.domain.movie.dto.response;

import lombok.Data;

@Data
public class AnalysisResponse {
    private String thumbnail_folder_uri;
    private String final_story;
    private String final_review;
}
