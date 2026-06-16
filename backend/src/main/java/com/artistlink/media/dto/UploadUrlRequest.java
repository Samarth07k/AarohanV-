package com.artistlink.media.dto;

import com.artistlink.media.MediaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UploadUrlRequest(
        @NotNull MediaType mediaType,
        @NotBlank String fileName,
        String contentType
) {}
