package com.anynote.notify.service;

import com.anynote.notify.api.model.po.Notice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;

public interface NoticeService extends IService<Notice> {

    public Long publishNotice(Notice notice);

}
