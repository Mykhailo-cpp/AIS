package com.academic.AIS.controller;

import com.academic.AIS.model.Grade;
import com.academic.AIS.model.Student;
import com.academic.AIS.model.Subject;
import com.academic.AIS.service.StudentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    private boolean checkStudentAccess(HttpSession session, RedirectAttributes redirectAttributes) {
        String role = (String) session.getAttribute("role");
        if (!"STUDENT".equals(role)) {
            redirectAttributes.addFlashAttribute("error", "Access denied. Student privileges required.");
            return false;
        }
        return true;
    }


    private Student getCurrentStudent(HttpSession session) {
        String username = (String) session.getAttribute("username");
        return studentService.getStudentByUsername(username).orElse(null);
    }


    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(value = "subjectId", required = false) Integer subjectId,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!checkStudentAccess(session, redirectAttributes)) {
            return "redirect:/login";
        }

        Student student = getCurrentStudent(session);
        if (student == null) {
            redirectAttributes.addFlashAttribute("error", "Student profile not found");
            return "redirect:/login";
        }

        List<Grade> grades = studentService.getStudentGrades(student.getStudentId());


        List<Subject> subjects = grades.stream()
                .map(g -> g.getAssignment().getSubject())
                .distinct()
                .toList();

        if (subjectId != null) {
            grades = grades.stream()
                    .filter(g -> g.getAssignment().getSubject().getSubjectId().equals(subjectId))
                    .toList();
        }

        double averageGrade = grades.isEmpty() ? 0.0 :
                grades.stream().mapToInt(Grade::getGradeValue).average().orElse(0.0);

        long passingGrades = grades.stream().filter(Grade::isPassing).count();
        long failingGrades = grades.size() - passingGrades;

        StudentStats stats = new StudentStats(
                grades.size(),
                averageGrade,
                passingGrades,
                failingGrades
        );

        model.addAttribute("currentUser", session.getAttribute("displayName"));
        model.addAttribute("student", student);
        model.addAttribute("grades", grades);
        model.addAttribute("stats", stats);

        model.addAttribute("subjects", subjects);
        model.addAttribute("selectedSubjectId", subjectId);

        return "student/dashboard";
    }


    public static class StudentStats {
        private final int totalGrades;
        private final double averageGrade;
        private final long passingGrades;
        private final long failingGrades;

        public StudentStats(int totalGrades, double averageGrade, long passingGrades, long failingGrades) {
            this.totalGrades = totalGrades;
            this.averageGrade = averageGrade;
            this.passingGrades = passingGrades;
            this.failingGrades = failingGrades;
        }

        public int getTotalGrades() { return totalGrades; }
        public double getAverageGrade() { return averageGrade; }
        public long getPassingGrades() { return passingGrades; }
        public long getFailingGrades() { return failingGrades; }
    }
}