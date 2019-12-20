package com.newbiest.ams.service.impl;

import com.google.common.collect.Lists;
import com.newbiest.ams.dto.AlarmMessage;
import com.newbiest.ams.model.AlarmData;
import com.newbiest.ams.model.AlarmJob;
import com.newbiest.ams.repository.AlarmDataRepository;
import com.newbiest.ams.repository.AlarmJobRepository;
import com.newbiest.ams.service.AmsService;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.DateUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.base.threadlocal.ThreadLocalContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by guoxunbo on 2019-11-19 16:41
 */
@Service
@Transactional
@Slf4j
public class AmsServiceImpl implements AmsService {

    @Autowired
    AlarmJobRepository alarmJobRepository;

    @Autowired
    AlarmDataRepository alarmDataRepository;

    public void triggerAlarm(AlarmMessage alarmMessage) throws ClientException {
        try {
            List<AlarmJob> matchJobs = matchJobs(alarmMessage.getCategory(), alarmMessage.getType(), alarmMessage.getObjectId(), alarmMessage.getName());
            if (CollectionUtils.isNotEmpty(matchJobs)) {
                if (log.isInfoEnabled()) {
                    log.info("Alarm message matched jobs. [" + StringUtils.join(matchJobs.stream().map(AlarmJob :: getName).collect(Collectors.toList()), StringUtils.SEMICOLON_CODE)+ "]");
                }
                if (alarmMessage.getTriggerTime() == null) {
                    alarmMessage.setTriggerTime(DateUtils.now());
                }

                for (AlarmJob job : matchJobs) {
                    AlarmData alarmData = new AlarmData();
                    alarmMessage.transfer2Object(alarmData);

                    alarmData.setJobRrn(job.getObjectRrn());
                    alarmData.setJobId(job.getName());
                    alarmData.setJobDesc(job.getDescription());
                    alarmData.setStatus(AlarmData.STATUS_OPEN);
                    if (alarmData.getPriority() == null) {
                        alarmData.setPriority(job.getPriority());
                    }
                    if (StringUtils.isNullOrEmpty(alarmData.getSeverityLevel())) {
                        alarmData.setSeverityLevel(job.getSeverityLevel());
                    }
                    alarmDataRepository.save(alarmData);
                    //TODO 处理layer action
                }
            } else {
                if (log.isWarnEnabled()) {
                    log.warn("Alarm message [" + alarmMessage.toString() + "] not matched jobs");
                }
            }

        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private List<AlarmJob> matchJobs(String alarmCategory, String alarmType, String objectId, String alarmName) throws ClientException {
        try {
            List<AlarmJob> matchedJobs = Lists.newArrayList();
            List<AlarmJob> alarmJobs = alarmJobRepository.findByCategoryAndType(alarmCategory, alarmType);
            if (CollectionUtils.isNotEmpty(alarmJobs)) {
                for (AlarmJob alarmJob : alarmJobs) {
                    Pattern pattern;
                    if (!StringUtils.isNullOrEmpty(alarmJob.getObjectIdRegex())) {
                        pattern = Pattern.compile(alarmJob.getObjectIdRegex());
                        if (!pattern.matcher(objectId).matches()) {
                            continue;
                        }
                    }
                    if (!StringUtils.isNullOrEmpty(alarmJob.getAlarmNameRegex())) {
                        pattern = Pattern.compile(alarmJob.getAlarmNameRegex());
                        if (!pattern.matcher(alarmName).matches()) {
                            continue;
                        }

                    }
                    matchedJobs.add(alarmJob);
                }
            }
            return matchedJobs;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

}
