package com.anynote.note.model.vo;

import com.anynote.note.model.po.NoteTaskChartsPO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoteTaskChartsVO {

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 统计次数列表
     */
    private List<NoteTaskChartsPO> chartsPOList;


}
