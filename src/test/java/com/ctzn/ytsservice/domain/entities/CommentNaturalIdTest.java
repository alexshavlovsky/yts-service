package com.ctzn.ytsservice.domain.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


class CommentNaturalIdTest {

    @Test
    void newFromPublicId_replyId() {
        CommentNaturalId commentNaturalId = CommentNaturalId
                .newFromPublicId("UgyHEKEMxW4hEtPvaZN5AaABAg.8MaaHnB_rlE8MakQHuIFB2");
        assertEquals("UgyHEKEMxW4hEtPvaZN5AaABAg", commentNaturalId.getThreadId());
        assertEquals("8MaaHnB_rlE8MakQHuIFB2", commentNaturalId.getReplyId());
    }

    @Test
    void newFromPublicId_commentId() {
        CommentNaturalId commentNaturalId = CommentNaturalId
                .newFromPublicId("UgyHEKEMxW4hEtPvaZN5AaABAg");
        assertEquals("UgyHEKEMxW4hEtPvaZN5AaABAg", commentNaturalId.getThreadId());
        assertNull(commentNaturalId.getReplyId());
    }

}
