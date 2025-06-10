package com.starter.backend.util;

import com.starter.backend.exceptions.BadRequestException;
import org.springframework.security.core.parameters.P;

public interface Constants {
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "firstName";
    public int MAX_PAGE_SIZE=1000;
    public static void validatePageNumberAndPageSize(int page, int size){
        if(page<0){
            throw new BadRequestException("Page number is less than zero");
        }
        if(size<1){
            throw new BadRequestException("Page size cannot be less than one");
        }
        if(size>Constants.MAX_PAGE_SIZE){
            throw new BadRequestException("page size is greater then "+Constants.MAX_PAGE_SIZE);
        }
    }
}
