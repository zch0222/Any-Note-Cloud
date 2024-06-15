package com.anynote.notify.service.impl;

import com.anynote.notify.api.model.po.UserNotice;
import com.anynote.notify.mapper.UserNoticeMapper;
import com.anynote.notify.service.UserNoticeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserNoticeServiceImpl extends ServiceImpl<UserNoticeMapper, UserNotice>
        implements UserNoticeService {
}
