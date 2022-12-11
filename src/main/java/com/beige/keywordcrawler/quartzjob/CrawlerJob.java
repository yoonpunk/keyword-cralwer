package com.beige.keywordcrawler.quartzjob;

import com.beige.keywordcrawler.service.CrawlerService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalDateTime;

@Slf4j
public class CrawlerJob extends QuartzJobBean {

    private ApplicationContext applicationContext;
    private CrawlerService crawlerService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        long beforeTime = System.currentTimeMillis(); //코드 실행 전에 시간 받아오기
        LocalDateTime startedTime = LocalDateTime.now();
        log.info("job started at " + startedTime);

        crawlerService = (CrawlerService) context.getJobDetail().getJobDataMap().get("crawlerService");
        crawlerService.doCrawl();

        long afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
        long secDiffTime = (afterTime - beforeTime)/1000; //두 시간에 차 계산
        LocalDateTime endedTime = LocalDateTime.now();
        log.info("job ended at " + endedTime + " running time(s): " + secDiffTime + "s");
    }
}
