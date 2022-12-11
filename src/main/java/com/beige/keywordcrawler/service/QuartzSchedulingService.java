package com.beige.keywordcrawler.service;

import com.beige.keywordcrawler.quartzjob.CrawlerJob;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static org.quartz.CronScheduleBuilder.cronSchedule;

@Service
@RequiredArgsConstructor
public class QuartzSchedulingService {

    @Value("${crawler.cron.trigger}")
    private String cronExpression;
    private final CrawlerService crawlerService;

    @PostConstruct
    public void init() throws SchedulerException {

        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("crawlerService", crawlerService);

        JobDetail jobDetail = JobBuilder.newJob(CrawlerJob.class)
                .usingJobData(jobDataMap)
                .withIdentity("crawl_ppomppu", Scheduler.DEFAULT_GROUP)
                .build();

        // CronTrigger
        Trigger cronTrigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger_crawl_ppomppu", Scheduler.DEFAULT_GROUP)
                .withSchedule(cronSchedule(cronExpression))
                .build();

        scheduler.scheduleJob(jobDetail, cronTrigger);
        scheduler.start();
    }
}
