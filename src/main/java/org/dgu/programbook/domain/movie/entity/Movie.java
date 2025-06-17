package org.dgu.programbook.domain.movie.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dgu.programbook.domain.user.entity.User;
import org.dgu.programbook.global.common.BaseTimeEntity;

@Getter
@NoArgsConstructor
@Table(name = "movie")
@Entity
public class Movie extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String thumbnailUrl;

    private String summary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @Builder(builderMethodName = "movieBuilder")
    public Movie(Long id, String title, String thumbnailUrl, String summary, User user) {
        this.id = id;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.summary = summary;
        this.user = user;
    }
}
