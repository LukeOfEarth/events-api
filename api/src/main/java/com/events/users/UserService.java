package com.events.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.events.Enums.UserAccountStatus;

@Service
public class UserService {
    @Autowired
    private UserRepository repo;

    public List<User> listAll() {
        return repo.findAll();
    }

    public void save(User user) {
        repo.save(user);
    }

    public User get(Integer id) {
        Optional<User> user = repo.findById(id);
        if(user.isPresent()){
            return user.get();
        }
        throw new NoSuchElementException("Unable to find user with id: "+id);
    }

    public void delete(Integer id) {
        User user = get(id);
        user.setStatus(UserAccountStatus.DELETED);
        save(user);
    }

    public void update(User user) {
        Optional<User> userOptional = repo.findById(user.getUserId());
        if(userOptional.isPresent()){
            save(userOptional.get());
        }
        throw new NoSuchElementException("Unable to find user with id: "+user.getUserId());
    }


}
