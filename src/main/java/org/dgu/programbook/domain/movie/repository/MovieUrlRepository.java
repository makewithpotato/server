package org.dgu.programbook.domain.movie.repository;

import org.dgu.programbook.domain.movie.entity.Movie;
import org.dgu.programbook.domain.movie.entity.MovieUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieUrlRepository extends JpaRepository<MovieUrl, Long> {


}
