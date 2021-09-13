package com.brandpark.sharemusic.domain.user;

import com.brandpark.sharemusic.domain.BaseTimeEntity;
import com.brandpark.sharemusic.account.Role;
import lombok.AccessLevel;
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

    public static User createUser(String email, String name, String nickname, String password) {
        User user = new User();
        user.email = email;
        user.name = name;
        user.nickname = nickname;
        user.password = password;

        return user;
    }

    public void updateProfile(String nickname, String imgUrl, String password, String intro) {
        this.nickname = nickname;
        this.imgUrl = imgUrl;
        this.password = password;
        this.intro = intro;
    }
}
