package com.ctzn.ytsservice.interfaces.rest.transform;

import com.ctzn.ytsservice.interfaces.rest.dto.ReadableResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ResponseFormatter {

    public ResponseEntity<ReadableResponse> getResponse(String entityId, String messagePattern, Object... objects) {
        String message = MessageFormatter.arrayFormat(messagePattern, objects).getMessage();
        log.info(message);
        return ResponseEntity.ok(new ReadableResponse(entityId, "OK", message));
    }

}
