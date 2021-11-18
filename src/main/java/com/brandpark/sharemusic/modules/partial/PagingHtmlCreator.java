package com.brandpark.sharemusic.modules.partial;


import com.brandpark.sharemusic.modules.util.page.dto.PagingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.AbstractContext;
import org.thymeleaf.context.Context;

import java.util.stream.IntStream;

@RequiredArgsConstructor
@Component
public class PagingHtmlCreator {

    private final TemplateEngine templateEngine;

    public PageHtmlResult getPageHtmlResult(AbstractContext context, PagingDto page, String listName, String viewPath) {
        context.setVariable(listName, page.getContents());
        String listHtml = getListHtml(viewPath, context);

        String paginationHtml = getPaginationHtml(page);

        return new PageHtmlResult(listHtml, paginationHtml);
    }

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

    public String getListHtml(String viewPath, AbstractContext webContext) {
        return templateEngine.process(viewPath, webContext);
    }
}
