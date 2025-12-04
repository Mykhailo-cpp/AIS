package com.academic.AIS.service;

import com.academic.AIS.model.*;
import com.academic.AIS.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class AdminService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final SubjectRepository subjectRepository;
    private final SubjectAssignmentRepository assignmentRepository;
    private final GradeRepository gradeRepository;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    @Autowired
    public AdminService(StudentRepository studentRepository,
                        TeacherRepository teacherRepository,
                        StudyGroupRepository studyGroupRepository,
                        SubjectRepository subjectRepository,
                        SubjectAssignmentRepository assignmentRepository,
                        GradeRepository gradeRepository,
                        AuthenticationService authenticationService,
                        UserRepository userRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.studyGroupRepository = studyGroupRepository;
        this.subjectRepository = subjectRepository;
        this.assignmentRepository = assignmentRepository;
        this.gradeRepository = gradeRepository;
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
    }

    // STUDENT MANAGEMENT

    public Student createStudent(String firstName, String lastName, String email) {
        return authenticationService.registerStudent(firstName, lastName, email);
    }
    @Transactional
    public void deleteStudent(Integer studentId) {

        if (!studentRepository.existsById(studentId)) {
            throw new IllegalArgumentException("Student not found");
        }


        Integer userId = studentId;


        studentRepository.deleteById(studentId);


        userRepository.deleteById(userId);
    }

    public Student updateStudent(Integer studentId, String firstName, String lastName, String email) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEmail(email);
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    //  TEACHER MANAGEMENT

    public Teacher createTeacher(String firstName, String lastName, String email) {
        return authenticationService.registerTeacher(firstName, lastName, email);
    }

    public void deleteTeacher(Integer teacherId) {
        if (!teacherRepository.existsById(teacherId)) {
            throw new IllegalArgumentException("Teacher not found");
        }

        Integer userId = teacherId;
        teacherRepository.deleteById(teacherId);
        userRepository.deleteById(userId);
    }


    public Teacher updateTeacher(Integer teacherId, String firstName, String lastName, String email) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
        teacher.setFirstName(firstName);
        teacher.setLastName(lastName);
        teacher.setEmail(email);
        return teacherRepository.save(teacher);
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    // GROUP MANAGEMENT

    public StudyGroup createGroup(String groupName, Integer year) {
        if (groupName == null || groupName.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name is required");
        }
        if (year == null || year < 2000 || year > 2100) {
            throw new IllegalArgumentException("Invalid year");
        }
        if (studyGroupRepository.existsByGroupName(groupName)) {
            throw new IllegalArgumentException("Group name already exists");
        }

        StudyGroup group = new StudyGroup(groupName, year);
        return studyGroupRepository.save(group);
    }

    public void deleteGroup(Integer groupId) {
        if (!studyGroupRepository.existsById(groupId)) {
            throw new IllegalArgumentException("Group not found");
        }
        studyGroupRepository.deleteById(groupId);
    }

    public StudyGroup updateGroup(Integer groupId, String groupName, Integer year) {
        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));
        group.setGroupName(groupName);
        group.setYear(year);
        return studyGroupRepository.save(group);
    }

    public List<StudyGroup> getAllGroups() {
        return studyGroupRepository.findAll();
    }

    // SUBJECT MANAGEMENT

    public Subject createSubject(String subjectName, String subjectCode, Integer credits,
                                 String description, String academicYear) {
        if (subjectName == null || subjectName.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject name is required");
        }
        if (subjectCode == null || subjectCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject code is required");
        }
        if (credits == null || credits < 0) {
            throw new IllegalArgumentException("Invalid credits");
        }
        if (subjectRepository.findBySubjectCode(subjectCode).isPresent()) {
            throw new IllegalArgumentException("Subject code already exists");
        }

        Subject subject = new Subject(subjectName, subjectCode, credits, description);
        return subjectRepository.save(subject);
    }

    public void deleteSubject(Integer subjectId) {
        if (!subjectRepository.existsById(subjectId)) {
            throw new IllegalArgumentException("Subject not found");
        }
        subjectRepository.deleteById(subjectId);
    }

    public Subject updateSubject(Integer subjectId, String subjectName, String subjectCode,
                                 Integer credits, String description) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));
        subject.setSubjectName(subjectName);
        subject.setSubjectCode(subjectCode);
        subject.setCredits(credits);
        subject.setDescription(description);
        return subjectRepository.save(subject);
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    // ASSIGNMENT MANAGEMENT

    public SubjectAssignment createAssignment(Integer subjectId, Integer teacherId,
                                              Integer groupId, String academicYear, String semester) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        if (assignmentRepository.findByAllFields(subjectId, teacherId, groupId, academicYear, semester).isPresent()) {
            throw new IllegalArgumentException("Assignment already exists");
        }

        SubjectAssignment assignment = new SubjectAssignment(subject, teacher, group, academicYear, semester);
        return assignmentRepository.save(assignment);
    }

    public void deleteAssignment(Integer assignmentId) {
        if (!assignmentRepository.existsById(assignmentId)) {
            throw new IllegalArgumentException("Assignment not found");
        }
        assignmentRepository.deleteById(assignmentId);
    }

    public List<SubjectAssignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    // STUDENT/GROUP ASIGNMENT

    public Student assignStudentToGroup(Integer studentId, Integer groupId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));
        student.setGroup(group);
        return studentRepository.save(student);
    }

    public Student removeStudentFromGroup(Integer studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        student.setGroup(null);
        return studentRepository.save(student);
    }

    // STATISTICS

    public SystemStatistics getSystemStatistics() {
        long totalStudents = studentRepository.count();
        long totalTeachers = teacherRepository.count();
        long totalGroups = studyGroupRepository.count();
        long totalSubjects = subjectRepository.count();
        long totalGrades = gradeRepository.count();

        return new SystemStatistics(totalStudents, totalTeachers, totalGroups,
                totalSubjects, totalGrades);
    }

    public static class SystemStatistics {
        private final long totalStudents;
        private final long totalTeachers;
        private final long totalGroups;
        private final long totalSubjects;
        private final long totalGrades;

        public SystemStatistics(long totalStudents, long totalTeachers, long totalGroups,
                                long totalSubjects, long totalGrades) {
            this.totalStudents = totalStudents;
            this.totalTeachers = totalTeachers;
            this.totalGroups = totalGroups;
            this.totalSubjects = totalSubjects;
            this.totalGrades = totalGrades;
        }

        public long getTotalStudents() {
            return totalStudents;
        }

        public long getTotalTeachers() {
            return totalTeachers;
        }

        public long getTotalGroups() {
            return totalGroups;
        }

        public long getTotalSubjects() {
            return totalSubjects;
        }

        public long getTotalGrades() {
            return totalGrades;
        }
    }
}