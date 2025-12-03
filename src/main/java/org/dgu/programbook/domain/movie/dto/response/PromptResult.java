package org.dgu.programbook.domain.movie.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PromptResult {
    private String prompt;
    private String result;
}
