package com.brandpark.sharemusic.domain.user;

import com.brandpark.sharemusic.domain.BaseTimeEntity;
import com.brandpark.sharemusic.domain.Role;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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

    @Enumerated(EnumType.STRING)
    private Role role;

    private String email;
    private String nickname;
    private String password;
    private String imgUrl;
    private String intro;

    @Builder
    private User(String name, Role role, String email, String nickname, String imgUrl, String intro, String password) {
        this.name = name;
        this.role = role;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.imgUrl = imgUrl;
        this.intro = intro;
    }
}
