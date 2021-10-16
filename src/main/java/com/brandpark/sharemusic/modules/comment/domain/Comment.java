package com.brandpark.sharemusic.modules.comment.domain;

import com.brandpark.sharemusic.modules.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name="i_comment", columnList = "createDate desc"))
@Entity
public class Comment extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    private Long albumId;

    @Column(length = 100, nullable = false)
    private String content;
}
