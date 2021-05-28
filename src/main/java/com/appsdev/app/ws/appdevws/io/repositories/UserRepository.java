package com.appsdev.app.ws.appdevws.io.repositories;

import com.appsdev.app.ws.appdevws.io.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {
    //if this does not exist we need to create the DTO and all the other things
    UserEntity findUserEntitiesByEmail(String email);
    UserEntity findUserEntityByUserId(String userId);
    UserEntity findUserEntityByEmailVerificationToken(String token);

    //native queries
    @Query(value = "select * from users where email_verification_status = 'true'",
            nativeQuery = true,
            countQuery = "select count(*) from users where email_verification_status = 'true'")
        //since pagination is used need to provide countQuery
    Page<UserEntity> findAllUsersWithConfirmedEmail(Pageable pageableRequest);

    @Query(value = "select * from users where last_name = :lastName",
            nativeQuery = true)
    List<UserEntity> findUserByLastName(@Param("lastName") String lastName);

    @Query(value = "select * from users where first_name like %:keyword% or first_name like %:keyword%",
            nativeQuery = true)
    List<UserEntity> findUserByKeyword(@Param("keyword") String lastName);

    //select specific columns
    @Query(value = "select first_name, last_name from users where first_name like %:keyword% or first_name like %:keyword%",
            nativeQuery = true)
    List<Object[]> findUserByKeyword_v2(@Param("keyword") String lastName);

    @Transactional
    @Modifying //modifying is used also in delete operation
    @Query(value= "update users set email_verification_status = :emailVerificationStatus where user_id = :userID",
            nativeQuery = true)
    void updateUserEmailVerificationStatus(@Param("emailVerificationStatus") boolean emailVerificationStatus, @Param("userID") String userID);

    //JPQL
    @Query("select user from UserEntity user where user.userId = :userID")
    UserEntity findUserEntityByUserId_v2(@Param("userID") String userID);

    @Query("select user.firstName, user.lastName from UserEntity user where user.userId = :userID")
    List<Object[]> getUserEntitiesFullNameById(@Param("userID") String userID);
}
