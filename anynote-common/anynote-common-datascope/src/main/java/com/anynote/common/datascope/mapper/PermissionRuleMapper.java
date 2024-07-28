package com.anynote.common.datascope.mapper;

import com.anynote.common.datascope.model.bo.EntityPermissionOneToOneQueryParam;
import com.anynote.common.datascope.model.bo.EntityPermissionQueryParam;
import com.anynote.common.datascope.model.po.EntityPermissionPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PermissionRuleMapper {

    public EntityPermissionPO selectEntityPermissionOneToOne(EntityPermissionQueryParam queryParam);

    public EntityPermissionPO selectEntityPermissionNToM(EntityPermissionQueryParam queryParam);

}
