package com.ctzn.ytsservice.application.channelrunner;

import com.ctzn.youtubescraper.core.persistence.dto.ChannelDTO;
import com.ctzn.youtubescraper.core.persistence.dto.ChannelVideosDTO;
import com.ctzn.youtubescraper.core.persistence.dto.CommentDTO;
import com.ctzn.youtubescraper.core.persistence.dto.VideoDTO;
import com.ctzn.ytsservice.application.service.ChannelService;
import com.ctzn.ytsservice.application.service.CommentService;
import com.ctzn.ytsservice.application.service.VideoService;
import com.ctzn.ytsservice.domain.entities.ChannelEntity;
import com.ctzn.ytsservice.domain.entities.CommentEntity;
import com.ctzn.ytsservice.domain.entities.VideoEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles({"h2db", "h2mem"})
@ExtendWith(SpringExtension.class)
@Transactional
class PersistenceServiceImplTest {

    @Autowired
    PersistenceServiceImpl persistenceService;

    @Autowired
    ChannelService channelService;

    @Autowired
    VideoService videoService;

    @Autowired
    CommentService commentService;

    private void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
    }

    private void print(String s) {
        sleep();
        String header = IntStream.range(0, s.length()).mapToObj(i -> "=").collect(Collectors.joining());
        System.out.println(header);
        System.out.println(s);
        System.out.println(header);
        sleep();
    }

    @Test
    void testChannelVideosPersistence() {
        String channelId = "chId01";
        ChannelDTO channelDTO1 = new ChannelDTO(channelId, "The channel 01", "Channel 1", 8, 100L);
        String videoId1 = "vidId01";
        String videoId2 = "vidId02";
        VideoDTO videoDTO1 = new VideoDTO(channelId, videoId1, "Video 01", "A moment ago", new Date(), 5);
        VideoDTO videoDTO2 = new VideoDTO(channelId, videoId2, "Video 02", "2 days ago", new Date(), 7);

        ChannelVideosDTO channelVideos = new ChannelVideosDTO(channelDTO1, List.of(videoDTO1, videoDTO2));
        print("=== SAVE CHANNEL VIDEOS ===");
        persistenceService.saveChannelVideos(channelVideos);

        String author1Text = "Author1";
        String author1Id = "authId1";
        String comment1Id = "12345678901234567890com__1";
        String rep1to1Id = "123456789012345rep1to1";
        CommentDTO commentDTO1 = new CommentDTO(videoId1, comment1Id, author1Text, author1Id, "2 seconds ago", new Date(), "Hello 1", 2, 1, null);
        CommentDTO replyDTO1 = new CommentDTO(videoId1, comment1Id + "." + rep1to1Id, author1Text, author1Id, "2 seconds ago", new Date(), "Reply to hello 1", 4, 0, comment1Id);
        print("=== SAVE VIDEO COMMENTS ===");
        persistenceService.saveVideoComments(videoId1, List.of(commentDTO1), List.of(replyDTO1));

        print("=== LOAD VIDEO 1 ===");
        VideoEntity videoEntity = videoService.getById(videoId1);
        assertEquals(videoId1, videoEntity.getVideoId());
        assertEquals(channelId, videoEntity.getChannelId());
        assertEquals("Video 01", videoEntity.getTitle());
        assertEquals("A moment ago", videoEntity.getPublishedTimeText());
        assertEquals(5, videoEntity.getViewCountText());

        print("=== LOAD VIDEO 2 ===");
        videoEntity = videoService.getById(videoId2);
        assertEquals(videoId2, videoEntity.getVideoId());
        assertEquals(channelId, videoEntity.getChannelId());
        assertEquals("Video 02", videoEntity.getTitle());
        assertEquals("2 days ago", videoEntity.getPublishedTimeText());
        assertEquals(7, videoEntity.getViewCountText());

        print("=== LOAD CHANNEL ===");
        ChannelEntity channelEntity = channelService.getById(channelId);
        assertEquals(channelId, channelEntity.getChannelId());
        assertEquals("The channel 01", channelEntity.getChannelVanityName());
        assertEquals("Channel 1", channelEntity.getTitle());
        assertEquals(8, channelEntity.getVideoCount());
        assertEquals(100, channelEntity.getSubscriberCount());

        print("=== QUERY COMMENTS ===");
        assertEquals(2, commentService.getComments("HELLO", Pageable.unpaged(), false).getContent().size());
        assertEquals(1, commentService.getComments("reply", Pageable.unpaged(), false).getContent().size());
        assertEquals(2, commentService.getComments("hell", Pageable.unpaged(), false).getContent().size());

        print("=== QUERY COMMENTS FTS ===");
        assertEquals(0, commentService.getComments("hell", Pageable.unpaged(), true).getContent().size());
        assertEquals(2, commentService.getComments("hello", Pageable.unpaged(), true).getContent().size());

        print("=== LOAD REPLY ===");
        CommentEntity reply = commentService.getComments("RePL", Pageable.unpaged(), false).getContent().get(0);
        assertEquals(comment1Id, reply.getNaturalId().getThreadId());
        assertEquals(rep1to1Id, reply.getNaturalId().getReplyId());
        assertEquals(comment1Id + "." + rep1to1Id, reply.getCommentId());
        assertEquals(author1Id, reply.getAuthorChannelId());
        assertEquals(author1Text, reply.getAuthorText());
        assertEquals("2 seconds ago", reply.getPublishedTimeText());
        assertEquals("Reply to hello 1", reply.getText());
        assertEquals(0, reply.getReplyCount());
        assertEquals(4, reply.getLikeCount());

        print("=== DELETE VIDEO 2 ===");
        videoService.deleteById(videoId2);
        assertNull(videoService.getById(videoId2));
        assertNotNull(videoService.getById(videoId1));
        assertNotNull(channelService.getById(channelId));

        print("=== DELETE CHANNEL ===");
        channelService.deleteChannel(channelId);

        assertNull(videoService.getById(videoId1));
        assertNull(channelService.getById(channelId));
    }

}
