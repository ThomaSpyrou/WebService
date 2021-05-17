package com.appsdev.app.ws.appdevws.io.repositories;

import com.appsdev.app.ws.appdevws.io.entity.PasswordResetTokenEntity;
import org.springframework.data.repository.CrudRepository;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetTokenEntity, Long> {
    PasswordResetTokenEntity findByToken(String token);
}
