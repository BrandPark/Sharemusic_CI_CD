package com.brandpark.sharemusic.service;

import com.brandpark.sharemusic.domain.user.User;
import com.brandpark.sharemusic.domain.user.UserRepository;
import com.brandpark.sharemusic.exception.DuplicateUserException;
import com.brandpark.sharemusic.exception.NotFoundUserException;
import com.brandpark.sharemusic.web.api.dto.UserSaveRequestDto;
import com.brandpark.sharemusic.web.api.dto.UserUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public Long saveUser(UserSaveRequestDto requestDto) {
        userRepository.findByEmail(requestDto.getEmail())
                .ifPresent(user -> {
                    throw new DuplicateUserException("'" + user.getEmail() + "'" + "로 계정이 이미 존재합니다.");
                });

        return userRepository.save(requestDto.toEntity()).getId();
    }

    @Transactional
    public void updateProfile(Long userId, UserUpdateRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException("존재하지 않는 사용자입니다."));

        user.updateProfile(requestDto.getNickname(), requestDto.getImgUrl(), requestDto.getPassword(), requestDto.getIntro());
    }
}
