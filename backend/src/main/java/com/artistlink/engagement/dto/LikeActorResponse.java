package com.artistlink.engagement.dto;

import com.artistlink.common.AuthorType;
import com.artistlink.engagement.Like;

import java.util.UUID;

public record LikeActorResponse(AuthorType actorType, UUID actorId) {
    public static LikeActorResponse from(Like l) {
        return new LikeActorResponse(l.getActorType(), l.getActorId());
    }
}
