package com.ctzn.ytsservice.infrastructure.repositories.comment;

import com.ctzn.ytsservice.domain.entities.CommentEntity;
import com.ctzn.ytsservice.interfaces.rest.dto.UserProjection;
import com.ctzn.ytsservice.interfaces.rest.dto.UserSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

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
            "(SELECT AUTHOR_CHANNEL_ID,\n" +
            "MAX(AUTHOR_TEXT_ID) AS AUTHOR_TEXT_ID,\n" +
            "COUNT(DISTINCT(SELECT chn.CHANNEL_ID FROM VIDEOS AS V join channel_ids as chn on chn.id = v.channel_id WHERE V.VIDEO_ID = cin.VIDEO_ID)) AS COMMENTED_CHANNEL_COUNT,\n" +
            "COUNT(DISTINCT(VIDEO_ID)) AS COMMENTED_VIDEO_COUNT,\n" +
            "COUNT(1) AS COMMENT_COUNT,\n" +
            "SUM(LIKE_COUNT) AS LIKE_COUNT,\n" +
            "SUM(REPLY_COUNT) AS REPLY_COUNT,\n" +
            "MIN(PUBLISHED_DATE) AS FIRST_SEEN,\n" +
            "MAX(PUBLISHED_DATE) AS LAST_SEEN\n" +
            "FROM COMMENTS as cin\n" +
            "GROUP BY (AUTHOR_CHANNEL_ID)) AS C\n" +
            "JOIN AUTHOR_TEXTS AS R_T ON C.AUTHOR_TEXT_ID = R_T.ID\n" +
            "JOIN AUTHOR_CHANNELS AS R_C ON C.AUTHOR_CHANNEL_ID = R_C.ID\n"
            , countQuery = "select count(distinct(author_channel_id)) from comments"
            , nativeQuery = true)
    Page<UserProjection> getUsers(Pageable pageable);

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
            "(SELECT AUTHOR_CHANNEL_ID,\n" +
            "MAX(AUTHOR_TEXT_ID) AS AUTHOR_TEXT_ID,\n" +
            "COUNT(DISTINCT(SELECT chn.CHANNEL_ID FROM VIDEOS AS V join channel_ids as chn on chn.id = v.channel_id WHERE V.VIDEO_ID =  cin.VIDEO_ID)) AS COMMENTED_CHANNEL_COUNT,\n" +
            "COUNT(DISTINCT(VIDEO_ID)) AS COMMENTED_VIDEO_COUNT,\n" +
            "COUNT(1) AS COMMENT_COUNT,\n" +
            "SUM(LIKE_COUNT) AS LIKE_COUNT,\n" +
            "SUM(REPLY_COUNT) AS REPLY_COUNT,\n" +
            "MIN(PUBLISHED_DATE) AS FIRST_SEEN,\n" +
            "MAX(PUBLISHED_DATE) AS LAST_SEEN\n" +
            "FROM COMMENTS as cin\n" +
            "GROUP BY (AUTHOR_CHANNEL_ID)) AS C\n" +
            "JOIN AUTHOR_TEXTS AS R_T ON C.AUTHOR_TEXT_ID = R_T.ID\n" +
            "JOIN AUTHOR_CHANNELS AS R_C ON C.AUTHOR_CHANNEL_ID = R_C.ID\n" +
            "WHERE LOWER(R_T.TEXT) like CONCAT(LOWER(?1),'%')"
            , countQuery = "" +
            "SELECT COUNT(DISTINCT(AUTHOR_CHANNEL_ID))\n" +
            "FROM COMMENTS AS C\n" +
            "JOIN AUTHOR_TEXTS AS R_T ON C.AUTHOR_TEXT_ID = R_T.ID\n" +
            "WHERE LOWER(R_T.TEXT) like CONCAT(LOWER(?1),'%')"
            , nativeQuery = true)
    Page<UserProjection> getUsers(String text, Pageable pageable);

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

}

// users commented on the same videos
//===================================
// SELECT
// user_id,
// user_title,
// count(1) as commented_video_count,
// sum(comment_count) as comment_count,
// sum(like_count) as like_count,
// sum(reply_count) as reply_count
// FROM (
// SELECT
// video_id,
// channel_id as user_id,
// author_text as user_title,
// count(1) as comment_count,
// sum(like_count) as like_count,
// sum(reply_count) as reply_count
// FROM comments
// GROUP BY (video_id, channel_id)
// )
// WHERE video_id in (
// SELECT DISTINCT
// video_id
// FROM
// comments
// WHERE channel_id = 'UCxxxxxxxxxxxxxxxxxx'
// )
// GROUP BY user_id
// HAVING commented_video_count > 1
// ORDER BY commented_video_count DESC

// known names list
//    SELECT T.author_text as known_as, min(T.published_date) as first_seen, max(T.published_date) as last_seen
//        FROM comments AS T
//        WHERE T.channel_id = 'UCCxxxxxxxxxxxxx'
//        group by T.author_text
//        order by last_seen


//    SELECT R_C.CHANNEL_ID AS authorChannelId,
//        R_T.TEXT AS authorText,
//        C.COMMENTED_VIDEO_COUNT AS commentedVideoCount,
//        C.COMMENT_COUNT AS commentCount,
//        C.LIKE_COUNT AS likeCount,
//        C.REPLY_COUNT AS replyCount,
//        C.FIRST_SEEN AS firstSeen,
//        C.LAST_SEEN AS lastSeen
//        FROM
//        (SELECT AUTHOR_CHANNEL_ID,
//        (ARRAY_AGG(DISTINCT(AUTHOR_TEXT_ID)))[1] AS AUTHOR_TEXT_ID,
//        COUNT(DISTINCT(VIDEO_ID)) AS COMMENTED_VIDEO_COUNT,
//        COUNT(1) AS COMMENT_COUNT,
//        SUM(LIKE_COUNT) AS LIKE_COUNT,
//        SUM(REPLY_COUNT) AS REPLY_COUNT,
//        MIN(PUBLISHED_DATE) AS FIRST_SEEN,
//        MAX(PUBLISHED_DATE) AS LAST_SEEN
//        FROM COMMENTS
//        GROUP BY (AUTHOR_CHANNEL_ID)) AS C
//        JOIN AUTHOR_TEXTS AS R_T ON C.AUTHOR_TEXT_ID = R_T.ID
//        JOIN AUTHOR_CHANNELS AS R_C ON C.AUTHOR_CHANNEL_ID = R_C.ID


//    SELECT AUTHOR_CHANNEL_ID,
//(ARRAY_AGG(DISTINCT(AUTHOR_TEXT_ID))) AS AUTHOR_TEXT_ID,
//        COUNT(DISTINCT(VIDEO_ID)) AS COMMENTED_VIDEO_COUNT,
//        COUNT(1) AS COMMENT_COUNT,
//        SUM(LIKE_COUNT) AS LIKE_COUNT,
//        SUM(REPLY_COUNT) AS REPLY_COUNT,
//        MIN(PUBLISHED_DATE) AS FIRST_SEEN,
//        MAX(PUBLISHED_DATE) AS LAST_SEEN,
//        count(distinct(SELECT channel_id from videos as v where v.video_id = c.video_id)) as channels
//        FROM COMMENTS as c
//        GROUP BY (AUTHOR_CHANNEL_ID)


//    SELECT AUTHOR_CHANNEL_ID,
//(max(AUTHOR_TEXT_ID)) AS AUTHOR_TEXT_ID,
//        COUNT(DISTINCT(VIDEO_ID)) AS COMMENTED_VIDEO_COUNT,
//        COUNT(1) AS COMMENT_COUNT,
//        SUM(LIKE_COUNT) AS LIKE_COUNT,
//        SUM(REPLY_COUNT) AS REPLY_COUNT,
//        MIN(PUBLISHED_DATE) AS FIRST_SEEN,
//        MAX(PUBLISHED_DATE) AS LAST_SEEN,
//        count(distinct(SELECT channel_id from videos as v where v.video_id = c.video_id)) as channels
//        FROM COMMENTS as c
//        GROUP BY (AUTHOR_CHANNEL_ID)

//    SELECT AUTHOR_CHANNEL_ID,
//(max(AUTHOR_TEXT_ID)) AS AUTHOR_TEXT_ID,
//        COUNT(DISTINCT(VIDEO_ID)) AS COMMENTED_VIDEO_COUNT,
//        COUNT(1) AS COMMENT_COUNT,
//        SUM(LIKE_COUNT) AS LIKE_COUNT,
//        SUM(REPLY_COUNT) AS REPLY_COUNT,
//        MIN(PUBLISHED_DATE) AS FIRST_SEEN,
//        MAX(PUBLISHED_DATE) AS LAST_SEEN,
//        count(distinct(SELECT channel_id from videos as v where v.video_id = c.video_id)) as channels
//        FROM COMMENTS as c
//        GROUP BY (AUTHOR_CHANNEL_ID)
//        having channels>1
//        order by Like_count desc


//    SELECT AUTHOR_CHANNEL_ID,
//    COUNT(DISTINCT ATX.TEXT) AS NAME_COUNT,
//    STRING_AGG(DISTINCT ATX.TEXT,',') AS KNOWN_NAMES,
//    COUNT(DISTINCT
//											(SELECT CHANNEL_ID
//                                                    FROM VIDEOS AS V
//                                                    WHERE V.VIDEO_ID = C.VIDEO_ID)) AS COMMENTED_CHANNEL_COUNT,
//        COUNT(DISTINCT(VIDEO_ID)) AS COMMENTED_VIDEO_COUNT,
//        COUNT(1) AS COMMENT_COUNT,
//        SUM(LIKE_COUNT) AS LIKE_COUNT,
//        SUM(REPLY_COUNT) AS REPLY_COUNT,
//        MIN(PUBLISHED_DATE) AS FIRST_SEEN,
//        MAX(PUBLISHED_DATE) AS LAST_SEEN
//        FROM COMMENTS AS C
//        JOIN AUTHOR_TEXTS AS ATX ON ATX.ID = AUTHOR_TEXT_ID
//        GROUP BY (AUTHOR_CHANNEL_ID)
//        ORDER BY NAME_COUNT DESC

//    SELECT ATC.CHANNEL_ID,
//        COUNT(DISTINCT ATX.TEXT) AS NAME_COUNT,
//        STRING_AGG(DISTINCT ATX.TEXT,',') AS KNOWN_NAMES,
//        COUNT(DISTINCT
//        (SELECT CHANNEL_ID
//        FROM VIDEOS AS V
//        WHERE V.VIDEO_ID = C.VIDEO_ID)) AS COMMENTED_CHANNEL_COUNT,
//        COUNT(DISTINCT(VIDEO_ID)) AS COMMENTED_VIDEO_COUNT,
//        COUNT(1) AS COMMENT_COUNT,
//        SUM(LIKE_COUNT) AS LIKE_COUNT,
//        SUM(REPLY_COUNT) AS REPLY_COUNT,
//        MIN(PUBLISHED_DATE) AS FIRST_SEEN,
//        MAX(PUBLISHED_DATE) AS LAST_SEEN
//        FROM COMMENTS AS C
//        JOIN AUTHOR_TEXTS AS ATX ON ATX.ID = AUTHOR_TEXT_ID
//        JOIN AUTHOR_channels AS ATC ON ATC.ID = AUTHOR_CHANNEL_ID
//        GROUP BY (ATC.CHANNEL_ID)
//        ORDER BY NAME_COUNT DESC

//    SELECT ATC.CHANNEL_ID AS authorChannelId,
//        ARRAY_AGG(DISTINCT ATX.TEXT) AS knownNames,
//        ARRAY_AGG(DISTINCT(SELECT chn.CHANNEL_ID FROM VIDEOS AS V join channel_ids as chn on chn.id = v.channel_id WHERE V.VIDEO_ID = C.VIDEO_ID)) AS commentedChannels,
//        ARRAY_AGG(DISTINCT(SELECT v_ids.video_ID FROM video_ids as v_ids WHERE v_ids.id = C.VIDEO_ID)) AS commentedVideos,
//        COUNT(1) AS commentCount,
//        SUM(LIKE_COUNT) AS likeCount,
//        SUM(REPLY_COUNT) AS replyCount,
//        MIN(PUBLISHED_DATE) AS firstSeen,
//        MAX(PUBLISHED_DATE) AS lastSeen
//        FROM COMMENTS AS C
//        JOIN AUTHOR_TEXTS AS ATX ON ATX.ID = AUTHOR_TEXT_ID
//        JOIN AUTHOR_CHANNELS AS ATC ON ATC.ID = AUTHOR_CHANNEL_ID
//        WHERE CHANNEL_ID = 'UCqxxxxxxxxxxxxxxxxxx'
//        GROUP BY (ATC.CHANNEL_ID)
