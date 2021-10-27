package com.brandpark.sharemusic.api.v1.album.dto;

import com.brandpark.sharemusic.api.v1.album.query.dto.CommentDetailDto;
import com.brandpark.sharemusic.api.v2.dto.PagingDto;
import lombok.Data;

@Data
public class CommentListPagingDto extends PagingDto<CommentDetailDto> {
}