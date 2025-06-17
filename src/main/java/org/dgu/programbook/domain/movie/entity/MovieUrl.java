package org.dgu.programbook.domain.movie.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Table(name = "movie_url")
@Entity
public class MovieUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @Column(name = "movie_url")
    private String movieUrl;

    @Builder
    public MovieUrl(Long id, Movie movie, String movieUrl) {
        this.id = id;
        this.movie = movie;
        this.movieUrl = movieUrl;
    }
}
