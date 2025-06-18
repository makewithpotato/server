package org.dgu.programbook.domain.user.repository;

import org.dgu.programbook.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findBySocialId(String socialId);
}
