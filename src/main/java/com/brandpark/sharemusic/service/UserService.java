package com.brandpark.sharemusic.service;

import com.brandpark.sharemusic.domain.user.UserRepository;
import com.brandpark.sharemusic.exception.DuplicateUserException;
import com.brandpark.sharemusic.web.api.dto.UserSaveRequestDto;
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
}
