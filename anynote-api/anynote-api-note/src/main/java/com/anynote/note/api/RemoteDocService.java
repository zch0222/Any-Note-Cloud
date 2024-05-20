package com.anynote.note.api;

import com.anynote.core.constant.ServiceNameConstants;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.note.api.factory.RemoteDocFallbackFactory;
import com.anynote.note.api.model.po.Doc;
import com.anynote.note.api.model.vo.DocVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.constraints.NotNull;

@FeignClient(contextId = "remoteDocController",
        value = ServiceNameConstants.NOTE_SERVICE, fallbackFactory = RemoteDocFallbackFactory.class)
public interface RemoteDocService {

    @GetMapping("/docs/inner/{id}")
    public ResData<Doc> selectDocById(@Validated @PathVariable("id") @NotNull(message = "文档ID不能为空") Long id);

    @GetMapping("/docs/{id}")
    public ResData<DocVO> getDoc(@Validated @PathVariable("id") @NotNull(message = "文档ID不能为空") Long id);

    @GetMapping("/docs/public/{id}")
    public ResData<DocVO> getPublicDoc(@Validated @PathVariable("id") @NotNull(message = "文档ID不能为空") Long id);

}
