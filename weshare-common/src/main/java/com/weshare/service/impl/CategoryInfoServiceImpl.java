package com.weshare.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.weshare.entity.constants.Constants;
import com.weshare.exception.BusinessException;
import org.springframework.stereotype.Service;

import com.weshare.entity.enums.PageSize;
import com.weshare.entity.query.CategoryInfoQuery;
import com.weshare.entity.po.CategoryInfo;
import com.weshare.entity.vo.PaginationResultVO;
import com.weshare.entity.query.SimplePage;
import com.weshare.mappers.CategoryInfoMapper;
import com.weshare.service.CategoryInfoService;
import com.weshare.utils.StringTools;


/**
 * 分类信息 业务接口实现
 */
@Service("categoryInfoService")
public class CategoryInfoServiceImpl implements CategoryInfoService {

	@Resource
	private CategoryInfoMapper<CategoryInfo, CategoryInfoQuery> categoryInfoMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<CategoryInfo> findListByParam(CategoryInfoQuery param) {
		List<CategoryInfo> categoryInfoList = this.categoryInfoMapper.selectList(param);
		//TODO 暂时树形转换有bug
//		if (param.getConvert2Tree() != null && param.getConvert2Tree()) {
//			categoryInfoList= convertLine2Tree(categoryInfoList, Constants.ZERO);
//		}
		return categoryInfoList;
	}
	private List<CategoryInfo> convertLine2Tree(List<CategoryInfo> dataList,Integer pid){
		List<CategoryInfo> children = new ArrayList<>();
		for(CategoryInfo m :dataList){
			if(m.getCategoryId()!=null&&m.getpCategoryId()!=null&&m.getpCategoryId().equals(pid)){
				m.setChildren(convertLine2Tree(dataList,m.getpCategoryId()));
				children.add(m);
			}
		}
		return children;
	}
	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(CategoryInfoQuery param) {
		return this.categoryInfoMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<CategoryInfo> findListByPage(CategoryInfoQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<CategoryInfo> list = this.findListByParam(param);
		PaginationResultVO<CategoryInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(CategoryInfo bean) {
		return this.categoryInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<CategoryInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.categoryInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<CategoryInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.categoryInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(CategoryInfo bean, CategoryInfoQuery param) {
		StringTools.checkParam(param);
		return this.categoryInfoMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(CategoryInfoQuery param) {
		StringTools.checkParam(param);
		return this.categoryInfoMapper.deleteByParam(param);
	}

	/**
	 * 根据CategoryId获取对象
	 */
	@Override
	public CategoryInfo getCategoryInfoByCategoryId(Integer categoryId) {
		return this.categoryInfoMapper.selectByCategoryId(categoryId);
	}

	/**
	 * 根据CategoryId修改
	 */
	@Override
	public Integer updateCategoryInfoByCategoryId(CategoryInfo bean, Integer categoryId) {
		return this.categoryInfoMapper.updateByCategoryId(bean, categoryId);
	}

	/**
	 * 根据CategoryId删除
	 */
	@Override
	public Integer deleteCategoryInfoByCategoryId(Integer categoryId) {
		return this.categoryInfoMapper.deleteByCategoryId(categoryId);
	}

	/**
	 * 根据CategoryCode获取对象
	 */
	@Override
	public CategoryInfo getCategoryInfoByCategoryCode(String categoryCode) {
		return this.categoryInfoMapper.selectByCategoryCode(categoryCode);
	}

	/**
	 * 根据CategoryCode修改
	 */
	@Override
	public Integer updateCategoryInfoByCategoryCode(CategoryInfo bean, String categoryCode) {
		return this.categoryInfoMapper.updateByCategoryCode(bean, categoryCode);
	}

	/**
	 * 根据CategoryCode删除
	 */
	@Override
	public Integer deleteCategoryInfoByCategoryCode(String categoryCode) {
		return this.categoryInfoMapper.deleteByCategoryCode(categoryCode);
	}

	@Override
	public void saveCategory(CategoryInfo bean){
		CategoryInfo dbBean = this.categoryInfoMapper.selectByCategoryCode(bean.getCategoryCode());

			if ((dbBean!= null)
					&&(bean.getCategoryId() == null && dbBean.getCategoryId() != null ||
					   bean.getCategoryId() != null && dbBean.getCategoryId() != null && bean.getCategoryId().equals(dbBean.getCategoryId()))) {
				throw new BusinessException("分类编号已存在");
			}
			if (bean.getCategoryId() == null) {
				Integer maxSort = this.categoryInfoMapper.selectMaxSort(bean.getpCategoryId());
				bean.setSort(maxSort + 1);
				this.categoryInfoMapper.insert(bean);
			} else {
				this.categoryInfoMapper.updateByCategoryId(bean, bean.getCategoryId());
			}

	}

	@Override
	public void delCategory(Integer categoryId){
		//TODO 查询分类下是否有视频

		CategoryInfoQuery categoryInfoQuery = new CategoryInfoQuery();
		categoryInfoQuery.setCategoryIdOrPCategoryId(categoryId);
		categoryInfoMapper.deleteByParam(categoryInfoQuery);

		//TODO 刷新缓存

	}

	@Override
	public void changeSort(Integer pCategoryId,String categoryIds){
		String[] categoryIdArray = categoryIds.split(",");
		List<CategoryInfo> categoryInfoList = new ArrayList<>();
		Integer sort = 0;
		for(String categoryId:categoryIdArray){
			CategoryInfo categoryInfo =new CategoryInfo();
			categoryInfo.setpCategoryId(Integer.parseInt(categoryId));
			categoryInfo.setpCategoryId(pCategoryId);
			categoryInfo.setSort(++sort);
			categoryInfoList.add(categoryInfo);
		}
		categoryInfoMapper.updateSortBatch(categoryInfoList);

	}
}