package com.ctzn.ytsservice.domain.scraper.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncChannelRunner {

    private final ChannelRunnerFactory commentRunnerFactory;

    public AsyncChannelRunner(ChannelRunnerFactory commentRunnerFactory) {
        this.commentRunnerFactory = commentRunnerFactory;
    }

    @Async
    public void newTask(String channelId) {
        try {
            commentRunnerFactory.newRunner(channelId).call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
