package com.academic.AIS.controller;

import com.academic.AIS.model.*;
import com.academic.AIS.service.GradeService;
import com.academic.AIS.service.TeacherService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    private final TeacherService teacherService;
    private final GradeService gradeService;

    @Autowired
    public TeacherController(TeacherService teacherService, GradeService gradeService) {
        this.teacherService = teacherService;
        this.gradeService = gradeService;
    }

    private boolean checkTeacherAccess(HttpSession session, RedirectAttributes redirectAttributes) {
        String role = (String) session.getAttribute("role");
        if (!"TEACHER".equals(role)) {
            redirectAttributes.addFlashAttribute("error", "Access denied. Teacher privileges required.");
            return false;
        }
        return true;
    }

    private Integer getCurrentTeacherId(HttpSession session) {
        String username = (String) session.getAttribute("username");
        return teacherService.getTeacherByUsername(username)
                .map(Teacher::getTeacherId)
                .orElse(null);
    }

    // DASHBOARD

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!checkTeacherAccess(session, redirectAttributes)) return "redirect:/login";

        Integer teacherId = getCurrentTeacherId(session);
        if (teacherId == null) {
            redirectAttributes.addFlashAttribute("error", "Teacher profile not found.");
            return "redirect:/login";
        }

        List<SubjectAssignment> assignments = teacherService.getTeacherAssignments(teacherId);

        long totalGrades = teacherService.countTeacherGrades(teacherId);

        int totalStudents = assignments.stream()
                .filter(a -> a != null && a.getGroup() != null && a.getGroup().getStudents() != null)
                .flatMap(a -> a.getGroup().getStudents().stream())
                .collect(Collectors.toSet()) // ensure unique students
                .size();

        List<Grade> teacherGrades = gradeService.getTeacherGrades(teacherId);
        double averageGrade = teacherGrades.isEmpty() ? 0.0 :
                teacherGrades.stream().mapToDouble(g -> g.getGradeValue()).average().orElse(0.0);

        TeacherStats stats = new TeacherStats(assignments.size(), totalStudents, totalGrades, averageGrade);

        model.addAttribute("currentUser", session.getAttribute("displayName"));
        model.addAttribute("subjects", assignments);
        model.addAttribute("stats", stats);

        return "teacher/dashboard";
    }

    //  GRADES PAGE

    @GetMapping("/grades")
    public String gradesPage(@RequestParam(required = false) Integer subjectId,
                             @RequestParam(required = false) Integer assignmentId,
                             HttpSession session,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        if (!checkTeacherAccess(session, redirectAttributes)) return "redirect:/login";
        Integer teacherId = getCurrentTeacherId(session);
        if (teacherId == null) return "redirect:/login";

        List<SubjectAssignment> allAssignments = teacherService.getTeacherAssignments(teacherId);
        List<SubjectAssignment> assignments = allAssignments.stream()
                .filter(a -> a != null && a.getSubject() != null)
                .collect(Collectors.toList());

        List<Grade> grades;

        if (assignmentId != null) {

            grades = gradeService.getGradesByAssignment(assignmentId);
        } else if (subjectId != null) {
            grades = gradeService.getGradesForTeacherSubject(teacherId, subjectId);
        } else {
            grades = gradeService.getTeacherGrades(teacherId);
        }

        model.addAttribute("assignments", assignments);
        model.addAttribute("grades", grades);
        model.addAttribute("selectedSubjectId", subjectId);
        model.addAttribute("selectedAssignmentId", assignmentId);

        // Build safe JSON for UI

        List<Map<String, Object>> safeAssignments = new ArrayList<>();

        for (SubjectAssignment a : assignments) {
            if (a == null || a.getSubject() == null || a.getGroup() == null) continue;

            Map<String, Object> safe = new HashMap<>();
            safe.put("assignmentId", a.getAssignmentId());

            Map<String, Object> sub = new HashMap<>();
            sub.put("subjectId", a.getSubject().getSubjectId());
            sub.put("subjectName", a.getSubject().getSubjectName());
            safe.put("subject", sub);

            Map<String, Object> grp = new HashMap<>();
            grp.put("groupName", a.getGroup().getGroupName());

            List<Map<String, Object>> studentList = new ArrayList<>();
            if (a.getGroup().getStudents() != null) {
                for (Student s : a.getGroup().getStudents()) {
                    Map<String, Object> st = new HashMap<>();
                    st.put("studentId", s.getStudentId());
                    st.put("firstName", s.getFirstName());
                    st.put("lastName", s.getLastName());
                    studentList.add(st);
                }
            }

            grp.put("students", studentList);
            safe.put("group", grp);

            safeAssignments.add(safe);
        }

        model.addAttribute("safeAssignments", safeAssignments);

        return "teacher/grades";
    }

    // CREATE GRADE

    @PostMapping("/grades/create")
    public String createGrade(@RequestParam Integer assignmentId,
                              @RequestParam Integer studentId,
                              @RequestParam Integer gradeValue,
                              @RequestParam(required = false) String comments,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        try {
            Integer teacherId = getCurrentTeacherId(session);
            gradeService.enterGrade(teacherId, studentId, assignmentId, gradeValue, comments);
            redirectAttributes.addFlashAttribute("success", "Grade added successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/teacher/grades?assignmentId=" + assignmentId;
    }

    // UPDATE GRADE

    @PostMapping("/grades/update/{id}")
    public String updateGrade(@PathVariable Integer id,
                              @RequestParam Integer gradeValue,
                              @RequestParam(required = false) String comments,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        Integer teacherId = getCurrentTeacherId(session);

        try {
            Grade g = gradeService.updateGrade(id, teacherId, gradeValue, comments);
            redirectAttributes.addFlashAttribute("success", "Grade updated successfully.");
            return "redirect:/teacher/grades?assignmentId=" + g.getAssignment().getAssignmentId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/teacher/grades";
        }
    }

    //  DELETE GRADE

    @PostMapping("/grades/delete/{id}")
    public String deleteGrade(@PathVariable Integer id,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        Integer teacherId = getCurrentTeacherId(session);

        try {
            Grade deleted = gradeService.deleteGrade(id, teacherId);
            redirectAttributes.addFlashAttribute("success", "Grade deleted successfully.");
            return "redirect:/teacher/grades?assignmentId=" +
                    deleted.getAssignment().getAssignmentId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/teacher/grades";
        }
    }

    //  STATS CLASS

    public static class TeacherStats {
        private final int totalSubjects;
        private final int totalStudents;
        private final long totalGrades;
        private final double averageGrade;

        public TeacherStats(int totalSubjects, int totalStudents, long totalGrades, double averageGrade) {
            this.totalSubjects = totalSubjects;
            this.totalStudents = totalStudents;
            this.totalGrades = totalGrades;
            this.averageGrade = averageGrade;
        }

        public int getTotalSubjects() { return totalSubjects; }
        public int getTotalStudents() { return totalStudents; }
        public long getTotalGrades() { return totalGrades; }
        public double getAverageGrade() { return averageGrade; }
    }
}
