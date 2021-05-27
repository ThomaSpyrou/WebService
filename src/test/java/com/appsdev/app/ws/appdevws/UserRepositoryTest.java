package com.appsdev.app.ws.appdevws;

import com.appsdev.app.ws.appdevws.io.entity.UserEntity;
import com.appsdev.app.ws.appdevws.io.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest //an integration test since we need to select/update the DB
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    UserEntity userEntity;
    String userId = "fef32";
    String encryptedPassword = "32rfw3";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setFirstName("thomas");
        userEntity.setLastName("sth");
        userEntity.setUserId(userId);
        userEntity.setEncryptedPassword(encryptedPassword);
        userEntity.setEmailVerificationToken("sdfgh");
        userEntity.setEmail("test@test.com");
        userEntity.setEmailVerificationStatus(true);
        userRepository.save(userEntity);
    }

    @Test
    final void testGetVerifiedUsers() {
        Pageable pageableRequest = PageRequest.of(0, 2);
        Page<UserEntity> pages = userRepository.findAllUsersWithConfirmedEmail(pageableRequest);
        List<UserEntity> userEntityList = pages.getContent();

        assertEquals(1, userEntityList.size());
        assertNotNull(pages);
    }

    @Test
    final void testFindUserByLastName(){
        String lastName = "sth";
        List<UserEntity> users = userRepository.findUserByLastName(lastName);
        assertNotNull(users);

        UserEntity user = users.get(0);
        assertEquals(user.getLastName(), lastName);
    }

    @Test
    final void testFindUserByKeyword(){
        String keyword = "th";
        List<UserEntity> users = userRepository.findUserByKeyword(keyword);
        assertNotNull(users);
    }

    @Test
    final void testFindUserByKeyword_v2(){
        String keyword = "th";
        List<Object[]> users = userRepository.findUserByKeyword_v2(keyword);
        assertNotNull(users);

        Object[] user = users.get(0);
        String user_first_name = String.valueOf(user[0]);
        String user_last_name = String.valueOf(user[1]);

        System.out.println(user_first_name + " "+ user_last_name);

        assertNotNull(user_first_name);
        assertNotNull(user_last_name);
    }

    @Test
    final void testUpdateUserEmailVerificationStatus(){
        boolean emailVerificationStatus = false;
        userRepository.updateUserEmailVerificationStatus(emailVerificationStatus, userId);
        UserEntity storedDetailed = userRepository.findUserEntityByUserId(userId);

        boolean storedEmailVerificationStatus = storedDetailed.getEmailVerificationStatus();

        assertEquals(storedEmailVerificationStatus, emailVerificationStatus);
    }

    @Test
    final void testFindUserEntityByUserId_v2(){
        UserEntity user = userRepository.findUserEntityByUserId_v2(userId);

        assertNotNull(user);
        assertEquals(user.getUserId(), userId);
    }

    @Test
    final void testGetUserEntitiesFullNameById(){
        List<Object[]> users = userRepository.getUserEntitiesFullNameById(userId);

        assertNotNull(users);
    }
}

