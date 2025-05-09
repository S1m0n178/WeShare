package com.weshare.admin.controller;

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

    @RequestMapping("/loadCategory")
    public ResponseVO loadCategory(CategoryInfoQuery query) {
            query.setOrderBy("sort asc");
            query.setConvert2Tree(true);
            List<CategoryInfo> categoryInfoList = categoryInfoService.findListByParam(query);
        return getSuccessResponseVO(categoryInfoList);
    }

    @RequestMapping("/saveCategory")
    public ResponseVO saveCategory(@NotNull Integer pCategoryId,
                                   Integer categoryId,
                                   @NotNull String categoryCode,
                                   @NotNull String categoryName,
                                   String icon,
                                   String background){

        CategoryInfo categoryInfo = new CategoryInfo();

        categoryInfo.setCategoryId(categoryId);
        categoryInfo.setpCategoryId(pCategoryId);
        categoryInfo.setCategoryCode(categoryCode);
        categoryInfo.setCategoryName(categoryName);
        categoryInfo.setIcon(icon);
        categoryInfo.setBackground(background);

        categoryInfoService.saveCategory(categoryInfo);
        return  getSuccessResponseVO(null);
    }

    @RequestMapping("/delCategory")
    public ResponseVO delCategory(@NotNull Integer categoryId) {
        categoryInfoService.delCategory(categoryId);
        return  getSuccessResponseVO(null);
    }

    @RequestMapping("/changeSort")
    public ResponseVO changeSort(@NotNull Integer pCategoryId,
                                 @NotNull String categoryIds) {
        categoryInfoService.changeSort(pCategoryId,categoryIds);
        return  getSuccessResponseVO(null);
    }
}
