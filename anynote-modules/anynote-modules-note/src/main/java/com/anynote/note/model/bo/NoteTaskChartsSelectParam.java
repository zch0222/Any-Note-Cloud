package com.anynote.note.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteTaskChartsSelectParam {

    private Long noteTaskId;

    private Date startTime;

    private Date endTime;

}
