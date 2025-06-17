package org.dgu.programbook.domain.programbook.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dgu.programbook.domain.user.entity.User;
import org.dgu.programbook.global.common.BaseTimeEntity;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Table(name = "programbook")
@Entity
public class Programbook extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String thumbnailUrl;

    private String pdfUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "user_id")
    private User user;

}
