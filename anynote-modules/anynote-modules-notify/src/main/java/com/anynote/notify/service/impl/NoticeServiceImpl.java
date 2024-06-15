package com.anynote.notify.service.impl;

import com.anynote.common.redis.constant.RedisChannel;
import com.anynote.common.redis.model.bo.RedisMessage;
import com.anynote.common.redis.service.RedisService;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.utils.RemoteResDataUtil;
import com.anynote.core.utils.StringUtils;
import com.anynote.note.api.RemoteKnowledgeBaseService;
import com.anynote.note.api.model.dto.NoteKnowledgeBaseDTO;
import com.anynote.note.api.model.po.NoteKnowledgeBase;
import com.anynote.notify.api.enmus.NoticeType;
import com.anynote.notify.api.model.bo.NoticePublishParam;
import com.anynote.notify.api.model.po.KnowledgeBaseNotice;
import com.anynote.notify.api.model.po.Notice;
import com.anynote.notify.api.model.po.UserNotice;
import com.anynote.notify.mapper.KnowledgeBaseNoticeMapper;
import com.anynote.notify.mapper.NoticeMapper;
import com.anynote.notify.mapper.UserNoticeMapper;
import com.anynote.notify.model.vo.NoticeVO;
import com.anynote.notify.service.NoticeService;
import com.anynote.notify.service.UserNoticeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {


    @Resource
    private UserNoticeService userNoticeService;

    @Resource
    private KnowledgeBaseNoticeMapper knowledgeBaseNoticeMapper;

//    @Resource
//    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisService redisService;

    @Resource
    private Gson gson;

    @Resource
    private RemoteKnowledgeBaseService remoteKnowledgeBaseService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long publishNotice(NoticePublishParam noticePublishParam) {
        Notice notice = Notice.builder()
                .title(noticePublishParam.getTitle())
                .content(noticePublishParam.getContent())
                .type(noticePublishParam.getType())
                .status(noticePublishParam.getStatus())
                .level(noticePublishParam.getLevel())
                .deleted(0)
                .createBy(noticePublishParam.getCreateBy())
                .createTime(noticePublishParam.getCreateTime())
                .updateBy(noticePublishParam.getUpdateBy())
                .updateTime(noticePublishParam.getUpdateTime())
                .build();
        this.saveNotice(notice);

        NoteKnowledgeBaseDTO noteKnowledgeBaseDTO = null;
        if (NoticeType.KNOWLEDGE_BASE.getType() == noticePublishParam.getType()) {
            knowledgeBaseNoticeMapper.insert(KnowledgeBaseNotice.builder()
                            .noticeId(notice.getId())
                            .knowledgeBaseId(noticePublishParam.getKnowledgeBaseId())
                    .build());
            noteKnowledgeBaseDTO = RemoteResDataUtil.getResData(remoteKnowledgeBaseService.innerGetKnowledgeBaseById(noticePublishParam.getKnowledgeBaseId(),
                    "inner"), "获取知识库信息失败");
        }
        List<UserNotice> userNoticeList = new ArrayList<>(noticePublishParam.getUserIdList().size());
        List<RedisMessage> messageList = new ArrayList<>(noticePublishParam.getUserIdList().size());

        String noticeString = gson.toJson(NoticeVO.builder()
                        .id(notice.getId())
                        .title(notice.getTitle())
                        .content(notice.getContent())
                        .type(notice.getType())
                        .status(notice.getStatus())
                        .level(notice.getLevel())
                        .createTime(notice.getCreateTime())
                        .createBy(notice.getCreateBy())
                        .updateTime(notice.getUpdateTime())
                        .updateBy(notice.getUpdateBy())
                        .knowledgeBaseId(noticePublishParam.getKnowledgeBaseId())
                        .knowledgeName(StringUtils.isNotNull(noteKnowledgeBaseDTO) ? noteKnowledgeBaseDTO.getKnowledgeBaseName() : null)
                .build());
        log.info(noticeString);
        for (Long userId : noticePublishParam.getUserIdList()) {
            userNoticeList.add(UserNotice.builder()
                            .noticeId(notice.getId())
                            .userId(userId)
                    .build());
            messageList.add(RedisMessage.builder()
                            .channel(RedisChannel.NOTIFY_CHANNEL_USER + userId)
                            .message(noticeString)
                    .build());
        }
        userNoticeService.saveBatch(userNoticeList);
        // 发送消息
        redisService.batchPublish(messageList);
        return notice.getId();
    }

    @Override
    public int saveNotice(Notice notice) {
        return this.baseMapper.insert(notice);
    }
}
