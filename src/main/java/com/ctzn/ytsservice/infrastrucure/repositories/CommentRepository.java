package com.ctzn.ytsservice.infrastrucure.repositories;

import com.ctzn.ytsservice.domain.entities.CommentEntity;
import com.ctzn.ytsservice.interfaces.rest.dto.UserProjection;
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
            "SELECT\n" +
            "user_id as userId,\n" +
            "user_title as userTitle,\n" +
            "count(1) as commentedVideoCount,\n" +
            "sum(comment_count) as commentCount,\n" +
            "sum(like_count) as likeCount,\n" +
            "sum(reply_count) as replyCount\n" +
            "FROM (\n" +
            "SELECT\n" +
            "video_id,\n" +
            "channel_id as user_id,\n" +
            "author_text as user_title,\n" +
            "count(1) as comment_count,\n" +
            "sum(like_count) as like_count,\n" +
            "sum(reply_count) as reply_count\n" +
            "FROM comments\n" +
            "GROUP BY (video_id, channel_id, author_text)\n" +
            ") as sub\n" +
            "GROUP BY (userId, userTitle)\n"
            , countQuery = "select count(1) from (select distinct channel_id from comments) as sub"
            , nativeQuery = true)
    Page<UserProjection> getUsers(Pageable pageable);

    @Query(value = "" +
            "SELECT\n" +
            "user_id as userId,\n" +
            "user_title as userTitle,\n" +
            "count(1) as commentedVideoCount,\n" +
            "sum(comment_count) as commentCount,\n" +
            "sum(like_count) as likeCount,\n" +
            "sum(reply_count) as replyCount\n" +
            "FROM (\n" +
            "SELECT\n" +
            "video_id,\n" +
            "channel_id as user_id,\n" +
            "author_text as user_title,\n" +
            "count(1) as comment_count,\n" +
            "sum(like_count) as like_count,\n" +
            "sum(reply_count) as reply_count\n" +
            "FROM comments\n" +
            "WHERE LOWER(author_text) like CONCAT('%',LOWER(?1),'%')" +
            "GROUP BY (video_id, channel_id, author_text)\n" +
            ") as sub\n" +
            "GROUP BY (userId, userTitle)\n"
            , countQuery = "select count(1) from (select distinct channel_id from comments where LOWER(author_text) like CONCAT('%',LOWER(?1),'%')) as sub"
            , nativeQuery = true)
    Page<UserProjection> getUsers(String text, Pageable pageable);
}

// sub query: users per video commented at least once
//====================================================
// SELECT
// video_id,
// channel_id as user_id,
// author_text as user_title,
// count(1) as comment_count,
// sum(like_count) as like_count,
// sum(reply_count) as reply_count
// FROM comments
// GROUP BY (video_id, channel_id)

// users by commented video count
//================================
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
// GROUP BY user_id
// ORDER BY commented_video_count DESC

// commented videos by user
//==========================
// SELECT DISTINCT
// video_id
// FROM
// comments
// WHERE channel_id = 'UCB12jjYsYv-eipCvBDcMbXw'

// users commented on ten same videos
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
// WHERE channel_id = 'UCB12jjYsYv-eipCvBDcMbXw'
// )
// GROUP BY user_id
// HAVING commented_video_count > 1
// ORDER BY commented_video_count DESC

// known names list
//    SELECT T.author_text as known_as, min(T.published_date) as first_seen, max(T.published_date) as last_seen
//        FROM comments AS T
//        WHERE T.channel_id = 'UCC_J43KR0LEluXM85OBm9nQ'
//        group by T.author_text
//        order by last_seen


//    CREATE TABLE authors (author_id, author_text)
//    AS (select distinct channel_id as author_id, author_text from comments)

//    CREATE TABLE authors (channel_id, author_text)as
//(select channel_id,
//        (SELECT STRING_AGG(U.author_text, ',,,') as author_text
//        FROM
//        (
//        SELECT DISTINCT T.author_text
//        FROM comments AS T
//        WHERE T.channel_id = S.channel_id
//        ) AS U)
//        -- STRING_AGG(author_text,';')
//
//        from comments as S group by (channel_id))

//select * from authors where author_text like '%,,,%'

