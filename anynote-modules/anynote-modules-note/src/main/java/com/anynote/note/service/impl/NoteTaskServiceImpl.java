package com.anynote.note.service.impl;

import com.alibaba.fastjson2.JSON;
import com.anynote.common.rocketmq.callback.RocketmqSendCallbackBuilder;
import com.anynote.common.rocketmq.properties.RocketMQProperties;
import com.anynote.common.rocketmq.tags.NoteTagsEnum;
import com.anynote.common.rocketmq.tags.NoteTaskTagsEnum;
import com.anynote.common.security.token.TokenUtil;
import com.anynote.core.constant.Constants;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.exception.user.UserParamException;
import com.anynote.core.utils.DateUtils;
import com.anynote.core.utils.StringUtils;
import com.anynote.core.web.enums.ResCode;
import com.anynote.core.web.model.bo.PageBean;
import com.anynote.note.api.model.bo.NoteOperationCount;
import com.anynote.note.api.model.bo.NoteTaskCreatedMessageBody;
import com.anynote.note.api.model.po.*;
import com.anynote.note.datascope.annotation.RequiresKnowledgeBasePermissions;
import com.anynote.note.datascope.annotation.RequiresNotePermissions;
import com.anynote.note.datascope.annotation.RequiresNoteTaskPermissions;
import com.anynote.note.enums.*;
import com.anynote.note.mapper.NoteTaskOperationHistoryMapper;
import com.anynote.note.mapper.NoteTaskMapper;
import com.anynote.note.mapper.UserNoteTaskMapper;
import com.anynote.note.model.bo.*;
import com.anynote.note.model.dto.AdminNoteTaskDTO;
import com.anynote.note.model.dto.MemberNoteTaskDTO;
import com.anynote.note.model.po.NoteTaskAnalyzePO;
import com.anynote.note.model.po.NoteTaskChartsPO;
import com.anynote.note.model.po.NoteTaskSubmissionTimePO;
import com.anynote.note.model.vo.NoteTaskChartsVO;
import com.anynote.note.model.vo.NoteTaskHistoryVO;
import com.anynote.note.model.vo.NoteTaskUserAnalyzeVO;
import com.anynote.note.service.*;
import com.anynote.note.validate.annotation.PageValid;
import com.anynote.system.api.model.bo.LoginUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 称霸幼儿园
 */
@Service
@Slf4j
public class NoteTaskServiceImpl extends ServiceImpl<NoteTaskMapper, NoteTask>
        implements NoteTaskService {


    @Autowired
    private NoteTaskSubmissionRecordService noteTaskSubmissionRecordService;

    @Autowired
    private UserNoteTaskMapper userNoteTaskMapper;

    @Autowired
    private NoteTaskOperationHistoryMapper noteTaskOperationHistoryMapper;

    @Autowired
    private NoteService noteService;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    @Autowired
    private ThreadPoolTaskExecutor asyncExecutor;

    @Resource
    private NoteHistoryService noteHistoryService;

    @Autowired
    private NoteTaskMapper noteTaskMapper;

    @Autowired
    private RocketMQProperties rocketMQProperties;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private NoteTaskOperationHistoryService noteTaskOperationHistoryService;

    @Resource
    private NoteOperationLogService noteOperationLogService;

    /**
     * 更新笔记任务
     *
     * @param updateParam
     * @return
     */
    @RequiresNoteTaskPermissions(NoteTaskPermissions.MANAGE)
    @Override
    public String updateNoteTask(NoteTaskUpdateParam updateParam) {
        LoginUser loginUser = tokenUtil.getLoginUser();
        Date date = new Date();
        NoteTask noteTask = NoteTask.builder()
                .id(updateParam.getNoteTaskId())
                .taskName(updateParam.getTaskName())
                .startTime(updateParam.getStartTime())
                .endTime(updateParam.getEndTime())
                .taskDescribe(updateParam.getTaskDescribe())
                .build();
        noteTask.setUpdateBy(loginUser.getSysUser().getId());
        noteTask.setUpdateTime(date);
        int count = this.baseMapper.updateById(noteTask);
        if (1 != count) {
            throw new BusinessException("未知错误，请联系管理员");
        }

        NoteTaskOperationHistory noteTaskOperationHistory = NoteTaskOperationHistory.builder()
                .noteTaskId(noteTask.getId())
                .type(NoteTaskOperationType.EDIT.getValue())
                .operatorId(loginUser.getSysUser().getId())
                .operationTime(date)
                .noteTaskUserId(loginUser.getSysUser().getId())
                .createBy(loginUser.getSysUser().getId())
                .createTime(date)
                .updateBy(loginUser.getSysUser().getId())
                .updateTime(date)
                .build();
        String destination = rocketMQProperties.getNoteTaskTopic() + ":" + NoteTaskTagsEnum.INSERT_HISTORY.name();
        rocketMQTemplate.asyncSend(destination, JSON.toJSON(noteTaskOperationHistory), RocketmqSendCallbackBuilder.commonCallback());

        return Constants.SUCCESS_RES;
    }


    /**
     * 获取用户对笔记任务的权限
     *
     * @param userId 用户ID
     * @param taskId 笔记ID
     * @return
     */
    @Override
    public NoteTaskPermissions getNoteTaskPermissions(Long userId, Long taskId) {
        LambdaQueryWrapper<UserNoteTask> userNoteTaskLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userNoteTaskLambdaQueryWrapper
                .eq(UserNoteTask::getUserId, userId)
                .eq(UserNoteTask::getNoteTaskId, taskId);
        UserNoteTask userNoteTask = userNoteTaskMapper.selectOne(userNoteTaskLambdaQueryWrapper);
        if (StringUtils.isNull(userNoteTask)) {
            return NoteTaskPermissions.NO;
        }
        if (1 == userNoteTask.getPermissions()) {
            return NoteTaskPermissions.MANAGE;
        } else if (2 == userNoteTask.getPermissions()) {
            return NoteTaskPermissions.SUBMIT;
        } else if (3 == userNoteTask.getPermissions()) {
            return NoteTaskPermissions.NO;
        }
        return NoteTaskPermissions.NO;
    }

    /**
     * 为知识库创建笔记任务
     *
     * @param taskCreateParam
     * @return
     */
    @RequiresKnowledgeBasePermissions(value = KnowledgeBasePermissions.MANAGE,
            message = "没有权限创建任务")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long createNoteTask(NoteTaskCreateParam taskCreateParam) {
        LoginUser loginUser = tokenUtil.getLoginUser();
        Date date = new Date();
        NoteTask noteTask = NoteTask.builder()
                .taskName(taskCreateParam.getTaskName())
                .startTime(taskCreateParam.getStartTime())
                .endTime(taskCreateParam.getEndTime())
                .knowledgeBaseId(taskCreateParam.getId())
                .status(0)
                .deleted(0)
                .taskDescribe(taskCreateParam.getTaskDescribe())
                .build();
        noteTask.setCreateBy(loginUser.getSysUser().getId());
        noteTask.setUpdateBy(loginUser.getSysUser().getId());
        noteTask.setCreateTime(date);
        noteTask.setUpdateTime(date);
        this.baseMapper.insert(noteTask);

        NoteTaskOperationHistory noteTaskOperationHistory = NoteTaskOperationHistory.builder()
                .noteTaskId(noteTask.getId())
                .type(NoteTaskOperationType.CREATE.getValue())
                .operatorId(loginUser.getSysUser().getId())
                .operationTime(date)
                .noteTaskUserId(loginUser.getSysUser().getId())
                .deleted(0)
                .createBy(loginUser.getSysUser().getId())
                .createTime(date)
                .updateBy(loginUser.getSysUser().getId())
                .updateTime(date)
                .build();
        // 插入创建笔记操作记录
        String destination = rocketMQProperties.getNoteTaskTopic() + ":" + NoteTaskTagsEnum.INSERT_HISTORY.name();
        rocketMQTemplate.asyncSend(destination, JSON.toJSON(noteTaskOperationHistory), RocketmqSendCallbackBuilder.commonCallback());


        // 异步执行
        asyncExecutor.submit(() -> {
            // 获取知识库内所有非管理员用户的id
            List<Long> knowledgeBaseMemberIds = knowledgeBaseService.getAllMemberKnowledgeBaseUserId(taskCreateParam.getId());
            // 给非管理员用户添加编辑权限
            for (Long userId : knowledgeBaseMemberIds) {
                userNoteTaskMapper.insert(UserNoteTask.builder()
                        .userId(userId)
                        .noteTaskId(noteTask.getId())
                        .permissions(NoteTaskPermissions.SUBMIT.getValue())
                        // 状态设置为未提交
                        .status(UserNoteTaskStatus.NOT_SUBMITTED.getValue())
                        .build());

                // 添加用户到任务操作日志
                NoteTaskOperationHistory addUserNoteTaskOperationHistory = NoteTaskOperationHistory.builder()
                        .noteTaskId(noteTask.getId())
                        .type(NoteTaskOperationType.ADD_USER.getValue())
                        .operatorId(loginUser.getSysUser().getId())
                        .operationTime(date)
                        .noteTaskUserId(userId)
                        .deleted(0)
                        .createBy(loginUser.getSysUser().getId())
                        .createTime(date)
                        .updateBy(loginUser.getSysUser().getId())
                        .updateTime(date)
                        .build();
                noteTaskOperationHistoryService.asyncSaveNoteTaskOperationHistory(addUserNoteTaskOperationHistory);

            }

            List<Long> knowledgeBaseManagerIds = knowledgeBaseService.getAllKnowledgeBaseManagerId(taskCreateParam.getId());
            // 给管理员用户添加管理权限
            for (Long userId : knowledgeBaseManagerIds) {
                userNoteTaskMapper.insert(UserNoteTask.builder()
                        .userId(userId)
                        .noteTaskId(noteTask.getId())
                        .permissions(NoteTaskPermissions.MANAGE.getValue())
                        // 管理员不用提交任务
                        .status(UserNoteTaskStatus.NO_SUBMISSION_REQUIRED.getValue())
                        .build());

                // 添加用户到任务操作日志
                NoteTaskOperationHistory addUserNoteTaskOperationHistory = NoteTaskOperationHistory.builder()
                        .noteTaskId(noteTask.getId())
                        .type(NoteTaskOperationType.ADD_USER.getValue())
                        .operatorId(loginUser.getSysUser().getId())
                        .operationTime(date)
                        .noteTaskUserId(userId)
                        .deleted(0)
                        .createBy(loginUser.getSysUser().getId())
                        .createTime(date)
                        .updateBy(loginUser.getSysUser().getId())
                        .updateTime(date)
                        .build();
                noteTaskOperationHistoryService.asyncSaveNoteTaskOperationHistory(addUserNoteTaskOperationHistory);
            }
        });

        rocketMQTemplate.asyncSend(rocketMQProperties.getNoteTopic() + ":" + NoteTagsEnum.NOTE_TASK_CREATED.name(),
                new Gson().toJson(NoteTaskCreatedMessageBody.builder()
                        .noteTaskId(noteTask.getId())
                        .taskDescribe(noteTask.getTaskName())
                        .startTime(noteTask.getStartTime())
                        .endTime(noteTask.getEndTime())
                        .knowledgeBaseId(noteTask.getKnowledgeBaseId())
                        .status(noteTask.getStatus())
                        .taskDescribe(noteTask.getTaskDescribe())
                        .createBy(noteTask.getCreateBy())
                        .createTime(noteTask.getCreateTime())
                        .updateBy(noteTask.getUpdateBy())
                        .updateTime(noteTask.getUpdateTime()).build()), RocketmqSendCallbackBuilder.commonCallback());
        return noteTask.getId();
    }

    /**
     * 需要提交的人数
     *
     * @param queryParam
     * @return
     */
    @Override
    public Long getNoteTaskNeedSubmitCount(NoteTaskQueryParam queryParam) {
        LambdaQueryWrapper<UserNoteTask> userNoteTaskLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userNoteTaskLambdaQueryWrapper
                .eq(UserNoteTask::getNoteTaskId, queryParam.getNoteTaskId())
                .gt(UserNoteTask::getPermissions, NoteTaskPermissions.MANAGE.getValue());
        return userNoteTaskMapper.selectCount(userNoteTaskLambdaQueryWrapper);
    }

    /**
     * 提交任务
     *
     * @param submitParam
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @RequiresKnowledgeBasePermissions(value = KnowledgeBasePermissions.EDIT,
            message = "没有权限提交任务")
    @RequiresNotePermissions(NotePermissions.MANAGE)
    @Override
    public String submitNoteTask(NoteTaskSubmitParam submitParam) {
        LoginUser loginUser = tokenUtil.getLoginUser();

        LambdaQueryWrapper<UserNoteTask> userNoteTaskLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userNoteTaskLambdaQueryWrapper
                .eq(UserNoteTask::getUserId, loginUser.getSysUser().getId())
                .eq(UserNoteTask::getNoteTaskId, submitParam.getTaskId());
        UserNoteTask userNoteTask = userNoteTaskMapper.selectOne(userNoteTaskLambdaQueryWrapper);
        if (UserNoteTaskStatus.NO_SUBMISSION_REQUIRED.getValue() == userNoteTask.getStatus()) {
            throw new UserParamException("提交失败，您不用提交这个任务", ResCode.USER_REQUEST_PARAM_ERROR);
        }


        Date date = new Date();

        LambdaQueryWrapper<NoteTask> noteTaskLambdaQueryWrapper = new LambdaQueryWrapper<>();
        noteTaskLambdaQueryWrapper
                .eq(NoteTask::getId, submitParam.getTaskId())
                .select(NoteTask::getStartTime, NoteTask::getEndTime, NoteTask::getStatus, NoteTask::getKnowledgeBaseId);
        NoteTask noteTaskInfo = this.baseMapper.selectOne(noteTaskLambdaQueryWrapper);

        LambdaQueryWrapper<Note> noteLambdaQueryWrapper = new LambdaQueryWrapper<>();
        noteLambdaQueryWrapper
                .eq(Note::getId, submitParam.getId())
                .select(Note::getKnowledgeBaseId);
        Note noteInfo = noteService.getBaseMapper().selectOne(noteLambdaQueryWrapper);

        if (!noteInfo.getKnowledgeBaseId().equals(noteTaskInfo.getKnowledgeBaseId())) {
            throw new UserParamException("提交失败，笔记和任务不属于一个知识库", ResCode.USER_REQUEST_PARAM_ERROR);
        }

        if (StringUtils.isNull(noteTaskInfo)) {
            throw new UserParamException("提交失败，任务不存在", ResCode.USER_REQUEST_PARAM_ERROR);
        }
        if (date.getTime() > noteTaskInfo.getEndTime().getTime()) {
            // 创建一个异步任务
            if (0 == noteTaskInfo.getStatus()) {

            }
            throw new UserParamException("提交失败，任务已经结束", ResCode.USER_REQUEST_PARAM_ERROR);
        } else if (date.getTime() < noteTaskInfo.getStartTime().getTime()) {
            throw new UserParamException("提交失败，任务尚未开始", ResCode.USER_REQUEST_PARAM_ERROR);
        }

        LambdaQueryWrapper<NoteTaskSubmissionRecord> noteTaskSubmissionRecordLambdaQueryWrapper =
                new LambdaQueryWrapper<>();
        noteTaskSubmissionRecordLambdaQueryWrapper
                .eq(NoteTaskSubmissionRecord::getUserId, loginUser.getSysUser().getId())
                .eq(NoteTaskSubmissionRecord::getNoteTaskId, submitParam.getTaskId())
                .eq(NoteTaskSubmissionRecord::getStatus, NoteTaskSubmissionRecordStatus.NORMAL.getValue());

        Long recordCount = noteTaskSubmissionRecordService.getBaseMapper()
                .selectCount(noteTaskSubmissionRecordLambdaQueryWrapper);
        if (recordCount > 0) {
            throw new UserParamException("提交失败，你已经提交过该任务", ResCode.USER_REQUEST_PARAM_ERROR);
        }


        // 添加任务提交记录
        NoteTaskSubmissionRecord noteTaskSubmissionRecord = NoteTaskSubmissionRecord.builder()
                .noteTaskId(submitParam.getTaskId())
                .userId(loginUser.getSysUser().getId())
                .noteId(submitParam.getId())
                .noteHistoryId(noteHistoryService.getLatestNoteHistory(submitParam.getNoteId()).getId())
                .submitTime(new Date())
                .noteEditCount(noteOperationLogService.selectNoteEditCount(submitParam.getNoteId()))
                .status(NoteTaskSubmissionRecordStatus.NORMAL.getValue())
                .deleted(0)
                .build();
        noteTaskSubmissionRecord.setUpdateBy(loginUser.getSysUser().getId());
        noteTaskSubmissionRecord.setCreateBy(loginUser.getSysUser().getId());
        noteTaskSubmissionRecord.setUpdateTime(date);
        noteTaskSubmissionRecord.setCreateTime(date);
        noteTaskSubmissionRecordService.getBaseMapper().insert(noteTaskSubmissionRecord);

        // 提交笔记，修改笔记权限
        Integer count = noteService.submitNote(submitParam.getId());
        if (1 != count) {
            throw new BusinessException("提交失败，请联系管理员", ResCode.BUSINESS_ERROR);
        }
        // 修改用户任务提交状态
        userNoteTask.setStatus(UserNoteTaskStatus.SUBMITTED.getValue());
        userNoteTaskMapper.update(userNoteTask, userNoteTaskLambdaQueryWrapper);

        // 添加任务操作记录
        String destination = rocketMQProperties.getNoteTaskTopic() + ":" + NoteTaskTagsEnum.INSERT_HISTORY.name();
        rocketMQTemplate.asyncSend(destination, NoteTaskOperationHistory.builder()
                .noteTaskId(submitParam.getTaskId())
                .type(NoteTaskOperationType.SUBMIT.getValue())
                .operatorId(loginUser.getSysUser().getId())
                .operationTime(date)
                .noteTaskUserId(loginUser.getSysUser().getId())
                .noteTaskSubmissionRecordId(noteTaskSubmissionRecord.getId())
                .deleted(0)
                .updateTime(date)
                .updateBy(loginUser.getSysUser().getId())
                .createTime(date)
                .createBy(loginUser.getSysUser().getId())
                .build(), RocketmqSendCallbackBuilder.commonCallback());
        return Constants.SUCCESS_RES;
    }

    /**
     * 非管理员获取笔记任务信息
     *
     * @param queryParam
     * @return
     */
    @PageValid
    @RequiresKnowledgeBasePermissions(value = KnowledgeBasePermissions.READ, message = "无法获取笔记任务信息")
    @Override
    public PageBean<MemberNoteTaskDTO> getMemberNoteTasks(NoteTaskQueryParam queryParam) {
        LoginUser loginUser = tokenUtil.getLoginUser();
        queryParam.setSubmitUserId(loginUser.getSysUser().getId());
        PageHelper.startPage(queryParam.getPage(), queryParam.getPageSize(), "update_time desc");
        List<MemberNoteTaskDTO> memberNoteTaskDTOList = this.baseMapper.selectMemberNoteTaskList(queryParam);
        PageInfo<MemberNoteTaskDTO> pageInfo = new PageInfo<>(memberNoteTaskDTOList);


//        memberNoteTaskDTOList.stream().forEach(memberNoteTaskDTO -> {
////            if (StringUtils.isNotNull(memberNoteTaskDTO.getSubmissionNoteId())) {
////                memberNoteTaskDTO.setSubmissionStatus(0);
////            }
////            else {
////                memberNoteTaskDTO.setSubmissionStatus(1);
////            }
//            LambdaQueryWrapper<UserNoteTask> userNoteTaskLambdaQueryWrapper = new LambdaQueryWrapper<>();
//            userNoteTaskLambdaQueryWrapper
//                    .eq(UserNoteTask::getUserId, loginUser.getSysUser().getId())
//                    .eq(UserNoteTask::getNoteTaskId, memberNoteTaskDTO.getId());
//            UserNoteTask userNoteTask = userNoteTaskMapper.selectOne(userNoteTaskLambdaQueryWrapper);
//
//        });
        return PageBean.<MemberNoteTaskDTO>builder()
                .rows(memberNoteTaskDTOList)
                .pages(pageInfo.getPages())
                .total(pageInfo.getTotal())
                .build();
    }


//    @RequiresKnowledgeBasePermissions(value = KnowledgeBasePermissions.MANAGE, message = "权限不足")
//    @Override
//    public AdminNoteTaskDTO getAdminNoteTaskById(NoteTaskQueryParam queryParam) {
//        queryParam.setId(this.getNoteTaskKnowledgeBaseId(queryParam.getNoteTaskId()));
//        AdminNoteTaskDTO adminNoteTaskDTO =  selectAdminNoteTaskById(queryParam);
//        return adminNoteTaskDTO;
//    }


    /**
     * 获取笔记所属的知识库id
     *
     * @param noteTaskId
     * @return
     */
    @Override
    public Long getNoteTaskKnowledgeBaseId(Long noteTaskId) {
        LambdaQueryWrapper<NoteTask> noteTaskLambdaQueryWrapper = new LambdaQueryWrapper<>();
        noteTaskLambdaQueryWrapper
                .eq(NoteTask::getId, noteTaskId)
                .select(NoteTask::getKnowledgeBaseId);
        NoteTask noteTask = this.baseMapper.selectOne(noteTaskLambdaQueryWrapper);
        return noteTask.getKnowledgeBaseId();
    }

    /**
     * 管理员根据id获取笔记任务信息
     *
     * @param queryParam
     * @return
     */
    @Override
    @RequiresKnowledgeBasePermissions(value = KnowledgeBasePermissions.MANAGE, message = "权限不足")
    public AdminNoteTaskDTO getAdminNoteTaskById(NoteTaskQueryParam queryParam) {
        LambdaQueryWrapper<NoteTask> noteTaskLambdaQueryWrapper = new LambdaQueryWrapper<>();
        noteTaskLambdaQueryWrapper
                .eq(NoteTask::getId, queryParam.getNoteTaskId());
        NoteTask noteTask = this.baseMapper.selectOne(noteTaskLambdaQueryWrapper);
        return this.getAdminNoteTaskInfo(noteTask);
    }

    private AdminNoteTaskDTO getAdminNoteTaskInfo(NoteTask noteTask) {
        Long needSubmitCount = this.getNoteTaskNeedSubmitCount(NoteTaskQueryParam.NoteTaskQueryParamBuilder()
                .noteTaskId(noteTask.getId())
                .build());
        LambdaQueryWrapper<NoteTaskSubmissionRecord> submissionRecordLambdaQueryWrapper =
                new LambdaQueryWrapper<>();
        submissionRecordLambdaQueryWrapper
                .eq(NoteTaskSubmissionRecord::getNoteTaskId, noteTask.getId())
                .eq(NoteTaskSubmissionRecord::getStatus, NoteTaskSubmissionRecordStatus.NORMAL.getValue());
        noteTask.setSubmittedCount(noteTaskSubmissionRecordService
                .getBaseMapper().selectCount(submissionRecordLambdaQueryWrapper));
        AdminNoteTaskDTO adminNoteTaskDTO = new AdminNoteTaskDTO(noteTask);
        adminNoteTaskDTO.setNeedSubmitCount(needSubmitCount);
        if (0L == needSubmitCount) {
            adminNoteTaskDTO.setSubmissionProgress(100.0);
        } else {
            adminNoteTaskDTO.setSubmissionProgress(new BigDecimal(1.0 * adminNoteTaskDTO.getSubmittedCount() / needSubmitCount)
                    .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        }
        return adminNoteTaskDTO;
    }

    /**
     * 管理员获取任务列表
     *
     * @param queryParam
     * @return
     */
    @RequiresKnowledgeBasePermissions(value = KnowledgeBasePermissions.MANAGE,
            message = "没有权限查看笔记任务信息")
    @PageValid
    @Override
    public PageBean<AdminNoteTaskDTO> getAdminNoteTasks(NoteTaskQueryParam queryParam) {

        LambdaQueryWrapper<NoteTask> noteTaskLambdaQueryWrapper = new LambdaQueryWrapper<>();
        noteTaskLambdaQueryWrapper
                .eq(NoteTask::getKnowledgeBaseId, queryParam.getId());
        PageHelper.startPage(queryParam.getPage(), queryParam.getPageSize(), "create_time desc");
        List<NoteTask> noteTaskList = this.baseMapper.selectList(noteTaskLambdaQueryWrapper);
        PageInfo<NoteTask> pageInfo = new PageInfo<>(noteTaskList);

//        Long needSubmitCount = knowledgeBaseService.getKnowledgeBaseMemberCount(queryParam);
//        DecimalFormat df = new DecimalFormat("#.00");

        List<AdminNoteTaskDTO> adminNoteTaskDTOList = noteTaskList.stream()
                .map(noteTask -> {
                    Long needSubmitCount = this.getNoteTaskNeedSubmitCount(NoteTaskQueryParam.NoteTaskQueryParamBuilder()
                            .noteTaskId(noteTask.getId())
                            .build());
                    LambdaQueryWrapper<NoteTaskSubmissionRecord> submissionRecordLambdaQueryWrapper =
                            new LambdaQueryWrapper<>();
                    submissionRecordLambdaQueryWrapper
                            .eq(NoteTaskSubmissionRecord::getNoteTaskId, noteTask.getId())
                            .eq(NoteTaskSubmissionRecord::getStatus, NoteTaskSubmissionRecordStatus.NORMAL.getValue());
                    noteTask.setSubmittedCount(noteTaskSubmissionRecordService
                            .getBaseMapper().selectCount(submissionRecordLambdaQueryWrapper));
                    AdminNoteTaskDTO adminNoteTaskDTO = new AdminNoteTaskDTO(noteTask);
                    adminNoteTaskDTO.setNeedSubmitCount(needSubmitCount);
                    if (0L == needSubmitCount) {
                        adminNoteTaskDTO.setSubmissionProgress(100.0);
                    } else {
                        adminNoteTaskDTO.setSubmissionProgress(new BigDecimal(1.0 * adminNoteTaskDTO.getSubmittedCount() / needSubmitCount)
                                .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                    }

                    return adminNoteTaskDTO;
                })
                .collect(Collectors.toList());

//        PageBean<AdminNoteTaskDTO> pageBean = new PageBean<>();
//        pageBean.setPages(pageInfo.getPages());
//        pageBean.setTotal(pageInfo.getTotal());
//        pageBean.setRows(adminNoteTaskDTOList);
        return PageBean.<AdminNoteTaskDTO>builder()
                .rows(adminNoteTaskDTOList)
                .pages(pageInfo.getPages())
                .total(pageInfo.getTotal())
                .current(queryParam.getPage())
                .build();
    }

    /**
     * 获取任务提交的笔记操作次数列表
     *
     * @param queryParam
     * @return
     */
    @Override
    @RequiresNoteTaskPermissions(NoteTaskPermissions.MANAGE)
    public List<NoteOperationCount> getNoteOperationCounts(NoteTaskQueryParam queryParam) {
        return this.baseMapper.selectNoteOperationCount(queryParam.getNoteTaskId()).stream()
                .sorted(Comparator.comparing(NoteOperationCount::getCount))
                .collect(Collectors.toList());
    }

    /**
     * 退回消息
     *
     * @param submissionReturnParam
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    @RequiresNoteTaskPermissions(NoteTaskPermissions.MANAGE)
    public String returnSubmission(SubmissionReturnParam submissionReturnParam) {
        LoginUser loginUser = tokenUtil.getLoginUser();
        Date date = new Date();
        NoteTaskSubmissionRecord noteTaskSubmissionRecord = submissionReturnParam.getNoteTaskSubmissionRecord();
        if (NoteTaskSubmissionRecordStatus.RETURNED.getValue() == noteTaskSubmissionRecord.getStatus()) {
            throw new UserParamException("提交已经退回了，不能重复操作", ResCode.USER_REQUEST_PARAM_ERROR);
        }
        noteTaskSubmissionRecord.setStatus(NoteTaskSubmissionRecordStatus.RETURNED.getValue());
        // 更新记录状态为已退回
        noteTaskSubmissionRecord.setUpdateTime(date);
        noteTaskSubmissionRecord.setUpdateBy(loginUser.getSysUser().getId());
        noteTaskSubmissionRecordService.getBaseMapper().updateById(noteTaskSubmissionRecord);

        LambdaQueryWrapper<UserNoteTask> userNoteTaskLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userNoteTaskLambdaQueryWrapper
                .eq(UserNoteTask::getUserId, noteTaskSubmissionRecord.getUserId())
                .eq(UserNoteTask::getNoteTaskId, submissionReturnParam.getNoteTaskId());
        UserNoteTask userNoteTask = userNoteTaskMapper.selectOne(userNoteTaskLambdaQueryWrapper);
        userNoteTask.setStatus(UserNoteTaskStatus.RETURNED.getValue());

        // 更新笔记状态为作者可管理
        LambdaQueryWrapper<Note> noteLambdaQueryWrapper = new LambdaQueryWrapper<>();
        noteLambdaQueryWrapper
                .eq(Note::getId, noteTaskSubmissionRecord.getNoteId())
                .select(Note::getPermissions, Note::getId);
        Note note = noteService.getBaseMapper().selectOne(noteLambdaQueryWrapper);
        note.setPermissions("70000");
        noteService.getBaseMapper().updateById(note);

        // 更新状态为已退回
        userNoteTaskMapper.update(userNoteTask, userNoteTaskLambdaQueryWrapper);

        // 插入历史日志
        String destination = rocketMQProperties.getNoteTaskTopic() + ":" + NoteTaskTagsEnum.INSERT_HISTORY.name();
        rocketMQTemplate.send(destination, MessageBuilder.withPayload(NoteTaskOperationHistory.builder()
                .noteTaskId(submissionReturnParam.getNoteTaskId())
                .type(NoteTaskOperationType.RETURN_SUBMISSION.getValue())
                .operatorId(loginUser.getSysUser().getId())
                .operationTime(date)
                .noteTaskUserId(noteTaskSubmissionRecord.getUserId())
                .noteTaskSubmissionRecordId(noteTaskSubmissionRecord.getId())
                .deleted(0)
                .createBy(loginUser.getSysUser().getId())
                .createTime(date)
                .updateBy(loginUser.getSysUser().getId())
                .updateTime(date)
                .build()).build());
        return Constants.SUCCESS_RES;
    }

    @Override
    public List<NoteTaskHistoryVO> getNoteTaskHistoryList(Long noteTaskId) {
        LoginUser loginUser = tokenUtil.getLoginUser();
        return noteTaskOperationHistoryService.getNoteTaskHistoryList(loginUser.getSysUser().getId(), noteTaskId);
    }


    @RequiresKnowledgeBasePermissions(value = KnowledgeBasePermissions.MANAGE, message = "权限不足")
    @Override
    public NoteTaskUserAnalyzeVO getUserNoteTaskAnalyze(NoteTaskAnalyzeQueryParam queryParam) {
        PageHelper.startPage(queryParam.getPage(), queryParam.getPageSize());
        List<NoteTaskAnalyzePO> noteTaskAnalyzes = this.baseMapper.selectNoteTaskAnalyze(queryParam);
        PageInfo<NoteTaskAnalyzePO> pageInfo = new PageInfo<>(noteTaskAnalyzes);

        long sumEditCounts = 0L;
        for (NoteTaskAnalyzePO noteTaskAnalyzePO : noteTaskAnalyzes) {
            sumEditCounts = sumEditCounts + noteTaskAnalyzePO.getEditCount();
        }
        double averageEditCounts = 0.0;
        if (sumEditCounts != 0L) {
            averageEditCounts = 1.0 * sumEditCounts / noteTaskAnalyzes.size();
        }
        return NoteTaskUserAnalyzeVO.NoteTaskUserAnalyzeVOBuilder()
                .averageEditCounts(averageEditCounts)
                .rows(noteTaskAnalyzes)
                .pages(pageInfo.getPages())
                .current(queryParam.getPage())
                .total(pageInfo.getTotal())
                .build();
    }

    @Override
    public Integer insertUserNoteTask(UserNoteTask userNoteTask) {
        return userNoteTaskMapper.insert(userNoteTask);
    }

    @Override
    public List<NoteTask> getNoteTasksByKnowledgeBaseId(Long knowledgeBaseId) {
        LambdaQueryWrapper<NoteTask> noteTaskLambdaQueryWrapper = new LambdaQueryWrapper<>();
        noteTaskLambdaQueryWrapper
                .eq(NoteTask::getKnowledgeBaseId, knowledgeBaseId);
        return this.baseMapper.selectList(noteTaskLambdaQueryWrapper);
    }

    @RequiresNoteTaskPermissions(NoteTaskPermissions.MANAGE)
    @Override
    public List<NoteTaskChartsVO> getNoteTaskChartsData(NoteTaskChartsQueryParam queryParam) {
        NoteTaskSubmissionTimePO noteTaskSubmissionTimePO =
                this.baseMapper.selectNoteTaskSubmissionTime(queryParam.getNoteTaskId());
        Date roundedEarliestTime = DateUtils.roundDownToHour(noteTaskSubmissionTimePO.getEarliestTime());
        Date roundedLatestTime = DateUtils.roundUpToHour(noteTaskSubmissionTimePO.getLatestTime());
        Calendar calendar = DateUtils.buildCalendar(roundedEarliestTime);
        List<NoteTaskChartsVO> noteTaskChartsVOList = new ArrayList<>();
        while (calendar.getTime().before(roundedLatestTime)) {
            Calendar endTimeCalendar = DateUtils.buildCalendar(calendar.getTime());
            endTimeCalendar.add(Calendar.HOUR_OF_DAY, 1);
            noteTaskChartsVOList.add(NoteTaskChartsVO
                    .builder()
                    .startTime(calendar.getTime())
                    .endTime(endTimeCalendar.getTime())
                    .chartsPOList(this.baseMapper.selectNoteTaskCharts(NoteTaskChartsSelectParam
                            .builder()
                            .startTime(calendar.getTime()).endTime(endTimeCalendar.getTime())
                            .noteTaskId(queryParam.getNoteTaskId()).build()))
                    .build());
            calendar.add(Calendar.HOUR_OF_DAY, 1);
        }
        return noteTaskChartsVOList;
    }


    @Override
    public List<UserNoteTask> getTaskUsers(Long taskId) {
        LambdaQueryWrapper<UserNoteTask> userNoteTaskLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userNoteTaskLambdaQueryWrapper
                .eq(UserNoteTask::getNoteTaskId, taskId);
        return userNoteTaskMapper.selectList(userNoteTaskLambdaQueryWrapper);
    }
}
