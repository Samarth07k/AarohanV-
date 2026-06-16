package com.artistlink.media.dto;

import com.artistlink.media.MediaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AttachMediaRequest(
        @NotNull MediaType mediaType,
        @NotBlank String url,
        String thumbnailUrl,
        Integer width,
        Integer height,
        Integer durationSeconds,
        Integer displayOrder
) {}
