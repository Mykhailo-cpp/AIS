package com.academic.AIS.service;

import com.academic.AIS.model.Grade;
import com.academic.AIS.model.SubjectAssignment;
import com.academic.AIS.model.Teacher;
import com.academic.AIS.repository.GradeRepository;
import com.academic.AIS.repository.SubjectAssignmentRepository;
import com.academic.AIS.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final SubjectAssignmentRepository assignmentRepository;
    private final GradeRepository gradeRepository;

    @Autowired
    public TeacherService(TeacherRepository teacherRepository,
                          SubjectAssignmentRepository assignmentRepository,
                          GradeRepository gradeRepository) {
        this.teacherRepository = teacherRepository;
        this.assignmentRepository = assignmentRepository;
        this.gradeRepository = gradeRepository;
    }

    public Optional<Teacher> getTeacherByUsername(String username) {
        return teacherRepository.findByUsername(username);
    }

    public List<SubjectAssignment> getTeacherAssignments(Integer teacherId) {
        return assignmentRepository.findByTeacher_TeacherId(teacherId);
    }

    public Long countTeacherGrades(Integer teacherId) {
        return gradeRepository.countByTeacher(teacherId);
    }
}
