package com.ctzn.ytsservice.infrastructure.repositories.comment;

import com.ctzn.ytsservice.domain.entities.CommentEntity;
import com.ctzn.ytsservice.interfaces.rest.dto.UserCommonCommentedVideosProjection;
import com.ctzn.ytsservice.interfaces.rest.dto.UserProjection;
import com.ctzn.ytsservice.interfaces.rest.dto.UserSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface CommentRepository extends PagingAndSortingRepository<CommentEntity, Long> {

    Optional<CommentEntity> findByNaturalId_threadIdAndNaturalId_replyId(String threadId, String replyId);

    Page<CommentEntity> findAllByTextContainingIgnoreCase(String text, Pageable pageable);

    Page<CommentEntity> nativeFts(String query, Pageable pageable);

    long countByVideo_naturalId_videoId(String videoId);

    @Query(value = "" +
            "SELECT R_C.CHANNEL_ID AS authorChannelId,\n" +
            "R_T.TEXT AS authorText,\n" +
            "C.COMMENTED_CHANNEL_COUNT AS commentedChannelCount,\n" +
            "C.COMMENTED_VIDEO_COUNT AS commentedVideoCount,\n" +
            "C.COMMENT_COUNT AS commentCount,\n" +
            "C.LIKE_COUNT AS likeCount,\n" +
            "C.REPLY_COUNT AS replyCount,\n" +
            "C.FIRST_SEEN AS firstSeen,\n" +
            "C.LAST_SEEN AS lastSeen\n" +
            "FROM\n" +
            "(SELECT cin.AUTHOR_CHANNEL_ID,\n" +
            "MAX(cin.AUTHOR_TEXT_ID) AS AUTHOR_TEXT_ID,\n" +
            "COUNT(DISTINCT(chn.CHANNEL_ID)) AS COMMENTED_CHANNEL_COUNT,\n" +
            "COUNT(DISTINCT(cin.VIDEO_ID)) AS COMMENTED_VIDEO_COUNT,\n" +
            "COUNT(1) AS COMMENT_COUNT,\n" +
            "SUM(cin.LIKE_COUNT) AS LIKE_COUNT,\n" +
            "SUM(cin.REPLY_COUNT) AS REPLY_COUNT,\n" +
            "MIN(cin.PUBLISHED_DATE) AS FIRST_SEEN,\n" +
            "MAX(cin.PUBLISHED_DATE) AS LAST_SEEN\n" +
            "FROM COMMENTS as cin\n" +
            "join VIDEOS AS V on v.video_id = cin.video_id\n" +
            "join channel_ids as chn on chn.id = v.channel_id\n" +
            "GROUP BY (AUTHOR_CHANNEL_ID)) AS C\n" +
            "JOIN AUTHOR_TEXTS AS R_T ON C.AUTHOR_TEXT_ID = R_T.ID\n" +
            "JOIN AUTHOR_CHANNELS AS R_C ON C.AUTHOR_CHANNEL_ID = R_C.ID\n"
            , countQuery = "select count(distinct(author_channel_id)) from comments"
            , nativeQuery = true)
    Page<UserProjection> getUsers(Pageable pageable);

    @Query(value = "" +
            "SELECT\n" +
            "CO.AUTHOR_CHANNEL_ID AS authorChannelId,\n" +
            "CO.AUTHOR_TEXT AS authorText,\n" +
            "CO.COMMENTED_CHANNEL_COUNT AS commentedChannelCount,\n" +
            "CO.COMMENTED_VIDEO_COUNT AS commentedVideoCount,\n" +
            "CO.COMMENT_COUNT AS commentCount,\n" +
            "CO.LIKE_COUNT AS likeCount,\n" +
            "CO.REPLY_COUNT AS replyCount,\n" +
            "CO.FIRST_SEEN AS firstSeen,\n" +
            "CO.LAST_SEEN AS lastSeen\n" +
            "from\n" +
            "(WITH TTT AS\t(SELECT CMTS.AUTHOR_CHANNEL_ID AS AUTHOR_CHANNEL_ID,\n" +
            "\tSTRING_AGG(DISTINCT(ATXT.TEXT), ', ') AS AUTHOR_TEXT\n" +
            "\tFROM AUTHOR_TEXTS AS ATXT\n" +
            "\tJOIN COMMENTS AS CMTS ON ATXT.ID = CMTS.AUTHOR_TEXT_ID\n" +
            "\tWHERE LOWER(ATXT.TEXT) like CONCAT(LOWER(:text),'%')\n" +
            "\tGROUP BY (CMTS.AUTHOR_CHANNEL_ID))\n" +
            "SELECT R_C.CHANNEL_ID AS AUTHOR_CHANNEL_ID,\n" +
            "\tTTT.AUTHOR_TEXT AS AUTHOR_TEXT,\n" +
            "\tCOUNT(DISTINCT(CHN.CHANNEL_ID)) AS COMMENTED_CHANNEL_COUNT,\n" +
            "\tCOUNT(DISTINCT(C.VIDEO_ID)) AS COMMENTED_VIDEO_COUNT,\n" +
            "\tCOUNT(1) AS COMMENT_COUNT,\n" +
            "\tSUM(C.LIKE_COUNT) AS LIKE_COUNT,\n" +
            "\tSUM(C.REPLY_COUNT) AS REPLY_COUNT,\n" +
            "\tMIN(C.PUBLISHED_DATE) AS FIRST_SEEN,\n" +
            "\tMAX(C.PUBLISHED_DATE) AS LAST_SEEN\n" +
            "FROM COMMENTS AS C\n" +
            "JOIN VIDEOS AS V ON V.VIDEO_ID = C.VIDEO_ID\n" +
            "JOIN CHANNEL_IDS AS CHN ON CHN.ID = V.CHANNEL_ID\n" +
            "JOIN AUTHOR_CHANNELS AS R_C ON C.AUTHOR_CHANNEL_ID = R_C.ID\n" +
            "JOIN TTT ON C.AUTHOR_CHANNEL_ID = TTT.AUTHOR_CHANNEL_ID\n" +
            "WHERE C.AUTHOR_CHANNEL_ID in (SELECT AUTHOR_CHANNEL_ID FROM TTT)\n" +
            "GROUP BY (R_C.CHANNEL_ID, TTT.AUTHOR_TEXT)) AS CO"
            , countQuery = "" +
            "SELECT COUNT(DISTINCT(AUTHOR_CHANNEL_ID))\n" +
            "FROM COMMENTS AS C\n" +
            "JOIN AUTHOR_TEXTS AS R_T ON C.AUTHOR_TEXT_ID = R_T.ID\n" +
            "WHERE LOWER(R_T.TEXT) like CONCAT(LOWER(:text),'%')"
            , nativeQuery = true)
    Page<UserProjection> getUsers(@Param("text") String text, Pageable pageable);

    @Query(value = "" +
            "SELECT ATC.CHANNEL_ID AS authorChannelId,\n" +
            "ARRAY_AGG(DISTINCT ATX.TEXT) AS knownNames,\n" +
            "ARRAY_AGG(DISTINCT(SELECT chn.CHANNEL_ID FROM VIDEOS AS V join channel_ids as chn on chn.id = v.channel_id WHERE V.VIDEO_ID = C.VIDEO_ID)) AS commentedChannels,\n" +
            "ARRAY_AGG(DISTINCT(SELECT v_ids.video_ID FROM video_ids as v_ids WHERE v_ids.id = C.VIDEO_ID)) AS commentedVideos,\n" +
            "COUNT(1) AS commentCount,\n" +
            "SUM(LIKE_COUNT) AS likeCount,\n" +
            "SUM(REPLY_COUNT) AS replyCount,\n" +
            "MIN(PUBLISHED_DATE) AS firstSeen,\n" +
            "MAX(PUBLISHED_DATE) AS lastSeen\n" +
            "FROM COMMENTS AS C\n" +
            "JOIN AUTHOR_TEXTS AS ATX ON ATX.ID = AUTHOR_TEXT_ID\n" +
            "JOIN AUTHOR_CHANNELS AS ATC ON ATC.ID = AUTHOR_CHANNEL_ID\n" +
            "WHERE CHANNEL_ID = ?1\n" +
            "GROUP BY (ATC.CHANNEL_ID)"
            , nativeQuery = true)
    UserSummaryProjection getUser(String userId);

    @Query(value = "" +
            "SELECT\n" +
            "R_C.CHANNEL_ID AS authorChannelId,\n" +
            "R_T.TEXT AS authorText,\n" +
            "count(1) as videoCount ,\n" +
            "array_agg(R_V.video_id) as videos\n" +
            "FROM\n" +
            "(SELECT cin.AUTHOR_CHANNEL_ID,\n" +
            "cin.VIDEO_ID,\n" +
            "MAX(cin.AUTHOR_TEXT_ID) AS AUTHOR_TEXT_ID,\n" +
            "FROM COMMENTS as cin\n" +
            "where video_id in (\n" +
            "SELECT DISTINCT video_id FROM comments as c\n" +
            "join author_channels as ac on c.author_channel_id = ac.id\n" +
            "WHERE ac.channel_id = ?1\n" +
            ") GROUP BY (AUTHOR_CHANNEL_ID, cin.VIDEO_ID)) as c\n" +
            "JOIN AUTHOR_TEXTS AS R_T ON C.AUTHOR_TEXT_ID = R_T.ID\n" +
            "JOIN AUTHOR_CHANNELS AS R_C ON C.AUTHOR_CHANNEL_ID = R_C.ID\n" +
            "JOIN VIDEO_IDS R_V ON C.VIDEO_ID = R_V.ID\n" +
            "where R_C.CHANNEL_ID != ?1\n" +
            "GROUP BY (AUTHOR_CHANNEL_ID, AUTHOR_TEXT_ID)\n" +
            "ORDER BY videoCount DESC\n" +
            "LIMIT 10\n"
            , nativeQuery = true)
    List<UserCommonCommentedVideosProjection> getTop10CommonCommentedVideos(String userId);

}
