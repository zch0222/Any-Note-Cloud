package com.anynote.system.api.factory;

import com.anynote.core.web.enums.ResCode;
import com.anynote.core.web.model.bo.CreateResEntity;
import com.anynote.core.web.model.bo.PageBean;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.system.api.RemoteUserService;
import com.anynote.system.api.model.bo.KnowledgeBaseImportUser;
import com.anynote.system.api.model.bo.LoginUser;
import com.anynote.system.api.model.dto.CreateUserDTO;
import com.anynote.system.api.model.dto.KnowledgeBaseUserImportDTO;
import com.anynote.system.api.model.po.SysUser;
import com.anynote.system.api.model.vo.KnowledgeBaseUserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户服务降级
 * @author 称霸幼儿园
 */
@Component
@Slf4j
public class RemoteUserFallbackFactory implements FallbackFactory<RemoteUserService> {

    @Override
    public RemoteUserService create(Throwable throwable) {
        log.error("用户服务调用失败：{}", throwable.getMessage());
        return new RemoteUserService() {
            @Override
            public ResData<LoginUser> getUserInfo(String username) {
                return ResData.error(ResCode.INNER_SYSTEM_SERVICE_ERROR);
            }

            @Override
            public ResData<KnowledgeBaseUserImportDTO> importKnowledgeBaseUser(KnowledgeBaseUserImportDTO knowledgeBaseUserImportDTO) {
                return ResData.error(ResCode.INNER_SYSTEM_SERVICE_ERROR);
            }

            @Override
            public ResData<PageBean<KnowledgeBaseUserVO>> getKnowledgeBaseUsers(Long knowledgeBaseId, Integer page, Integer pageSize, String username) {
                return ResData.error(ResCode.INNER_SYSTEM_SERVICE_ERROR);
            }

            @Override
            public ResData<SysUser> getSysUserById(Long userId) {
                return ResData.error(ResCode.INNER_SYSTEM_SERVICE_ERROR);
            }

            @Override
            public ResData<Integer> updateSysUser(Long userId, SysUser sysUser) {
                return ResData.error(ResCode.INNER_SYSTEM_SERVICE_ERROR);
            }

            @Override
            public ResData<SysUser> getSysUserInfoById(Long userId) {
                return ResData.error(ResCode.INNER_SYSTEM_SERVICE_ERROR);
            }

            @Override
            public ResData<PageBean<SysUser>> getManageUserList(Integer page, Integer pageSize, String username) {
                return ResData.error(ResCode.INNER_SYSTEM_SERVICE_ERROR);
            }

            @Override
            public ResData<CreateResEntity> createUser(CreateUserDTO createUserDTO) {
                return ResData.error(ResCode.INNER_SYSTEM_SERVICE_ERROR);
            }
        };
    }
}
