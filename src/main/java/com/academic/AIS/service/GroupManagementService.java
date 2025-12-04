package com.academic.AIS.service;

import com.academic.AIS.model.StudyGroup;
import com.academic.AIS.repository.StudyGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class GroupManagementService {

    private final StudyGroupRepository studyGroupRepository;

    @Autowired
    public GroupManagementService(StudyGroupRepository studyGroupRepository) {
        this.studyGroupRepository = studyGroupRepository;
    }

    public List<StudyGroup> getAllGroups() {
        return studyGroupRepository.findAll();
    }

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
}