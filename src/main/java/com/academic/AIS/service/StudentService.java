package com.academic.AIS.service;

import com.academic.AIS.model.Grade;
import com.academic.AIS.model.Student;
import com.academic.AIS.model.StudyGroup;
import com.academic.AIS.repository.GradeRepository;
import com.academic.AIS.repository.StudentRepository;
import com.academic.AIS.repository.StudyGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;
    private final StudyGroupRepository studentGroupRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository,
                          GradeRepository gradeRepository,
                          StudyGroupRepository studentGroupRepository) {
        this.studentRepository = studentRepository;
        this.gradeRepository = gradeRepository;
        this.studentGroupRepository = studentGroupRepository;
    }

    public Optional<Student> getStudentByUsername(String username) {
        return studentRepository.findByUsername(username);
    }

    public List<Grade> getStudentGrades(Integer studentId) {
        return gradeRepository.findByStudentWithDetails(studentId);
    }
}