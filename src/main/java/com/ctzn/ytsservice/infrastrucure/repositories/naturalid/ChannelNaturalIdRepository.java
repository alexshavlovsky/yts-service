package com.ctzn.ytsservice.infrastrucure.repositories.naturalid;

import com.ctzn.ytsservice.domain.entities.ChannelNaturalId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelNaturalIdRepository extends CrudRepository<ChannelNaturalId, Long> {

    @Modifying
    @Query(value = "delete from channel_ids where id in (select chan.id from channel_ids as chan LEFT JOIN channels as c on chan.id = c.channel_id where c.channel_id is null)",
            nativeQuery = true)
    void deleteOrphans();

}
