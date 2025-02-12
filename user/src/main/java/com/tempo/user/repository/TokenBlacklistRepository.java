package com.tempo.user.repository;

import com.tempo.user.security.TokenBlacklist;
import org.springframework.data.repository.CrudRepository;

public interface TokenBlacklistRepository extends CrudRepository<TokenBlacklist, String> {
    boolean existsByToken(String token);
}