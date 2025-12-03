package org.dgu.programbook.domain.movie.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class AnalysisResponse {
    private List<String> prompt2results;
    private List<RetrievalResult> retrieval2uris;
    private String thumbnail_folder_uri;
}
