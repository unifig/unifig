package etl.dispatch.quartz.jobs;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import etl.dispatch.base.holder.SpringContextHolder;
import etl.dispatch.config.api.IEtlConfigApiService;
import etl.dispatch.config.entity.SignInfoTasksEntity;
import etl.dispatch.config.enums.ClassifyEnum;
import etl.dispatch.config.enums.IsSuccessEnum;
import etl.dispatch.config.enums.ScriptTypeEnum;
import etl.dispatch.quartz.notice.QuartzNotice;
import etl.dispatch.script.IScriptService;
import etl.dispatch.script.ScriptBean;
import etl.dispatch.script.ScriptCallBack;
import etl.dispatch.script.constant.CommonConstants;
import etl.dispatch.util.DateUtil;
import etl.dispatch.util.StringUtil;
import etl.dispatch.util.helper.ObjectClassHelper;

/**
 * 定时任务调度类，会根据数据库中配置的定时任务时间自动触发该方法。
 *
 *
 */
@Component("scheduledJob")
public class ScheduledJob implements Job, Serializable {
    private static final long serialVersionUID = 2146758924773151564L;
    private static final Logger logger = LoggerFactory.getLogger(ScheduledJob.class);

    @SuppressWarnings("unchecked")
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            if (null == jobDataMap || jobDataMap.isEmpty()) {
                logger.info("ScheduledJob execute fail; get jobDataMap is null. ");
                return;
            }
            Date startTime = DateUtil.getDate();
            // 任务去重
            List<Map<String, Object>> relyTasksMapListTmp = (List<Map<String, Object>>) jobDataMap.get(CommonConstants.PROP_QUARTZ_CONTEXT);
            if (null == relyTasksMapListTmp || relyTasksMapListTmp.isEmpty()) {
                logger.info("ScheduledJob execute fail; get rely tasksMap list is null. ");
                return;
            }
            List<Map<String, Object>> relyTasksMapList = new ArrayList<>();
            relyTasksMapListTmp.forEach(l -> {
                List<Map<String, Object>> rr = relyTasksMapList.stream().filter(ls -> {
                    return ls.get("taskId").toString().equals(l.get("taskId").toString());
                }).collect(Collectors.toList());
                if (rr == null || rr.size() <= 0) {
                    relyTasksMapList.add(l);
                }
            });
            if (null == relyTasksMapList || relyTasksMapList.isEmpty()) {
                logger.info("ScheduledJob execute fail; get rely tasksMap set is null. ");
                return;
            }
            String jobGroupId = String.valueOf(jobDataMap.get(CommonConstants.PROP_GROUP_GROUPID));
            String jobGroupName = String.valueOf(jobDataMap.get(CommonConstants.PROP_GROUP_GROUPNAME));
            String alarmJson = String.valueOf(jobDataMap.get(CommonConstants.PROP_GROUP_REPTNOTICE));
            logger.info("ScheduledJob execute start; jobGroupId:" + jobGroupId + ", rely tasksMap set:" + JSON.toJSONString(relyTasksMapListTmp));
            // 删除历史记录
            if (!StringUtil.isNullOrEmpty(jobGroupId)) {
                delHistoryIdValue(jobGroupId, ClassifyEnum.GROUP);
                for (Map<String, Object> tasksMap : relyTasksMapList) {
                    String groupId = String.valueOf(tasksMap.get(CommonConstants.PROP_TASKS_GROUPID));
                    String tasksId = String.valueOf(tasksMap.get(CommonConstants.PROP_TASKS_TASKID));
                    delHistoryIdValue(groupId + "_" + tasksId, ClassifyEnum.TASK);
                }
            }
            CountDownLatch countDownLatch = new CountDownLatch(relyTasksMapList.size());
            // 任务结果状态
            List<Boolean> taskStatusList = new ArrayList<>();
            // 线程执行返回
            List<Future<Boolean>> taskFutureList = new ArrayList<>();
            ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("tasks--%d").build();
            ThreadPoolExecutor taskScheduledJob = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2,
                                                                        16,
                                                                        0L,
                                                                        TimeUnit.MILLISECONDS,
                                                                        new LinkedBlockingDeque<>(1024),
                                                                        threadFactory,
                                                                        new ThreadPoolExecutor.AbortPolicy());
            // 使用多线程调用脚本
            for (Map<String, Object> tasksMap : relyTasksMapList) {
                Future<Boolean> taskStatus = this.doTask(taskScheduledJob, tasksMap, countDownLatch);
                if (null != taskStatus) {
                    taskFutureList.add(taskStatus);
                }
            }
            // 主线程阻塞
            for (Future<Boolean> future : taskFutureList) {
                taskStatusList.add(future.get());
            }
            countDownLatch.await();
            taskScheduledJob.shutdown();
            while (true) {
                if (taskScheduledJob.isTerminated()) {
                    String message = "Job execution success; time" + DateUtil.getSysStrCurrentDate("yyyy-MM-dd") + " ,job Group Config [groupId:" + jobGroupId + " , groupName:" + jobGroupName + "], Contains " + relyTasksMapList.size() + "scheduling tasks";
                    IEtlConfigApiService etlConfigApiService = SpringContextHolder.getBean("iEtlConfigApiService", IEtlConfigApiService.class);
                    if (null != etlConfigApiService && !taskStatusList.contains(false)) {
                        SignInfoTasksEntity signInfoTasks = new SignInfoTasksEntity();
                        ObjectClassHelper.setFieldValue(signInfoTasks, "classify", ClassifyEnum.GROUP.getCode());
                        ObjectClassHelper.setFieldValue(signInfoTasks, "taskId", jobGroupId);
                        ObjectClassHelper.setFieldValue(signInfoTasks, "taskName", jobGroupName);
                        ObjectClassHelper.setFieldValue(signInfoTasks, "timeSign", DateUtil.getSysStrCurrentDate("yyyy-MM-dd"));
                        ObjectClassHelper.setFieldValue(signInfoTasks, "startTime", startTime);
                        ObjectClassHelper.setFieldValue(signInfoTasks, "endTime", DateUtil.getDate());
                        ObjectClassHelper.setFieldValue(signInfoTasks, "isSuccess", IsSuccessEnum.SUCCESS.getCode());
                        ObjectClassHelper.setFieldValue(signInfoTasks, "message", message);
                        etlConfigApiService.saveSignInfoTasks(signInfoTasks);
                    } else {
                        QuartzNotice.doAlarm(true, alarmJson, null, message, jobGroupName);
                        SignInfoTasksEntity signInfoTasks = new SignInfoTasksEntity();
                        ObjectClassHelper.setFieldValue(signInfoTasks, "classify", ClassifyEnum.GROUP.getCode());
                        ObjectClassHelper.setFieldValue(signInfoTasks, "taskId", jobGroupId);
                        ObjectClassHelper.setFieldValue(signInfoTasks, "taskName", jobGroupName);
                        ObjectClassHelper.setFieldValue(signInfoTasks, "timeSign", DateUtil.getSysStrCurrentDate("yyyy-MM-dd"));
                        ObjectClassHelper.setFieldValue(signInfoTasks, "startTime", startTime);
                        ObjectClassHelper.setFieldValue(signInfoTasks, "endTime", DateUtil.getDate());
                        ObjectClassHelper.setFieldValue(signInfoTasks, "isSuccess", IsSuccessEnum.FAIL.getCode());
                        ObjectClassHelper.setFieldValue(signInfoTasks, "message", message);
                        etlConfigApiService.saveSignInfoTasks(signInfoTasks);
                    }
                    break;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("reason for failure:" + e.getMessage());
        }
    }

    /**
     * 执行调度任务
     *
     * @param tasksMap
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public Future<Boolean> doTask(ExecutorService taskScheduledJob, Map<String, Object> tasksMap, CountDownLatch countDownLatch) throws InterruptedException, ExecutionException {
        String groupId = String.valueOf(tasksMap.get(CommonConstants.PROP_TASKS_GROUPID));
        String tasksId = String.valueOf(tasksMap.get(CommonConstants.PROP_TASKS_TASKID));
        String relyTaskId = String.valueOf(tasksMap.get(CommonConstants.PROP_TASKS_RELYTASKID));
        String allRelyTasksId = String.valueOf(tasksMap.get(CommonConstants.PROP_TASKS_AllRELYTASKID));
        String allRelyGroupId = String.valueOf(tasksMap.get(CommonConstants.PROP_GROUP_AllRELYGROUPID));
        String scriptPath = String.valueOf(tasksMap.get(CommonConstants.PROP_SCRIPT_SCRIPTPATH));
        int scriptType = (int) tasksMap.get(CommonConstants.PROP_SCRIPT_SCRIPTTYPE);
        Future<Boolean> taskStatus = null;
        // 调度脚本Python脚本
        if (scriptType == ScriptTypeEnum.PYTHON.getCode()) {
            File file = new File(scriptPath);
            if (!file.exists()) {
                logger.error("python脚本" + scriptPath + "不存在！");
                return taskStatus;
            }
            taskStatus = taskScheduledJob.submit(new Callable<Boolean>() {
                private volatile boolean isPerform = false;
                private volatile long startTimes = 0L;
                private volatile boolean taskStatus;

                @Override
                public Boolean call() {
                    for (; !isPerform; ) {
                        try {
                            // 依赖任务组未执行完，休眠等待60秒
                            if (!ScheduledJob.isCanPerform(allRelyGroupId)) {
                                // 休眠60秒
                                Thread.sleep(60 * 1000);
                            } else {
                                // 判断组内任务依赖是否执行完毕
                                if (!relyTaskId.equalsIgnoreCase(CommonConstants.PROP_QUARTZ_ROOTRELY) && !ScheduledJob.isCanPerform(allRelyTasksId, groupId)) {
                                    // 休眠30秒
                                    Thread.sleep(30 * 1000);
                                } else {
                                    // 执行Python脚本
                                    IScriptService pythonScript = SpringContextHolder.getBean("pythonScript", IScriptService.class);
                                    startTimes = System.currentTimeMillis();
                                    pythonScript.init(tasksMap);
                                    pythonScript.start(startTimes, groupId, new ScriptCallBack() {
                                        @Override
                                        public void setSign(boolean isSuccess, String errorMsg, ScriptBean scriptBean) {
                                            QuartzNotice.taskAlarm(isSuccess, errorMsg, scriptBean);
                                            // 任务组中有任意一项任务失败，任务组失败
                                            taskStatus = isSuccess;
                                            countDownLatch.countDown();
                                        }
                                    });
                                    this.isPerform = true;
                                }
                            }
                        } catch (InterruptedException e) {
                            logger.error("Python script execution failed, reason for failure:" + e.getMessage());
                        }
                    }
                    return taskStatus;
                }
            });
            // 调度脚本Java脚本
        } else if (scriptType == ScriptTypeEnum.JAVA.getCode()) {
            String scriptKey = String.valueOf(tasksMap.get(CommonConstants.PROP_SCRIPT_SCRIPTKEY));
            Collection<IScriptService> javaScriptsSet = SpringContextHolder.getBeansOfType(IScriptService.class).values();
            if (null != javaScriptsSet && !javaScriptsSet.isEmpty()) {
                for (IScriptService javaScript : javaScriptsSet) {
                    if (scriptPath.equals(javaScript.getClass().getCanonicalName())) {
                        taskStatus = taskScheduledJob.submit(new Callable<Boolean>() {
                            private volatile boolean isPerform = false;
                            private volatile long startTimes = 0l;
                            private volatile boolean taskStatus;

                            @Override
                            public Boolean call() {
                                for (; !isPerform; ) {
                                    try {
                                        // 依赖任务组未执行完毕，休眠等待60秒
                                        if (!ScheduledJob.isCanPerform(allRelyGroupId)) {
                                            // 休眠60秒
                                            Thread.sleep(60 * 1000);
                                        } else {
                                            // 判断组内任务依赖是否执行完毕
                                            if (!relyTaskId.equalsIgnoreCase(CommonConstants.PROP_QUARTZ_ROOTRELY) && !ScheduledJob.isCanPerform(allRelyTasksId, groupId)) {
                                                // 休眠30秒
                                                Thread.sleep(30 * 1000);
                                            } else {
                                                // 执行Java数据脚本
                                                startTimes = System.currentTimeMillis();
                                                javaScript.init(tasksMap);
                                                javaScript.start(startTimes, groupId, new ScriptCallBack() {
                                                    @Override
                                                    public void setSign(boolean isSuccess, String errorMsg, ScriptBean scriptBean) {
                                                        QuartzNotice.taskAlarm(isSuccess, errorMsg, scriptBean);
                                                        // 任务组中有任意一项任务失败，任务组失败
                                                        taskStatus = isSuccess;
                                                        countDownLatch.countDown();
                                                    }
                                                });
                                                this.isPerform = true;
                                            }
                                        }
                                    } catch (InterruptedException e) {
                                        logger.error("Java script execution failed, reason for failure:" + e.getMessage());
                                    }
                                }
                                return taskStatus;
                            }
                        });
                        break;
                    }
                }
            }
            // 调度脚本Shell脚本
        } else if (scriptType == ScriptTypeEnum.SHELL.getCode()) {
            File file = new File(scriptPath);
            if (!file.exists()) {
                logger.error("shell脚本" + scriptPath + "不存在！");
                return taskStatus;
            }
            taskStatus = taskScheduledJob.submit(new Callable<Boolean>() {
                private volatile boolean isPerform = false;
                private volatile long startTimes = 0l;
                private volatile boolean taskStatus;

                @Override
                public Boolean call() {
                    for (; !isPerform; ) {
                        try {
                            // 依赖任务组未执行完毕，休眠等待60秒
                            if (!ScheduledJob.isCanPerform(allRelyGroupId)) {
                                // 休眠60秒
                                Thread.sleep(60 * 1000);
                            } else {
                                // 判断组内任务依赖是否执行完毕
                                if (!relyTaskId.equalsIgnoreCase(CommonConstants.PROP_QUARTZ_ROOTRELY) && !ScheduledJob.isCanPerform(allRelyTasksId, groupId)) {
                                    // 休眠30秒
                                    Thread.sleep(30 * 1000);
                                } else {
                                    // 执行shell脚本
                                    IScriptService shellScript = SpringContextHolder.getBean("shellScript", IScriptService.class);
                                    startTimes = System.currentTimeMillis();
                                    shellScript.init(tasksMap);
                                    shellScript.start(startTimes, groupId, new ScriptCallBack() {
                                        @Override
                                        public void setSign(boolean isSuccess, String errorMsg, ScriptBean scriptBean) {
                                            QuartzNotice.taskAlarm(isSuccess, errorMsg, scriptBean);
                                            // 任务组中有任意一项任务失败，任务组失败
                                            taskStatus = isSuccess;
                                            countDownLatch.countDown();
                                        }
                                    });
                                    this.isPerform = true;
                                }
                            }
                        } catch (InterruptedException e) {
                            logger.error("Python script execution failed, reason for failure:" + e.getMessage());
                        }
                    }
                    return taskStatus;
                }
            });
        }
        return taskStatus;
    }

    /**
     * 判断任务组之间依赖执行情况
     *
     * @param allRelyGroupId
     * @return
     */
    public static boolean isCanPerform(String allRelyGroupId) {
        // 依赖为NULL,直接返回true
        if (StringUtil.isNullOrEmpty(allRelyGroupId)) {
            return true;
        }
        // 依赖为-1，或者依赖为NULL,直接返回true
        if ((CommonConstants.PROP_QUARTZ_ROOTRELY + ",").equalsIgnoreCase(StringUtils.join(allRelyGroupId, ","))) {
            return true;
        }
        IEtlConfigApiService etlConfigApiService = SpringContextHolder.getBean("iEtlConfigApiService", IEtlConfigApiService.class);
        String[] allRelyGroup = allRelyGroupId.split(",");
        for (String relyGroupId : allRelyGroup) {
            // 必须全匹配,任何一个不存在则依赖不成立
            if (null != etlConfigApiService) {
                SignInfoTasksEntity signInfoTasks = new SignInfoTasksEntity();
                ObjectClassHelper.setFieldValue(signInfoTasks, "classify", ClassifyEnum.GROUP.getCode());
                ObjectClassHelper.setFieldValue(signInfoTasks, "taskId", relyGroupId);
                ObjectClassHelper.setFieldValue(signInfoTasks, "timeSign", DateUtil.getSysStrCurrentDate("yyyy-MM-dd"));
                List<SignInfoTasksEntity> logInfoList = etlConfigApiService.findSignInfoTasks(signInfoTasks);
                if (null == logInfoList || logInfoList.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断任务组内，任务之间依赖执行情况
     *
     * @param allRelyTasksId
     * @param groupId
     * @return
     */
    public static boolean isCanPerform(String allRelyTasksId, String groupId) {
        // 依赖为NULL,直接返回true
        if (StringUtil.isNullOrEmpty(allRelyTasksId)) {
            return true;
        }
        IEtlConfigApiService etlConfigApiService = SpringContextHolder.getBean("iEtlConfigApiService", IEtlConfigApiService.class);
        String[] allRelyTasks = allRelyTasksId.split(",");
        for (String relyTasksId : allRelyTasks) {
            // 必须全匹配,任何一个不存在则依赖不成立
            if (null != etlConfigApiService) {
                SignInfoTasksEntity signInfoTasks = new SignInfoTasksEntity();
                ObjectClassHelper.setFieldValue(signInfoTasks, "classify", ClassifyEnum.TASK.getCode());
                ObjectClassHelper.setFieldValue(signInfoTasks, "taskId", groupId + "_" + relyTasksId);
                ObjectClassHelper.setFieldValue(signInfoTasks, "timeSign", DateUtil.getSysStrCurrentDate("yyyy-MM-dd"));
                List<SignInfoTasksEntity> logInfoList = etlConfigApiService.findSignInfoTasks(signInfoTasks);
                if (null == logInfoList || logInfoList.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 删除历史运行记录
     *
     * @return
     */
    public static boolean delHistoryIdValue(String groupOrTaskId, ClassifyEnum classifyEnum) {
        IEtlConfigApiService etlConfigApiService = SpringContextHolder.getBean("iEtlConfigApiService", IEtlConfigApiService.class);
        if (null != etlConfigApiService) {
            SignInfoTasksEntity signInfoTasks = new SignInfoTasksEntity();
            ObjectClassHelper.setFieldValue(signInfoTasks, "classify", classifyEnum.getCode());
            ObjectClassHelper.setFieldValue(signInfoTasks, "taskId", groupOrTaskId);
            ObjectClassHelper.setFieldValue(signInfoTasks, "timeSign", DateUtil.getSysStrCurrentDate("yyyy-MM-dd"));
            etlConfigApiService.deleteSignInfoTasks(signInfoTasks);
        }
        return true;
    }

    public static void main(String[] args) {
        // 重复跑需要删除redis缓存
        for (int i = 0; i < 100; i++) {
            delHistoryIdValue(String.valueOf(i), ClassifyEnum.GROUP);
        }
    }

}