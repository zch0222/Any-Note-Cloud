package com.anynote.notify.service;

import com.anynote.notify.api.model.bo.NoticePublishParam;
import com.anynote.notify.api.model.po.Notice;
import com.anynote.notify.model.vo.NoticeVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.aspectj.weaver.ast.Not;

public interface NoticeService extends IService<Notice> {

    public Long publishNotice(NoticePublishParam noticePublishParam);

    public int saveNotice(Notice notice);

}
