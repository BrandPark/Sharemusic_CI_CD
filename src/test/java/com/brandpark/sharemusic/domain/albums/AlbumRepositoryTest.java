package com.brandpark.sharemusic.domain.albums;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AlbumRepositoryTest {

    @Autowired
    AlbumRepository albumRepository;

    @Test
    public void 앨범_저장_및_불러오기() {
        //given
        Album target = new Album("mingon");

        //when
        albumRepository.save(target);

        //then
        Album saved = albumRepository.findAll().get(0);
        assertThat(saved.getName()).isEqualTo("mingon");
    }

}