package com.gfg.jbdl12majorproject.userservice.repository;

import com.gfg.jbdl12majorproject.userservice.entities.PaymentUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<PaymentUser,Long> {
    Optional<PaymentUser> findByUsername(String s);
}
