package com.ctzn.ytsservice.infrastrucure.repositories.naturalid;

import com.ctzn.ytsservice.domain.entities.CommentNaturalId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentNaturalIdRepository extends JpaRepository<CommentNaturalId, Long> {

    @Modifying
    @Query(value = "delete from comment_ids where id in (select com.id from comment_ids as com LEFT JOIN comments as c on com.id = c.comment_id where c.comment_id is null)",
            nativeQuery = true)
    void deleteOrphans();

}
