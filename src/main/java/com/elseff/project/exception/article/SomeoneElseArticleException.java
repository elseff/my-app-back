package com.elseff.project.exception.article;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class SomeoneElseArticleException extends RuntimeException {
    public SomeoneElseArticleException() {
        super("It's someone else's article. You can't modify her");
    }
}
