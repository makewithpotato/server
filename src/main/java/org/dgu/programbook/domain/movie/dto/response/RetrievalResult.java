package org.dgu.programbook.domain.movie.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RetrievalResult {
    private String scene;
    private List<String> uri;
}
