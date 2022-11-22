package be.rubus.workshop.security.challenge3;


import be.rubus.workshop.security.challenge3.model.User;
import be.rubus.workshop.security.challenge3.model.UserGroup;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.util.HashMap;
import java.util.Map;


@Singleton
@Startup  // So that the init() method is executed when application is started by the runtime.
public class DatabaseSetup {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private Pbkdf2PasswordHash passwordHash;

    @PostConstruct
    public void init() {

        Map<String, String> parameters = new HashMap<>();
        parameters.put("Pbkdf2PasswordHash.Iterations", "100000");
        parameters.put("Pbkdf2PasswordHash.Algorithm", "PBKDF2WithHmacSHA512");
        parameters.put("Pbkdf2PasswordHash.SaltSizeBytes", "64");
        passwordHash.initialize(parameters);

        entityManager.persist(newUser("rudy", passwordHash.generate("secret1".toCharArray())));
        entityManager.persist(newUser("user", passwordHash.generate("pw".toCharArray())));

        entityManager.persist(newUserGroup("rudy", "foo"));
        entityManager.persist(newUserGroup("rudy", "bar"));
        entityManager.persist(newUserGroup("user", "bar"));
    }

    private UserGroup newUserGroup(String userName, String groupName) {
        UserGroup result = new UserGroup();
        result.setUserName(userName);
        result.setGroupName(groupName);
        return result;
    }

    private User newUser(String name, String password) {
        User result = new User();
        result.setName(name);
        result.setPassword(password);
        return result;
    }


}
