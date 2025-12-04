package com.academic.AIS.repository;

import com.academic.AIS.model.SubjectAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectAssignmentRepository extends JpaRepository<SubjectAssignment, Integer> {

    @Query("SELECT DISTINCT sa FROM SubjectAssignment sa " +
            "LEFT JOIN FETCH sa.subject " +
            "LEFT JOIN FETCH sa.group g " +
            "LEFT JOIN FETCH g.students " +
            "WHERE sa.teacher.teacherId = :teacherId")
    List<SubjectAssignment> findByTeacher_TeacherId(@Param("teacherId") Integer teacherId);

    @Query("SELECT sa FROM SubjectAssignment sa " +
            "WHERE sa.subject.subjectId = :subjectId " +
            "AND sa.teacher.teacherId = :teacherId " +
            "AND sa.group.groupId = :groupId " +
            "AND sa.academicYear = :academicYear " +
            "AND sa.semester = :semester")
    Optional<SubjectAssignment> findByAllFields(@Param("subjectId") Integer subjectId,
                                                @Param("teacherId") Integer teacherId,
                                                @Param("groupId") Integer groupId,
                                                @Param("academicYear") String academicYear,
                                                @Param("semester") String semester);


}