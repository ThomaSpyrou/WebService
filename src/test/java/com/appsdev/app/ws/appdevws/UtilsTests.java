package com.appsdev.app.ws.appdevws;

import com.appsdev.app.ws.appdevws.shared.dto.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.*;


//integration tests
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UtilsTests {

    @Autowired
    Utils utils;

    @BeforeEach
    void setUp() throws Exception{

    }

    @Test
    final void testGenerateUserId() {
        String userId = utils.generateUserId(30);
        String userId_2 = utils.generateUserId(30);

        assertNotNull(userId);
        assertEquals(30, userId.length());
        assertNotEquals(userId, userId_2);
    }

    @Test
    //@Disabled if i want to ignore the test
    final void testHasTokenNotExpired() {
        String token = utils.generateEmailVerificationToken("21erfr43a");
        assertNotNull(token);

        boolean hasTokenExpired = Utils.hasTokenExpired(token);
        assertFalse(hasTokenExpired);
    }

    @Test
    final void testHasTokenExpired(){
        String token = "23frgth7j6u5y5gfedsx";
        boolean hasTokenExpired = Utils.hasTokenExpired(token);
        assertTrue(hasTokenExpired);
    }
}
