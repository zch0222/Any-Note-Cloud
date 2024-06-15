package com.anynote.note.api;

import com.anynote.core.constant.ServiceNameConstants;
import com.anynote.core.utils.ResUtil;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.note.api.factory.RemoteNoteTaskFallbackFactory;
import com.anynote.note.api.model.po.UserNoteTask;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(contextId = "remoteNoteTaskService", value =
        ServiceNameConstants.NOTE_SERVICE, fallbackFactory = RemoteNoteTaskFallbackFactory.class)
public interface RemoteNoteTaskService {

    @GetMapping("noteTasks/inner/taskUsers/{taskId}")
    public ResData<List<UserNoteTask>> getTaskUsers(@PathVariable("taskId") Long taskId,
                                                    @RequestHeader("from-source") String fromSource);
}
