package com.dxc.iotbackend;

import com.dxc.iotbackend.model.User;
import com.dxc.iotbackend.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

@DataJpaTest //this test class will run as a datajpa test
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class UserRepositoryTests {
    @Autowired
    private UserRepository repo;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateUser(){
        User user= new User();
        user.setEmail("suz@gmail.com");
        user.setPassword("1234");
        user.setFirstName("Suzn");
        user.setLastName("Roshy");
        User savedUser=repo.save(user);
        System.out.println("Saved user ID: " + savedUser.getId());

       User existUser= entityManager.find(User.class, savedUser.getId());
        System.out.println("Found user: " + existUser);

        assertThat(existUser).isNotNull();
       assertThat(existUser.getEmail()).isEqualTo(user.getEmail());
    }
}

