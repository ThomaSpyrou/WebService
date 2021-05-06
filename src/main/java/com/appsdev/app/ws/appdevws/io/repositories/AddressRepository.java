package com.appsdev.app.ws.appdevws.io.repositories;

import com.appsdev.app.ws.appdevws.io.entity.AddressEntity;
import com.appsdev.app.ws.appdevws.io.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity, Long> {
    List<AddressEntity> findAllByUserDetails(UserEntity userEntity);
}
