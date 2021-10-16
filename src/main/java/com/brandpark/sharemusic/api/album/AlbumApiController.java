package com.brandpark.sharemusic.api.album;

import com.brandpark.sharemusic.api.DtoValidator;
import com.brandpark.sharemusic.api.album.dto.AlbumSaveRequest;
import com.brandpark.sharemusic.api.album.dto.AlbumUpdateRequest;
import com.brandpark.sharemusic.api.album.query.AlbumQueryRepository;
import com.brandpark.sharemusic.api.album.query.AlbumShortDto;
import com.brandpark.sharemusic.api.album.query.CommentDetailDto;
import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.service.AlbumService;
import com.brandpark.sharemusic.modules.comment.CommentService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class AlbumApiController {

    private final AlbumService albumService;
    private final DtoValidator dtoValidator;
    private final AlbumQueryRepository albumQueryRepository;
    private final CommentService commentService;

    @GetMapping("/albums")
    public Page<AlbumShortDto> getAllAlbumShort(@PageableDefault(size = 9) Pageable pageable) {
        return albumQueryRepository.findAllAlbumShortDtos(pageable);
    }

    @PostMapping("/albums")
    public Long createAlbum(@LoginAccount SessionAccount account, @RequestBody AlbumSaveRequest requestDto) {

        dtoValidator.validateAlbumSaveDto(requestDto, account.getId());

        return albumService.saveAlbum(account.getId(), requestDto);
    }

    @PutMapping("/albums/{albumId}")
    public Long updateAlbum(@LoginAccount SessionAccount account, @RequestBody AlbumUpdateRequest requestDto
            , @PathVariable("albumId") Album album) {

        dtoValidator.validateAlbumUpdateDto(requestDto, account.getId(), album.getId());

        albumService.updateAlbum(requestDto, album);

        return album.getId();
    }

    @GetMapping("/albums/{albumId}/comments")
    public Page<CommentDetailDto> getAllComments(@PathVariable Long albumId, @PageableDefault(size = 10, sort = "create_date", direction = Sort.Direction.DESC) Pageable pageable) {
        return albumQueryRepository.findAllCommentDetailDtosByAlbumId(albumId, pageable);
    }

    @PostMapping("/albums/{albumId}/comments")
    public Long saveComment(@LoginAccount SessionAccount sessionAccount, @PathVariable Long albumId, @RequestBody String content) {
        return commentService.saveComment(albumId, sessionAccount.getId(), content);
    }

    @Builder
    public static class ResultPage<T> {
        private T data;
        private int totalPages;
        private long totalElements;
        private int pageNumber;
        private int numberOfElements;
    }
}
