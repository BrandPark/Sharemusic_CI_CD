package com.brandpark.sharemusic.api.v1.album;

import com.brandpark.sharemusic.api.AlbumFactory;
import com.brandpark.sharemusic.infra.MockMvcTest;
import com.brandpark.sharemusic.modules.AccountFactory;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.AlbumRepository;
import com.brandpark.sharemusic.modules.comment.domain.Comment;
import com.brandpark.sharemusic.modules.comment.domain.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@MockMvcTest
@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CommentApiControllerTest {

    @Autowired AlbumFactory albumFactory;
    @Autowired AccountFactory accountFactory;
    @Autowired AccountRepository accountRepository;
    @Autowired AlbumRepository albumRepository;
    @Autowired CommentRepository commentRepository;
    @Autowired TestRestTemplate restTemplate;
    @Autowired MockMvc mockMvc;
    @LocalServerPort String port;
    Album savedAlbum;


    @BeforeEach
    public void setUp() {
        Account account = accountFactory.createAccount("account");
        accountRepository.save(account);

        savedAlbum = albumFactory.createAlbumWithTracks(5, account.getId());
        albumRepository.save(savedAlbum);
    }

    @DisplayName("앨범의 댓글들 페이징 조회")
    @Test
    public void RetrieveAllCommentsByPaging_Success() throws Exception {

        // given
        Comment comment1 = albumFactory.createComment(savedAlbum.getId(), savedAlbum.getAccountId(), "댓글1");
        Comment comment2 = albumFactory.createComment(savedAlbum.getId(), savedAlbum.getAccountId(), "댓글2");
        commentRepository.save(comment1);
        commentRepository.save(comment2);

        String url = "http://localhost:" + port + "/api/v1/albums/" + savedAlbum.getId() + "/comments?page=0";

        // when, then



    }

}