package com.anynote.note.mapper;

import com.anynote.note.api.model.po.Doc;
import com.anynote.note.model.bo.DocQueryParam;
import com.anynote.note.model.vo.DocListVO;
import com.anynote.note.model.vo.DocVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文档 Mapper
 * @author 称霸幼儿园
 */
@Mapper
public interface DocMapper extends BaseMapper<Doc> {

    public List<DocListVO> selectDocList(DocQueryParam docQueryParam);

    public DocVO selectDocById(@Param("docId") Long docId);

}
