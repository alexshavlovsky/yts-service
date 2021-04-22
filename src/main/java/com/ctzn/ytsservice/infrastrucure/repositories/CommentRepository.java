package com.ctzn.ytsservice.infrastrucure.repositories;

import com.ctzn.ytsservice.domain.entities.CommentEntity;
import com.ctzn.ytsservice.interfaces.rest.dto.UserProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface CommentRepository extends PagingAndSortingRepository<CommentEntity, String> {

    Page<CommentEntity> findAllByTextContainingIgnoreCase(String text, Pageable pageable);

    Page<CommentEntity> nativeFts(String query, Pageable pageable);

    @Query(value = "SELECT count(1) from comments where video_id = ?1", nativeQuery = true)
    Long countComments(String videoId);

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
            "GROUP BY (video_id, channel_id)\n" +
            ")\n" +
            "GROUP BY userId\n"
            , countQuery = "select count(1) from (select distinct channel_id from comments)"
            , nativeQuery = true)
    Page<UserProjection> getUsers(Pageable pageable);

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
