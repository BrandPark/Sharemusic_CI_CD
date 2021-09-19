package com.brandpark.sharemusic.account.domain;

import com.brandpark.sharemusic.domain.BaseTimeEntity;
import lombok.*;
import org.springframework.security.core.userdetails.User;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter(AccessLevel.PRIVATE)
@NoArgsConstructor @AllArgsConstructor
@Builder @EqualsAndHashCode(of = "id")
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
        emailCheckToken = UUID.randomUUID().toString();
        emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public void assignRole(Role role) {
        this.role = role;
    }
}
