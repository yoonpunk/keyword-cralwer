package com.beige.keywordcrawler.service;

import com.beige.keywordcrawler.domain.Posts;
import com.beige.keywordcrawler.domain.PostsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlerService {

    private static final String TARGET_HOME_URL = "https://www.ppomppu.co.kr";
    private final PostsRepository postsRepository;
    private final LineNotifyService lineNotifyService;

    private List<String> targetUrls = new ArrayList<>();

    @PostConstruct
    public void init() {
        // 커뮤니티 카카오뱅크
        targetUrls.add("https://www.ppomppu.co.kr/search_bbs.php?bbs_cate=2&keyword=%C4%AB%C4%AB%BF%C0%B9%F0%C5%A9");

        // 커뮤니티 카뱅
        targetUrls.add("https://www.ppomppu.co.kr/search_bbs.php?bbs_cate=2&keyword=%C4%AB%B9%F0");

        // 타커뮤니티 카카오뱅크
        targetUrls.add("https://www.ppomppu.co.kr/search_bbs.php?bbs_cate=8&keyword=%C4%AB%C4%AB%BF%C0%B9%F0%C5%A9");

        // 타커뮤니티 카뱅
        targetUrls.add("https://www.ppomppu.co.kr/search_bbs.php?bbs_cate=8&keyword=%C4%AB%B9%F0");
    }

    public void doCrawls() throws InterruptedException {
        for (String targetUrl : targetUrls) {
            crawl(targetUrl);
            Thread.sleep(5000);
        }
    }

    public void crawl(String targetUrl) {
        Connection conn = Jsoup.connect(targetUrl);

        try {
            Document document = conn.get();
            Elements conts = document.getElementsByClass("conts");

            for (Element element : conts) {
                Elements titleElement = element.select("div.content > span.title > a");
                String title = titleElement.get(0).html();
                title = title.replaceAll("<b>", "");
                title = title.replaceAll("</b>", "");
                title = title.replaceAll("<font.*</font>", "");

                String originUrl = titleElement.get(0).attr("href");
                Pattern pattern = Pattern.compile("&no=.*&");
                Matcher matcher = pattern.matcher(originUrl);

                if (originUrl.startsWith("/zboard")) {
                    originUrl = TARGET_HOME_URL + originUrl;
                }

//                String postNo = "";
//                if (matcher.find()) {
//                    postNo = matcher.group();
//                    int startIndex = postNo.indexOf('=') + 1;
//                    postNo = postNo.substring(startIndex, postNo.length() - 2);
//                }

                Posts post = Posts.builder()
                        .title(title)
                        .originUrl(originUrl)
                        .build();

                try {
                    postsRepository.insert(post);
                    log.info(post.toString());

                    Thread.sleep(5000);
                    String message = post.getTitle() + " " + post.getOriginUrl();
                    lineNotifyService.sendNotifiy(message);
                    log.info("send Message=" + message);
                } catch (DuplicateKeyException e) {
                    log.info("old posts reported. don't insert this post(title: " + post.getTitle() + " url: " + post.getOriginUrl());
                }
            }

        } catch (Exception e) {
            log.error(e.toString());
            e.printStackTrace();
        }
    }


}
