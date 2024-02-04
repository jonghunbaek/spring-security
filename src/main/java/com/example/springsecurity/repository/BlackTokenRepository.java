package com.example.springsecurity.repository;

import com.example.springsecurity.entity.BlackToken;
import org.springframework.data.repository.CrudRepository;

public interface BlackTokenRepository extends CrudRepository<BlackToken, String> {
}
