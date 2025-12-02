package org.dgu.programbook.domain.movie.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dgu.programbook.domain.user.entity.User;
import org.dgu.programbook.global.common.BaseTimeEntity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Table(name = "movie")
@Entity
public class Movie extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String director;

    private String actor;

    private String genre;

    private LocalDate releaseDate;

    private String thumbnailUrl;

    @Column(columnDefinition = "TEXT")
    private String summary;

    private String status;

    @Column(columnDefinition = "TEXT")
    private String review;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private String[] custom_prompts;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private String[] custom_retrievals;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @Builder(builderMethodName = "movieBuilder")
    public Movie(User user, String review, String status, String summary, String thumbnailUrl,
                 LocalDate releaseDate, String genre, String actor, String director, String title,
                 Long id, String[] custom_prompts, String[] custom_retrievals) {
        this.user = user;
        this.review = review;
        this.status = status;
        this.summary = summary;
        this.thumbnailUrl = thumbnailUrl;
        this.releaseDate = releaseDate;
        this.genre = genre;
        this.actor = actor;
        this.director = director;
        this.title = title;
        this.id = id;
        this.custom_prompts=custom_prompts;
        this.custom_retrievals=custom_retrievals;
    }

    public void updateAnalysisResult(String thumbnailUrl, String review ,String summary) {
        this.thumbnailUrl = thumbnailUrl;
        this.review = review;
        this.summary = summary;
    }

    public void updateStatus(String status) {
        this.status = status;
    }
}
