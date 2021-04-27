package com.ctzn.ytsservice.infrastrucure.repositories;

import com.ctzn.ytsservice.domain.entities.VideoNaturalId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoNaturalIdRepository extends JpaRepository<VideoNaturalId, Long> {

    @Modifying
    @Query(value = "delete from video_ids where id in (select vid.id from video_ids as vid LEFT JOIN videos as v on vid.id = v.video_id where v.video_id is null)",
            nativeQuery = true)
    void deleteOrphans();

}
