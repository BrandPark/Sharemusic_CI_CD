package com.brandpark.sharemusic.domain.user;

import com.brandpark.sharemusic.domain.BaseTimeEntity;
import com.brandpark.sharemusic.domain.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @OneToMany(mappedBy = "user")
    private List<Follow> followings = new ArrayList<>();

    @OneToMany(mappedBy = "targetUser")
    private List<Follow> followers = new ArrayList<>();

    @Column(name = "user_name")
    private String name;

    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String nickname;
    private String imgUrl;
    private String intro;
}
