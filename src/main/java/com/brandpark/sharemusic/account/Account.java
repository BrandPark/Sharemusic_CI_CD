package com.brandpark.sharemusic.account;

import com.brandpark.sharemusic.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @NoArgsConstructor @AllArgsConstructor
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
}
