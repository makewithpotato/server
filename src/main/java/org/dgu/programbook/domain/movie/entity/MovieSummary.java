package org.dgu.programbook.domain.movie.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "moviemanager_summary")
public class MovieSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long summaryId;

    @Column(columnDefinition = "text")
    private String summaryText;

    private Long movieId;

}
