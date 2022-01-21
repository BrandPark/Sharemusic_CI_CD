package com.brandpark.sharemusic.modules.album.domain;

import com.brandpark.sharemusic.modules.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
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

    public static Comment createComment(Long accountId, Long albumId, String content) {
        Comment comment = new Comment();

        comment.accountId = accountId;
        comment.albumId = albumId;
        comment.content = content;

        return comment;
    }
}
