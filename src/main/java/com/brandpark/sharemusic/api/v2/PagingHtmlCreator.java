package com.brandpark.sharemusic.api.v2;


import com.brandpark.sharemusic.api.v2.dto.PagingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.WebContext;

import java.util.stream.IntStream;

@RequiredArgsConstructor
@Component
public class PagingHtmlCreator {

    private final TemplateEngine templateEngine;

    public <T> String getPaginationHtml(PagingDto<T> pageObj) {

        Context context = new Context();
        context.setVariable("pageObj", pageObj);

        int paginationStartNum = pageObj.getPageNumber() / pageObj.getPaginationUnit() * pageObj.getPaginationUnit();
        int paginationEndNum = Math.min(pageObj.getTotalPages(), paginationStartNum + pageObj.getPaginationUnit());
        int[] pageNumberArray = IntStream
                .range(paginationStartNum, paginationEndNum)
                .toArray();

        context.setVariable("pageNumberArray", pageNumberArray);

        return templateEngine.process("partial/pagination", context);
    }

    public String getListHtml(String viewPath, WebContext webContext) {
        return templateEngine.process(viewPath, webContext);
    }

    public String getListHtml(String viewPath, Context context) {
        return templateEngine.process(viewPath, context);
    }
}
