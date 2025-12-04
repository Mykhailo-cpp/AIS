package com.academic.AIS.service;

import com.academic.AIS.model.*;
import com.academic.AIS.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional
public class AuthenticationService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    @Autowired
    public AuthenticationService(UserRepository userRepository,
                                 StudentRepository studentRepository,
                                 TeacherRepository teacherRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    public User authenticate(String username, String password) {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            return null;
        }

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();
        if (!password.equals(user.getPassword())) {
            return null;
        }

        return user;
    }

    public Student registerStudent(String firstName, String lastName, String email) {
        validateInput(firstName, lastName, email);

        String username = firstName;
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User(username, lastName, "STUDENT");
        user = userRepository.save(user);

        Student student = new Student(user, firstName, lastName, email);
        return studentRepository.save(student);
    }

    public Teacher registerTeacher(String firstName, String lastName, String email) {
        validateInput(firstName, lastName, email);

        String username = firstName;
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User(username, lastName, "TEACHER");
        user = userRepository.save(user);

        Teacher teacher = new Teacher(user, firstName, lastName, email);
        return teacherRepository.save(teacher);
    }

    public String getDashboardPath(User user) {
        if (user == null) return "/login";

        switch (user.getRole()) {
            case "ADMINISTRATOR": return "/admin/dashboard";
            case "TEACHER": return "/teacher/dashboard";
            case "STUDENT": return "/student/dashboard";
            default: return "/login";
        }
    }

    private void validateInput(String firstName, String lastName, String email) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
    }
}