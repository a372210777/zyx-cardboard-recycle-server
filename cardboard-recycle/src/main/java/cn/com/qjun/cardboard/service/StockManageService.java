package cn.com.qjun.cardboard.service;


import cn.com.qjun.cardboard.service.dto.StockInOrderDto;
import cn.com.qjun.cardboard.service.dto.StockInOrderQueryCriteria;

import java.util.List;


/**
 * @website https://el-admin.vip
 * @description 服务接口
 * @author RenQiang
 * @date 2022-06-18
 **/
public interface StockManageService {

    /**
     * 查询所有数据不分页
     * @param criteria 条件参数
     * @return List<StockInOrderDto>
     */
    List<StockInOrderDto> queryAll(Integer warehouseId,Integer materialId,String date);
}