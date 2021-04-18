package com.appsdev.app.ws.appdevws.io.repositories;

import com.appsdev.app.ws.appdevws.io.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {
    //if this does not exist we need to create the DTO and all the other things
    UserEntity findUserEntitiesByEmail(String email);
    UserEntity findUserEntityByUserId(String userId);
}
