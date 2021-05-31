package com.appsdev.app.ws.appdevws;

import com.appsdev.app.ws.appdevws.io.entity.AuthorityEntity;
import com.appsdev.app.ws.appdevws.io.entity.RoleEntity;
import com.appsdev.app.ws.appdevws.io.entity.UserEntity;
import com.appsdev.app.ws.appdevws.io.repositories.AuthorityRepository;
import com.appsdev.app.ws.appdevws.io.repositories.RoleRepository;
import com.appsdev.app.ws.appdevws.io.repositories.UserRepository;
import com.appsdev.app.ws.appdevws.shared.dto.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collection;

@Component
public class InitUserSetup {

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    UserRepository userRepository;

    @EventListener
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event){
        System.out.println("Application Started");
        AuthorityEntity readAuthority = createAuthority("READ_AUTHORITY");
        AuthorityEntity writeAuthority = createAuthority("WRITE_AUTHORITY");
        AuthorityEntity deleteAuthority = createAuthority("DELETE_AUTHORITY");

        RoleEntity roleUser = createRole("ROLE_USER", Arrays.asList(readAuthority, writeAuthority));
        RoleEntity roleAdmin = createRole("ADMIN_USER", Arrays.asList(readAuthority, writeAuthority, deleteAuthority));

        if (roleAdmin == null){
            return;
        }

        UserEntity adminUser = new UserEntity();
        adminUser.setFirstName("thomas");
        adminUser.setLastName("spyrou");
        adminUser.setEmail("test@");
        adminUser.setEmailVerificationStatus(true);
        adminUser.setUserId(utils.generateUserId(30));
        adminUser.setEncryptedPassword(bCryptPasswordEncoder.encode("213456"));
        adminUser.setRoles(Arrays.asList(roleAdmin));

        userRepository.save(adminUser);
    }

    @Transactional
    private AuthorityEntity createAuthority(String name){
        AuthorityEntity authorityEntity = authorityRepository.findByName(name);

        if(authorityEntity == null){
            authorityEntity = new AuthorityEntity(name);
            authorityRepository.save(authorityEntity);
        }

        return authorityEntity;
    }

    @Transactional
    private RoleEntity createRole(String name, Collection<AuthorityEntity> authorities){
        RoleEntity roleEntity = roleRepository.findByName(name);

        if(roleEntity == null){
            roleEntity =  new RoleEntity(name);
            roleEntity.setAuthorities(authorities);
            roleRepository.save(roleEntity);
        }

        return roleEntity;
    }
}
