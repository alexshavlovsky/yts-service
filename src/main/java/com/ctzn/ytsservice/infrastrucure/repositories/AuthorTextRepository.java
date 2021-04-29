package com.ctzn.ytsservice.infrastrucure.repositories;

import com.ctzn.ytsservice.domain.entities.AuthorTextEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorTextRepository extends JpaRepository<AuthorTextEntity, Long> {

    Optional<AuthorTextEntity> findByText(String text);

    @Modifying
    @Query(value = "delete from author_texts where id in (select atx.id from author_texts as atx LEFT JOIN comments as c on atx.id = c.author_text_id where c.author_text_id is null)",
            nativeQuery = true)
    void deleteOrphans();

}
