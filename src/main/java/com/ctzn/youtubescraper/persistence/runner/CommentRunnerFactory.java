package com.ctzn.youtubescraper.persistence.runner;


import com.ctzn.youtubescraper.config.CommentIteratorCfg;
import com.ctzn.youtubescraper.config.CommentOrderCfg;
import com.ctzn.youtubescraper.handler.DataHandler;
import com.ctzn.youtubescraper.iterator.comment.CommentVisitor;
import com.ctzn.youtubescraper.iterator.comment.IterableCommentContextBuilder;
import com.ctzn.youtubescraper.model.comments.CommentDTO;

public class CommentRunnerFactory {

    public static Runnable newInstance(String videoId, DataHandler<CommentDTO> handler, CommentOrderCfg commentOrderCfg, CommentIteratorCfg commentIteratorCfg) {
        return new CommentRunner(
                new IterableCommentContextBuilder(videoId, commentOrderCfg),
                new CommentVisitor(handler, commentIteratorCfg)
        );
    }

}
