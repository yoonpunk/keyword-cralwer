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

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlerService {

    private static final String TARGET_URL = "https://www.ppomppu.co.kr/search_bbs.php?bbs_cate=2&keyword=%C4%AB%C4%AB%BF%C0%B9%F0%C5%A9";
    private static final String TARGET_HOME_URL = "https://www.ppomppu.co.kr";

    private final PostsRepository postsRepository;

    public void doCrawl() {
        Connection conn = Jsoup.connect(TARGET_URL);

        try {
            Document document = conn.get();
            Elements conts = document.getElementsByClass("conts");

            log.info(conts.toString());

            for (Element element : conts) {
                Elements titleElement = element.select("div.content > span.title > a");
                String title = titleElement.get(0).html();
                title = title.replaceAll("<b>", "");
                title = title.replaceAll("</b>", "");
                title = title.replaceAll("<font.*</font>", "");

                String originUrl = titleElement.get(0).attr("href");
                Pattern pattern = Pattern.compile("&no=.*&");
                Matcher matcher = pattern.matcher(originUrl);

                originUrl = TARGET_HOME_URL + originUrl;

                String postNo = "";
                if (matcher.find()) {
                    postNo = matcher.group();
                    int startIndex = postNo.indexOf('=') + 1;
                    postNo = postNo.substring(startIndex, postNo.length() - 2);
                }

                Posts post = Posts.builder()
                        .title(title)
                        .originUrl(originUrl)
                        .postNo(postNo)
                        .build();

                try {
                    postsRepository.insert(post);
                    log.info(post.toString());
                } catch (DuplicateKeyException e) {
                    log.info("old posts reported. don't insert this post(title: " + post.getTitle() + " url: " + post.getOriginUrl());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
