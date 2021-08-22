package com.brandpark.sharemusic.service;

import com.brandpark.sharemusic.domain.user.User;
import com.brandpark.sharemusic.exception.DuplicateUserException;
import com.brandpark.sharemusic.web.api.dto.UserSaveRequestDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private EntityManager em;

    @Test
    public void 회원가입이_된다() throws Exception {
        // given
        UserSaveRequestDto form = createSaveDto();

        // when
        Long saveId = userService.saveUser(form);
        persistToDb();

        // then
        User findUser = em.find(User.class, saveId);
        assertThat(findUser.getEmail()).isEqualTo(form.getEmail());
        assertThat(findUser.getName()).isEqualTo(form.getName());
        assertThat(findUser.getNickname()).isEqualTo(form.getNickname());
        assertThat(findUser.getPassword()).isEqualTo(form.getPassword());
    }

    @Test
    public void 같은_이메일로_중복_가입은_예외처리() throws Exception {
        // given
        userService.saveUser(createSaveDto());
        persistToDb();

        // when
        // then
        assertThatThrownBy(() -> {
            userService.saveUser(createSaveDto());
        }).isInstanceOf(DuplicateUserException.class).hasMessageContaining("계정이 이미 존재합니다.");
    }

    private void persistToDb() {
        em.flush();
        em.clear();
    }

    private UserSaveRequestDto createSaveDto() {
        UserSaveRequestDto dto = new UserSaveRequestDto();
        dto.setEmail("email");
        dto.setName("mingon");
        dto.setNickname("min");
        dto.setPassword("123");

        return dto;
    }

}