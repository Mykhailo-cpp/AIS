package com.academic.AIS.controller;

import com.academic.AIS.model.*;
import com.academic.AIS.repository.*;
import com.academic.AIS.service.AuthenticationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

@Controller
public class LoginController {

    private final AuthenticationService authenticationService;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final AdministratorRepository administratorRepository;

    @Autowired
    public LoginController(AuthenticationService authenticationService,
                           StudentRepository studentRepository,
                           TeacherRepository teacherRepository,
                           AdministratorRepository administratorRepository) {
        this.authenticationService = authenticationService;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.administratorRepository = administratorRepository;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(required = false) String error,
                                @RequestParam(required = false) String logout,
                                Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully");
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        try {
            User user = authenticationService.authenticate(username, password);

            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "Invalid username or password");
                return "redirect:/login?error";
            }

            String displayName = getDisplayName(user);

            session.setAttribute("currentUser", user);
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole());
            session.setAttribute("displayName", displayName);

            String dashboardPath = authenticationService.getDashboardPath(user);
            return "redirect:" + dashboardPath;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Login failed: " + e.getMessage());
            return "redirect:/login?error";
        }
    }

    private String getDisplayName(User user) {
        switch (user.getRole()) {
            case "STUDENT":
                Optional<Student> student = studentRepository.findByUsername(user.getUsername());
                return student.map(Student::getFullName).orElse(user.getUsername());

            case "TEACHER":
                Optional<Teacher> teacher = teacherRepository.findByUsername(user.getUsername());
                return teacher.map(Teacher::getFullName).orElse(user.getUsername());

            case "ADMINISTRATOR":
                Optional<Administrator> admin = administratorRepository.findByUsername(user.getUsername());
                return admin.map(Administrator::getFullName).orElse(user.getUsername());

            default:
                return user.getUsername();
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }
        return "redirect:" + authenticationService.getDashboardPath(user);
    }
}