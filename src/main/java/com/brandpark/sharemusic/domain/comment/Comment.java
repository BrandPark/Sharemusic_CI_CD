package com.brandpark.sharemusic.domain.comment;

import com.brandpark.sharemusic.domain.BaseTimeEntity;
import com.brandpark.sharemusic.domain.album.Album;
import com.brandpark.sharemusic.domain.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album album;

    private String contents;

    public static Comment createComment(Album album, User user, String contents) {
        Comment comment = new Comment();
        comment.album = album;
        comment.user = user;
        comment.contents = contents;

        return comment;
    }

    public void update(String contents) {
        this.contents = contents;
    }
}
