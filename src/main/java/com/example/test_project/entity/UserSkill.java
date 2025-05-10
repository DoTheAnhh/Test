package com.example.test_project.entity;

import com.example.test_project.entity.sub.UserSkillId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "userSkill")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSkill {

    @EmbeddedId
    private UserSkillId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("skillId")
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @Column(name = "status")
    private String status;
}
