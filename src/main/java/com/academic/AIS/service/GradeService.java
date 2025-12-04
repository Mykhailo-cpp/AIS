
package com.academic.AIS.service;

import com.academic.AIS.model.Grade;
import com.academic.AIS.model.Student;
import com.academic.AIS.model.SubjectAssignment;
import com.academic.AIS.repository.GradeRepository;
import com.academic.AIS.repository.StudentRepository;
import com.academic.AIS.repository.SubjectAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GradeService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final SubjectAssignmentRepository assignmentRepository;

    @Autowired
    public GradeService(GradeRepository gradeRepository,
                        StudentRepository studentRepository,
                        SubjectAssignmentRepository assignmentRepository) {
        this.gradeRepository = gradeRepository;
        this.studentRepository = studentRepository;
        this.assignmentRepository = assignmentRepository;
    }

    public Grade enterGrade(Integer teacherId, Integer studentId, Integer assignmentId,
                            Integer gradeValue, String comments) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));


        SubjectAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        if (!assignment.getTeacher().getTeacherId().equals(teacherId)) {
            throw new IllegalArgumentException("You are not assigned to teach this subject");
        }

        if (student.getGroup() == null ||
                !student.getGroup().getGroupId().equals(assignment.getGroup().getGroupId())) {
            throw new IllegalArgumentException("Student is not in the group for this subject");
        }

        if (gradeRepository.existsByStudentAndAssignment(studentId, assignmentId)) {
            throw new IllegalArgumentException("Grade already exists. Use update instead.");
        }

        if (gradeValue < 0 || gradeValue > 10) {
            throw new IllegalArgumentException("Grade must be between 0 and 10");
        }

        Grade grade = new Grade(student, assignment, gradeValue, comments);
        return gradeRepository.save(grade);
    }

    public Grade updateGrade(Integer gradeId, Integer teacherId,
                             Integer newGradeValue, String newComments) {
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new IllegalArgumentException("Grade not found"));

        if (!grade.getAssignment().getTeacher().getTeacherId().equals(teacherId)) {
            throw new IllegalArgumentException("You can only edit grades you assigned");
        }

        if (newGradeValue < 0 || newGradeValue > 10) {
            throw new IllegalArgumentException("Grade must be between 0 and 10");
        }

        grade.setGradeValue(newGradeValue);
        grade.setComments(newComments);
        grade.setGradeDate(LocalDate.now());

        return gradeRepository.save(grade);
    }

    public Grade deleteGrade(Integer gradeId, Integer teacherId) {
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new IllegalArgumentException("Grade not found"));

        if (!grade.getAssignment().getTeacher().getTeacherId().equals(teacherId)) {
            throw new IllegalArgumentException("You can only delete grades you assigned");
        }

        Grade deletedGrade = grade;

        gradeRepository.deleteById(gradeId);

        return deletedGrade;
    }

    public List<Grade> getTeacherGrades(Integer teacherId) {
        return gradeRepository.findByTeacher_TeacherId(teacherId);
    }

    public List<Grade> getGradesForTeacherSubject(Integer teacherId, Integer subjectId) {
        return gradeRepository.findByTeacherAndSubject(teacherId, subjectId);
    }

    public List<Grade> getGradesByAssignment(Integer assignmentId) {
        return gradeRepository.findByAssignment_AssignmentIdOrderByGradeDateDesc(assignmentId);
    }
}