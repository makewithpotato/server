package org.dgu.programbook.domain.programbook.repository;

import org.dgu.programbook.domain.programbook.entity.Programbook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgrambookRepository extends JpaRepository<Programbook, Long> {
    List<Programbook> findAllByUserId(Long userId);
}