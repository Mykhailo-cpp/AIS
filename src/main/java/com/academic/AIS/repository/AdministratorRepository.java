package com.academic.AIS.repository;

import com.academic.AIS.model.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Integer> {
    @Query("SELECT a FROM Administrator a WHERE a.user.username = :username")
    Optional<Administrator> findByUsername(@Param("username") String username);
}