package com.weshare.web.controller;

import com.weshare.entity.po.CategoryInfo;
import com.weshare.entity.query.CategoryInfoQuery;
import com.weshare.entity.vo.ResponseVO;
import com.weshare.service.CategoryInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController extends ABaseController{

@Resource
private CategoryInfoService categoryInfoService;

    @RequestMapping("/loadAllCategory")
    public ResponseVO loadAllCategory() {
        return getSuccessResponseVO(categoryInfoService.getAllCategoryList());
    }
}
