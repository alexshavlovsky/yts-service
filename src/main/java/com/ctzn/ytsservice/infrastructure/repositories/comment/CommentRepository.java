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
            "with posters as (\n" +
            "    select repac.channel_id,\n" +
            "           vids.video_id,\n" +
            "           max(rep.AUTHOR_TEXT_ID) AUTHOR_TEXT_ID\n" +
            "    from comments com,\n" +
            "         comment_ids comids,\n" +
            "         author_channels comac,\n" +
            "         comments rep,\n" +
            "         comment_ids repids,\n" +
            "         author_channels repac,\n" +
            "         video_ids vids\n" +
            "    where com.comment_id = comids.id\n" +
            "      and rep.comment_id = repids.id\n" +
            "      and comids.thread_id = repids.thread_id\n" +
            "      and comids.reply_id is null\n" +
            "      and repids.reply_id is not null\n" +
            "      and com.author_channel_id = comac.id\n" +
            "      and rep.author_channel_id = repac.id\n" +
            "      and com.video_id = vids.id\n" +
            "      and comac.channel_id = ?1\n" +
            "      and repac.channel_id != comac.channel_id\n" +
            "    group by repac.channel_id, vids.video_id),\n" +
            "     recipients as (\n" +
            "         select comac.channel_id,\n" +
            "                vids.video_id,\n" +
            "                max(com.AUTHOR_TEXT_ID) AUTHOR_TEXT_ID\n" +
            "         from comments com,\n" +
            "              comment_ids comids,\n" +
            "              author_channels comac,\n" +
            "              comments rep,\n" +
            "              comment_ids repids,\n" +
            "              author_channels repac,\n" +
            "              video_ids vids\n" +
            "         where com.comment_id = comids.id\n" +
            "           and rep.comment_id = repids.id\n" +
            "           and comids.thread_id = repids.thread_id\n" +
            "           and comids.reply_id is null\n" +
            "           and repids.reply_id is not null\n" +
            "           and com.author_channel_id = comac.id\n" +
            "           and rep.author_channel_id = repac.id\n" +
            "           and com.video_id = vids.id\n" +
            "           and repac.channel_id = ?1\n" +
            "           and repac.channel_id != comac.channel_id\n" +
            "         group by comac.channel_id, vids.video_id),\n" +
            "     same_thread as (\n" +
            "         select distinct comac.CHANNEL_ID,\n" +
            "                         vids.video_id,\n" +
            "                         max(com.AUTHOR_TEXT_ID) AUTHOR_TEXT_ID\n" +
            "         from comments com,\n" +
            "              comment_ids comids,\n" +
            "              author_channels comac,\n" +
            "              video_ids vids\n" +
            "         where com.comment_id = comids.id\n" +
            "           and comids.reply_id is null\n" +
            "           and com.author_channel_id = comac.id\n" +
            "           and com.video_id = vids.id\n" +
            "           and comac.CHANNEL_ID != ?1\n" +
            "           and com.VIDEO_ID in (select distinct com.video_id\n" +
            "                                from comments com,\n" +
            "                                     comment_ids comids,\n" +
            "                                     author_channels comac\n" +
            "                                where com.comment_id = comids.id\n" +
            "                                  and comids.reply_id is null\n" +
            "                                  and com.author_channel_id = comac.id\n" +
            "                                  and comac.channel_id = ?1)\n" +
            "         group by (comac.CHANNEL_ID, vids.video_id)\n" +
            "     ),\n" +
            "     all_union as (\n" +
            "         select *, 1 rp, 0 rr, 0 st\n" +
            "         from posters\n" +
            "         union\n" +
            "         select *, 0 rp, 1 rr, 0 st\n" +
            "         from recipients\n" +
            "         union\n" +
            "         select *, 0 rp, 0 rr, 1 st\n" +
            "         from same_thread),\n" +
            "     agg_no_author_texts as (\n" +
            "         select CHANNEL_ID                                                       authorchannelid,\n" +
            "                max(AUTHOR_TEXT_ID)                                              AUTHOR_TEXT_ID,\n" +
            "                array_agg(distinct case when rp = 1 then VIDEO_ID else null end) reppostervideos,\n" +
            "                array_agg(distinct case when rr = 1 then VIDEO_ID else null end) reprecipientvideos,\n" +
            "                array_agg(distinct case when st = 1 then VIDEO_ID else null end) samethreadpostervideos\n" +
            "         from all_union au\n" +
            "         group by CHANNEL_ID\n" +
            "         order by bit_or(rp) + bit_or(rr) + bit_or(st) desc, sum(rp + rr + st) desc\n" +
            "         limit 10\n" +
            "     )\n" +
            "select authorchannelid,\n" +
            "       at.TEXT as authortext,\n" +
            "       reppostervideos,\n" +
            "       reprecipientvideos,\n" +
            "       samethreadpostervideos\n" +
            "from agg_no_author_texts ant\n" +
            "         join AUTHOR_TEXTS at on ant.AUTHOR_TEXT_ID = at.ID"
            , nativeQuery = true)
    List<UserCommonCommentedVideosProjection> getTop10CommonCommentedVideos(String userId);

}


// get comment intersection from user to user on video

//select comids.THREAD_ID COMMENT_YT_ID,
//       com.COMMENT_ID,
//       array_agg(distinct comids.THREAD_ID||'.'||repids.reply_id) REP_YT_IDS,
//       array_agg(distinct rep.COMMENT_ID) REP_IDS
//from comments com,
//     comment_ids comids,
//     author_channels comac,
//     comments rep,
//     comment_ids repids,
//     author_channels repac,
//     video_ids vids
//where com.comment_id = comids.id
//  and rep.comment_id = repids.id
//  and comids.thread_id = repids.thread_id
//  and comids.reply_id is null
//  and repids.reply_id is not null
//  and com.author_channel_id = comac.id
//  and rep.author_channel_id = repac.id
//  and com.video_id = vids.id
//  -- to user
//  and comac.channel_id = 'xxxxx'
//  -- from user
//  and repac.channel_id = 'xxxx'
//  -- on video
//  and vids.video_id = 'xxxx'
//group by comac.channel_id, repac.channel_id, vids.video_id, comids.THREAD_ID, com.COMMENT_ID
