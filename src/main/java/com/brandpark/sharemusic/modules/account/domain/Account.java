package com.brandpark.sharemusic.modules.account.domain;

import com.brandpark.sharemusic.modules.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
public class Account extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @Column(name="account_name", nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String bio;

    @Lob
    private String profileImage;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String emailCheckToken;

    private LocalDateTime emailCheckTokenGeneratedAt;

    public void generateEmailCheckToken() {
        if (emailCheckToken == null) {
            emailCheckToken = UUID.randomUUID().toString();
            emailCheckTokenGeneratedAt = LocalDateTime.now();
        }
    }

    public void assignRole(Role role) {
        if(this.role != role)
            this.role = role;
    }

    public void updateInfo(String name, String nickName, String bio, String profileImage) {
        this.name = name;
        this.nickname = nickName;
        this.bio = bio;
        this.profileImage = profileImage;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public static Account createAccount(String email, String name, String nickname, String password) {
        Account newAccount = new Account();

        newAccount.email = email;
        newAccount.name = name;
        newAccount.nickname = nickname;
        newAccount.password = password;

        newAccount.assignRole(Role.GUEST);
        newAccount.generateEmailCheckToken();

        return newAccount;
    }
}
