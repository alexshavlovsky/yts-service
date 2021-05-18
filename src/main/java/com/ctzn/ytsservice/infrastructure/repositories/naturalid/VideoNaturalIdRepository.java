package com.ctzn.ytsservice.infrastructure.repositories.naturalid;

import com.ctzn.ytsservice.domain.entities.VideoNaturalId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoNaturalIdRepository extends CrudRepository<VideoNaturalId, Long> {

    @Modifying
    @Query(value = "delete from video_ids where id in (select vid.id from video_ids as vid LEFT JOIN videos as v on vid.id = v.video_id where v.video_id is null)",
            nativeQuery = true)
    void deleteOrphans();

}
