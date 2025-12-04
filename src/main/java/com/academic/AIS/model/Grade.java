package com.academic.AIS.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "grade")
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grade_id")
    private Integer gradeId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private SubjectAssignment assignment;

    @Column(name = "grade_value", nullable = false)
    private Integer gradeValue;

    @Column(name = "grade_date", nullable = false)
    private LocalDate gradeDate;

    @Column(name = "comments", length = 500)
    private String comments;

    public Grade() {
        this.gradeDate = LocalDate.now();
    }

    public Grade(Student student, SubjectAssignment assignment, Integer gradeValue, String comments) {
        this.student = student;
        this.assignment = assignment;
        this.gradeValue = gradeValue;
        this.comments = comments;
        this.gradeDate = LocalDate.now();
    }

    public boolean isPassing() {
        return gradeValue != null && gradeValue >= 5;
    }

    public String getGradeLevel() {
        if (gradeValue == null) return "N/A";
        if (gradeValue >= 9) return "Excellent";
        if (gradeValue >= 7) return "Good";
        if (gradeValue >= 5) return "Satisfactory";
        return "Unsatisfactory";
    }

    public Integer getGradeId() { return gradeId; }
    public void setGradeId(Integer gradeId) { this.gradeId = gradeId; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public SubjectAssignment getAssignment() { return assignment; }
    public void setAssignment(SubjectAssignment assignment) { this.assignment = assignment; }
    public Integer getGradeValue() { return gradeValue; }
    public void setGradeValue(Integer gradeValue) { this.gradeValue = gradeValue; }
    public LocalDate getGradeDate() { return gradeDate; }
    public void setGradeDate(LocalDate gradeDate) { this.gradeDate = gradeDate; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    @Override
    public String toString() {
        return "Grade{gradeId=" + gradeId + ", gradeValue=" + gradeValue +
                ", gradeDate=" + gradeDate + ", level='" + getGradeLevel() + "'}";
    }
}