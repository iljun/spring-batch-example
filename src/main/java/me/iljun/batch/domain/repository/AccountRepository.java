package me.iljun.batch.domain.repository;

import me.iljun.batch.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT MAX(id) FROM Account")
    Long findMaxId();

}
