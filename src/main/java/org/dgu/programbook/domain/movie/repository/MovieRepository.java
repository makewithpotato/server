package org.dgu.programbook.domain.movie.repository;

import org.dgu.programbook.domain.movie.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Long, Movie> {

    List<Movie> findAllByUserId(Long userId);
    void save(Movie movie);


    Optional<Movie> findByUserId(Long movieId);

}
