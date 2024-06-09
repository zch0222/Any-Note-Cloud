package com.anynote.notify.service.impl;

import com.anynote.notify.api.model.po.Notice;
import com.anynote.notify.mapper.KnowledgeBaseNoticeMapper;
import com.anynote.notify.mapper.NoticeMapper;
import com.anynote.notify.mapper.UserNoticeMapper;
import com.anynote.notify.service.NoticeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Resource
    private UserNoticeMapper userNoticeMapper;

    @Resource
    private KnowledgeBaseNoticeMapper knowledgeBaseNoticeMapper;


    @Override
    public Long publishNotice(Notice notice) {

        return 0L;
    }
}
