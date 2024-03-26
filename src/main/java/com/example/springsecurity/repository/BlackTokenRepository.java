package com.example.springsecurity.repository;

import com.example.springsecurity.entity.BlackToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackTokenRepository extends CrudRepository<BlackToken, String> {
}
