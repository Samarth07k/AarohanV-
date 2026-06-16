package com.artistlink.media.dto;

/**
 * Signed-URL response (Blueprint 10.2). Client PUTs the file to uploadUrl,
 * then references the resulting fileUrl when attaching media to a post.
 */
public record UploadUrlResponse(String uploadUrl, String fileUrl, String key) {}
