package com.weshare.admin.controller;

import com.weshare.entity.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("category")
public class CategoryController extends ABaseController{
    @RequestMapping("/loadDataList")
    public ResponseVO checkCode() {
        return getSuccessResponseVO(null);
    }
}
