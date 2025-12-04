package com.academic.AIS.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "study_group")
public class StudyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Integer groupId;

    @Column(name = "group_name", unique = true, nullable = false, length = 50)
    private String groupName;

    @Column(name = "year", nullable = false)
    private Integer year;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Student> students = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SubjectAssignment> assignments = new ArrayList<>();

    public StudyGroup() {}

    public StudyGroup(String groupName, Integer year) {
        this.groupName = groupName;
        this.year = year;
    }

    public Integer getGroupId() { return groupId; }
    public void setGroupId(Integer groupId) { this.groupId = groupId; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }
    public List<SubjectAssignment> getAssignments() { return assignments; }
    public void setAssignments(List<SubjectAssignment> assignments) { this.assignments = assignments; }

    @Override
    public String toString() {
        return "StudyGroup{groupId=" + groupId + ", groupName='" + groupName + "', year=" + year + "}";
    }
}