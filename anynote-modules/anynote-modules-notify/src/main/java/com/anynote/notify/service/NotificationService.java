package com.anynote.notify.service;

import com.anynote.core.web.model.bo.ResData;
import com.anynote.notify.api.model.po.Notice;
import com.anynote.notify.model.dto.NoticeDTO;
//import org.apache.rocketmq.client.apis.ClientException;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;


public interface NotificationService {

    public Flux<ServerSentEvent<String>> notice(NoticeDTO noticeDTO, String accessToken);

    public void publishNotice(Notice notice);

}
