package org.dgu.programbook.domain.user.repository;

import org.dgu.programbook.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

}
