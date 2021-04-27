package com.events.users;

import com.events.Enums.UserAccountStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository repo;

    public List<User> listAll() {
        return repo.findAll();
    }

    public User save(User user) {
        return repo.save(user);
    }

    public User get(Integer id) {
        Optional<User> user = repo.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        throw new NoSuchElementException("Unable to find user with id: " + id);
    }

    public User getByPrinciple(OidcUser principle) {
        List<User> allUsers = repo.findAll();
        String email = principle.getEmail();
        for (User user : allUsers) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return user;
            }
        }
        String name = principle.getFullName();
        return this.save(new User(name, email));
    }
    public User getByEmail(String email) {
        List<User> allUsers = repo.findAll();
        for (User user : allUsers) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return user;
            }
        }

        throw new NoSuchElementException("Unable to find user with email: " + email);
    }


    public void delete(Integer id) {
        User user = get(id);
        user.setStatus(UserAccountStatus.DELETED);
        save(user);
    }

    public User update(User user) {
        Optional<User> userOptional = repo.findById(user.getUserId());
        if (userOptional.isPresent()) {
            return save(userOptional.get());
        }
        throw new NoSuchElementException("Unable to find user with id: " + user.getUserId());
    }


}
