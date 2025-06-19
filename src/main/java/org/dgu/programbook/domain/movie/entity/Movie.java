package org.dgu.programbook.domain.movie.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dgu.programbook.domain.user.entity.User;
import org.dgu.programbook.global.common.BaseTimeEntity;

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

    private String summary;

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @Builder(builderMethodName = "movieBuilder")
    public Movie(Long id, String title, String director, String actor, String genre, LocalDate releaseDate, String thumbnailUrl, String summary, String status, User user) {
        this.id = id;
        this.title = title;
        this.director = director;
        this.actor = actor;
        this.genre = genre;
        this.releaseDate = releaseDate;
        this.thumbnailUrl = thumbnailUrl;
        this.summary = summary;
        this.status = status;
        this.user = user;
    }
}
