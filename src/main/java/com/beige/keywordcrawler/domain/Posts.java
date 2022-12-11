package com.beige.keywordcrawler.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Document(collection = "posts")
@ToString
public class Posts {

    @Id
    private String id = UUID.randomUUID().toString();
    @Indexed(unique = true)
    private String postNo;
    private String title;
    private String originUrl;

    @CreatedDate
    private LocalDateTime createdTime = LocalDateTime.now();

    @Builder
    public Posts(String postNo, String title, String text, String originUrl) {
        this.postNo = postNo;
        this.title = title;
        this.originUrl = originUrl;
    }
}
