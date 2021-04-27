package com.ctzn.ytsservice.infrastrucure.repositories;

import com.ctzn.ytsservice.domain.entities.ChannelNaturalId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelNaturalIdRepository extends JpaRepository<ChannelNaturalId, Long> {

    @Modifying
    @Query(value = "delete from channel_ids where id in (select chan.id from channel_ids as chan LEFT JOIN channels as c on chan.id = c.channel_id where c.channel_id is null)",
            nativeQuery = true)
    void deleteOrphans();

}
