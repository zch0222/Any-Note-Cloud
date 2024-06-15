package com.anynote.note.api.factory;

import com.anynote.core.exception.BusinessException;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.note.api.RemoteNoteTaskService;
import com.anynote.note.api.model.po.UserNoteTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RemoteNoteTaskFallbackFactory implements FallbackFactory<RemoteNoteTaskService> {

    @Override
    public RemoteNoteTaskService create(Throwable cause) {
        return new RemoteNoteTaskService() {

            @Override
            public ResData<List<UserNoteTask>> getTaskUsers(Long taskId, String fromSource) {
                throw new BusinessException("远程获取任务用户列表失败");
            }
        };
    }
}
