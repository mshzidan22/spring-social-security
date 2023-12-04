package com.mshzidan.mvpsecurity.repository;

import com.mshzidan.mvpsecurity.model.EmailCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailRepository  extends JpaRepository<EmailCode, Long> {

    Optional<EmailCode> findByCode(String code);
}
