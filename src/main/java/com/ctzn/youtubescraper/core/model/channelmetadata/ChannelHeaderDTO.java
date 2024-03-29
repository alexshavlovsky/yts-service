package com.ctzn.youtubescraper.core.model.channelmetadata;

import com.ctzn.youtubescraper.core.model.commons.NavigationEndpoint;
import com.ctzn.youtubescraper.core.model.commons.SimpleText;
import com.ctzn.youtubescraper.core.model.commons.Thumbnails;
import com.ctzn.youtubescraper.core.parser.ParserUtil;
import lombok.Value;

@Value
public class ChannelHeaderDTO {

    public String channelId;
    public String title;
    public NavigationEndpoint navigationEndpoint;
    public Thumbnails avatar;
    public Thumbnails banner;
    public SimpleText subscriberCountText;
    public Thumbnails tvBanner;
    public Thumbnails mobileBanner;

    public long getSubscriberCount() {
        return subscriberCountText == null ? 0 : ParserUtil.parseSubCount(subscriberCountText.toString());
    }

}
