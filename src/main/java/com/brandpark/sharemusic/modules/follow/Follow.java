package com.brandpark.sharemusic.modules.follow;

import com.brandpark.sharemusic.modules.account.domain.Account;
import lombok.*;

import javax.persistence.*;

@Builder @AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "UK_FOLLOW", columnNames = {"follower_id", "target_id"}))
public class Follow {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "follower_id")
    private Account follower;

    @ManyToOne
    @JoinColumn(name = "target_id")
    private Account target;
}
