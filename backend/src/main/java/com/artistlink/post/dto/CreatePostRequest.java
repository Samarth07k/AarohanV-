package com.artistlink.post.dto;

import com.artistlink.post.LinkedEntityType;
import com.artistlink.post.PostType;
import com.artistlink.post.PostVisibility;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

/** POST /posts — Blueprint 9.1 payload. */
public record CreatePostRequest(
        @NotNull PostType postType,
        @Size(max = 5000) String content,
        List<UUID> mediaIds,          // attachments already uploaded via signed-URL flow
        LinkedEntityType linkedEntityType,
        UUID linkedEntityId,
        PostVisibility visibility
) {}
