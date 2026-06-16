package com.artistlink.post.dto;

import com.artistlink.post.PostVisibility;
import jakarta.validation.constraints.Size;

public record UpdatePostRequest(
        @Size(max = 5000) String content,
        PostVisibility visibility
) {}
