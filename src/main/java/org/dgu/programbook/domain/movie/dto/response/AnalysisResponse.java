package org.dgu.programbook.domain.movie.dto.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AnalysisResponse {
    private List<List<String>> prompt2results;
    private Map<String, List<String>> retrieval2uris;
    private String thumbnail_folder_uri;
}
