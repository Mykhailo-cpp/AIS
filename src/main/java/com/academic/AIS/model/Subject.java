package com.academic.AIS.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subject")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Integer subjectId;

    @Column(name = "subject_name", nullable = false, length = 150)
    private String subjectName;

    @Column(name = "subject_code", unique = true, nullable = false, length = 50)
    private String subjectCode;

    @Column(name = "credits", nullable = false)
    private Integer credits;

    @Column(name = "description", length = 1000)
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SubjectAssignment> assignments = new ArrayList<>();

    // Constructors
    public Subject() {}

    public Subject(String subjectName, String subjectCode, Integer credits, String description) {
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
        this.credits = credits;
        this.description = description;
    }

    // Getters and Setters
    public Integer getSubjectId() { return subjectId; }
    public void setSubjectId(Integer subjectId) { this.subjectId = subjectId; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }
    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<SubjectAssignment> getAssignments() { return assignments; }
    public void setAssignments(List<SubjectAssignment> assignments) { this.assignments = assignments; }

    @Override
    public String toString() {
        return "Subject{subjectId=" + subjectId + ", name='" + subjectName + "', code='" + subjectCode + "'}";
    }
}