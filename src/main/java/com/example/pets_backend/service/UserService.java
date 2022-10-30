package com.example.pets_backend.service;

import com.example.pets_backend.entity.Event;
import com.example.pets_backend.entity.Task;
import com.example.pets_backend.entity.User;
import com.example.pets_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.pets_backend.ConstantValues.WEB_PREFIX;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final SendMailService sendMailService;
    private final SchedulerService schedulerService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email);
        checkUserForLogin(user);
        return new org.springframework.security.core.userdetails.User(email, user.getPassword(), new ArrayList<>());
    }

    private void checkUserForLogin(User user) {
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }
        if (!user.isEmail_verified()) {
            throw new EntityNotFoundException("Email not verified");
        }
    }

    public User save(User user) {
        String email = user.getEmail();
        if (userRepo.findByEmail(email) != null) {
            log.error("Duplicate email '" + email + "'");
            throw new DuplicateKeyException(("Duplicate email '" + email + "'"));
        } else {
            log.info("New user '{}' saved into database", user.getUid());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    public void sendVerifyEmail(User user) {
        String email = user.getEmail();
        String token = user.getVerify_token();
        String verifyUrl = WEB_PREFIX + "#/user/verify?" +
                "email=" + email + "&" +
                "token=" + token;
        String text = "Hi " + user.getFirstName() + ", \n\n" +
                "Click the following link to verify your email: \n" +
                verifyUrl + "\n\n";
        schedulerService.addJobToScheduler(token, () -> {
            try {
                sendMailService.sendVerifyEmail(email, text);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }, LocalDateTime.now());
    }

    public User findByUid(String uid) {
        User user = userRepo.findByUid(uid);
        checkUserInDB(user, uid);
        return user;
    }

    public List<User> findAll() {
        log.info("Finding all users in database");
        return userRepo.findAll();
    }

    public void deleteByUid(String uid) {
        User user = userRepo.findByUid(uid);
        checkUserInDB(user, uid);
        userRepo.deleteById(uid);
        log.info("User '{}' deleted from database", uid);
    }

    public void deleteByEmail(String email) {
        User user = userRepo.findByEmail(email);
        checkUserInDB(user, email);
        userRepo.deleteByEmail(email);
        log.info("User with email '{}' deleted from database", email);
    }

    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public Event getEventByUidAndEventId(String uid, String eventId) {
        User user = userRepo.findByUid(uid);
        checkUserInDB(user, uid);
        return user.getEventByEventId(eventId);
    }

    public Task getTaskByUidAndTaskId(String uid, String taskId) {
        User user = userRepo.findByUid(uid);
        checkUserInDB(user, uid);
        return user.getTaskByTaskId(taskId);
    }

    private void checkUserInDB(User user, String identifier) {
        if (user == null) {
            log.error("User '{}' not found in database", identifier);
            throw new EntityNotFoundException("User '" + identifier + "' not found in database");
        } else {
            log.info("User '{}' found in database", identifier);
        }
    }
}
