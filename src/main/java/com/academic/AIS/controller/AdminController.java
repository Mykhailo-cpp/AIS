package com.academic.AIS.controller;


import com.academic.AIS.model.*;
import com.academic.AIS.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;


    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    private boolean checkAdminAccess(HttpSession session, RedirectAttributes redirectAttributes) {
        String role = (String) session.getAttribute("role");
        if (!"ADMINISTRATOR".equals(role)) {
            redirectAttributes.addFlashAttribute("error", "Access denied. Administrator privileges required.");
            return false;
        }
        return true;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!checkAdminAccess(session, redirectAttributes)) {
            return "redirect:/login";
        }

        AdminService.SystemStatistics stats = adminService.getSystemStatistics();

        model.addAttribute("stats", stats);
        model.addAttribute("currentUser", session.getAttribute("displayName"));

        return "admin/dashboard";
    }

    // STUDENT MANAGEMENT

    @GetMapping("/students")
    public String listStudents(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!checkAdminAccess(session, redirectAttributes)) {
            return "redirect:/login";
        }

        List<Student> students = adminService.getAllStudents();
        List<StudyGroup> groups = adminService.getAllGroups();

        model.addAttribute("students", students);
        model.addAttribute("groups", groups);

        return "admin/students";
    }

    @PostMapping("/students/create")
    public String createStudent(@RequestParam String firstName,
                                @RequestParam String lastName,
                                @RequestParam String email,
                                @RequestParam(required = false) Integer groupId,
                                RedirectAttributes redirectAttributes) {
        try {
            Student student = adminService.createStudent(firstName, lastName, email);

            if (groupId != null) {
                adminService.assignStudentToGroup(student.getStudentId(), groupId);
            }

            redirectAttributes.addFlashAttribute("success",
                    "Student created successfully. Login: " + firstName + ", Password: " + lastName);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/admin/students";
    }


    @PostMapping("/students/update/{id}")
    public String updateStudent(@PathVariable Integer id,
                                @RequestParam String firstName,
                                @RequestParam String lastName,
                                @RequestParam String email,
                                @RequestParam(required = false) Integer groupId,
                                RedirectAttributes redirectAttributes) {
        try {
            adminService.updateStudent(id, firstName, lastName, email);

            if (groupId != null) {
                adminService.assignStudentToGroup(id, groupId);
            } else {
                adminService.removeStudentFromGroup(id);
            }

            redirectAttributes.addFlashAttribute("success", "Student updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/admin/students";
    }


    @PostMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("success", "Student deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/admin/students";
    }

    // TEACHER MANAGEMENT

    @GetMapping("/teachers")
    public String listTeachers(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!checkAdminAccess(session, redirectAttributes)) {
            return "redirect:/login";
        }

        List<Teacher> teachers = adminService.getAllTeachers();
        model.addAttribute("teachers", teachers);

        return "admin/teachers";
    }

    @PostMapping("/teachers/create")
    public String createTeacher(@RequestParam String firstName,
                                @RequestParam String lastName,
                                @RequestParam String email,
                                RedirectAttributes redirectAttributes) {
        try {
            adminService.createTeacher(firstName, lastName, email);
            redirectAttributes.addFlashAttribute("success",
                    "Teacher created successfully. Login: " + firstName + ", Password: " + lastName);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/admin/teachers";
    }


    @PostMapping("/teachers/update/{id}")
    public String updateTeacher(@PathVariable Integer id,
                                @RequestParam String firstName,
                                @RequestParam String lastName,
                                @RequestParam String email,
                                RedirectAttributes redirectAttributes) {
        try {
            adminService.updateTeacher(id, firstName, lastName, email);
            redirectAttributes.addFlashAttribute("success", "Teacher updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/admin/teachers";
    }

    @PostMapping("/teachers/delete/{id}")
    public String deleteTeacher(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteTeacher(id);
            redirectAttributes.addFlashAttribute("success", "Teacher deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/admin/teachers";
    }

    //  GROUP MANAGEMENT

    @GetMapping("/groups")
    public String listGroups(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!checkAdminAccess(session, redirectAttributes)) {
            return "redirect:/login";
        }

        List<StudyGroup> groups = adminService.getAllGroups();
        model.addAttribute("groups", groups);

        return "admin/groups";
    }

    @PostMapping("/groups/create")
    public String createGroup(@RequestParam String groupName,
                              @RequestParam Integer year,
                              RedirectAttributes redirectAttributes) {
        try {
            adminService.createGroup(groupName, year);
            redirectAttributes.addFlashAttribute("success", "Group created successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/admin/groups";
    }

    @PostMapping("/groups/update/{id}")
    public String updateGroup(@PathVariable Integer id,
                              @RequestParam String groupName,
                              @RequestParam Integer year,
                              RedirectAttributes redirectAttributes) {
        try {
            adminService.updateGroup(id, groupName, year);
            redirectAttributes.addFlashAttribute("success", "Group updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/admin/groups";
    }

    @PostMapping("/groups/delete/{id}")
    public String deleteGroup(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteGroup(id);
            redirectAttributes.addFlashAttribute("success", "Group deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/admin/groups";
    }

    // SUBJECT MANAGEMENT

    @GetMapping("/subjects")
    public String listSubjects(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!checkAdminAccess(session, redirectAttributes)) {
            return "redirect:/login";
        }

        try {
            List<Subject> subjects = adminService.getAllSubjects();
            List<Teacher> teachers = adminService.getAllTeachers();
            List<StudyGroup> groups = adminService.getAllGroups();

            System.out.println("Subjects found: " + subjects.size());
            System.out.println("Teachers found: " + teachers.size());
            System.out.println("Groups found: " + groups.size());

            model.addAttribute("subjects", subjects);
            model.addAttribute("teachers", teachers);
            model.addAttribute("groups", groups);

            return "admin/subjects";
        } catch (Exception e) {
            System.err.println("Error loading subjects page: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error loading subjects: " + e.getMessage());
            return "redirect:/admin/dashboard";
        }
    }

    @PostMapping("/subjects/create")
    public String createSubject(@RequestParam String subjectName,
                                @RequestParam String subjectCode,
                                @RequestParam Integer credits,
                                @RequestParam(required = false) String description,
                                @RequestParam(required = false) String academicYear,
                                @RequestParam(required = false) String semester,
                                @RequestParam(required = false) Integer teacherId,
                                @RequestParam(required = false) Integer groupId,
                                RedirectAttributes redirectAttributes) {
        try {
            System.out.println("Creating subject: " + subjectName + " (" + subjectCode + ")");

            String year = (academicYear != null && !academicYear.trim().isEmpty())
                    ? academicYear
                    : "2024/2025";

            String sem = (semester != null && !semester.trim().isEmpty())
                    ? semester
                    : "Fall";

            Subject subject = adminService.createSubject(
                    subjectName,
                    subjectCode,
                    credits,
                    description,
                    year
            );

            if (teacherId != null && teacherId > 0 && groupId != null && groupId > 0) {
                System.out.println("Creating assignment: Teacher=" + teacherId + ", Group=" + groupId);
                adminService.createAssignment(
                        subject.getSubjectId(),
                        teacherId,
                        groupId,
                        year,
                        sem
                );
            }

            redirectAttributes.addFlashAttribute("success", "Subject created successfully");
        } catch (Exception e) {
            System.err.println("Error creating subject: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/admin/subjects";
    }

    @PostMapping("/subjects/update/{id}")
    public String updateSubject(@PathVariable Integer id,
                                @RequestParam String subjectName,
                                @RequestParam String subjectCode,
                                @RequestParam Integer credits,
                                @RequestParam(required = false) String description,
                                @RequestParam(required = false) String academicYear,
                                @RequestParam(required = false) String semester,
                                @RequestParam(required = false) Integer teacherId,
                                @RequestParam(required = false) Integer groupId,
                                RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== Updating Subject ID: " + id + " ===");
            System.out.println("Subject Name: " + subjectName);
            System.out.println("Subject Code: " + subjectCode);
            System.out.println("Credits: " + credits);
            System.out.println("Description: " + description);
            System.out.println("Academic Year: " + academicYear);
            System.out.println("Semester: " + semester);
            System.out.println("Teacher ID: " + teacherId);
            System.out.println("Group ID: " + groupId);


            adminService.updateSubject(id, subjectName, subjectCode, credits, description);


            List<SubjectAssignment> existingAssignments = adminService.getAllAssignments()
                    .stream()
                    .filter(a -> a.getSubject().getSubjectId().equals(id))
                    .collect(Collectors.toList());

            for (SubjectAssignment assignment : existingAssignments) {
                adminService.deleteAssignment(assignment.getAssignmentId());
            }

            if (teacherId != null && teacherId > 0 && groupId != null && groupId > 0) {
                String year = (academicYear != null && !academicYear.trim().isEmpty())
                        ? academicYear
                        : "2024/2025";
                String sem = (semester != null && !semester.trim().isEmpty())
                        ? semester
                        : "Fall";

                adminService.createAssignment(id, teacherId, groupId, year, sem);
                System.out.println("Created new assignment");
            } else {
                System.out.println("No assignment created (teacher or group missing)");
            }

            redirectAttributes.addFlashAttribute("success", "Subject updated successfully");
        } catch (Exception e) {
            System.err.println("Error updating subject: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/admin/subjects";
    }

    @PostMapping("/subjects/delete/{id}")
    public String deleteSubject(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("Deleting subject ID: " + id);
            adminService.deleteSubject(id);
            redirectAttributes.addFlashAttribute("success", "Subject deleted successfully");
        } catch (Exception e) {
            System.err.println("Error deleting subject: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/admin/subjects";
    }
}