package com.ctzn.youtubescraper.core.http;

import com.ctzn.youtubescraper.core.config.CommentIteratorCfg;
import com.ctzn.youtubescraper.core.config.CommentOrderCfg;
import com.ctzn.youtubescraper.core.persistence.CommentRunnerFactory;
import com.ctzn.youtubescraper.core.persistence.dto.CommentDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
class GenericYoutubeClientTest {

    @Test
    void testNextApi() {
        String videoId = "dHqPuZs_nPk";
        List<CommentDTO> acc = Collections.synchronizedList(new ArrayList<>());
        Runnable runner = CommentRunnerFactory.newInstance(
                videoId,
                acc::addAll,
                CommentOrderCfg.NEWEST_FIRST,
                CommentIteratorCfg.newInstance(100, 10)
        );
        runner.run();
        assertTrue(acc.size() >= 100);
    }

}
