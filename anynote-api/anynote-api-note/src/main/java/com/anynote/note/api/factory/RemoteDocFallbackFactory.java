package com.anynote.note.api.factory;

import com.anynote.core.exception.BusinessException;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.note.api.RemoteDocService;
import com.anynote.note.api.model.po.Doc;
import com.anynote.note.api.model.vo.DocVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteDocFallbackFactory implements FallbackFactory<RemoteDocService> {

    @Override
    public RemoteDocService create(Throwable cause) {
        log.error("doc服务调用失败: {}", cause.getMessage());
        return new RemoteDocService() {

            @Override
            public ResData<Doc> selectDocById(Long id) {
                throw new BusinessException("远程获取doc失败");
            }

            @Override
            public ResData<DocVO> getDoc(Long id) {
                throw new BusinessException("获取文档信息失败");
            }


            @Override
            public ResData<DocVO> getPublicDoc(Long id) {
                throw new BusinessException("获取文档信息失败");
            }
        };
    }
}
