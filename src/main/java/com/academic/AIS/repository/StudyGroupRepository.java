package com.academic.AIS.repository;

import com.academic.AIS.model.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, Integer> {

    boolean existsByGroupName(String groupName);

}