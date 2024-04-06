/*
*  Copyright 2019-2020 Zheng Jie
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/
package cn.com.qjun.cardboard.service;

import cn.com.qjun.cardboard.domain.StockOutOrder;
import cn.com.qjun.cardboard.service.dto.StockInOrderDto;
import cn.com.qjun.cardboard.service.dto.StockInOrderQueryCriteria;
import cn.com.qjun.cardboard.service.dto.StockOutOrderDto;
import cn.com.qjun.cardboard.service.dto.StockOutOrderQueryCriteria;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务接口
* @author RenQiang
* @date 2022-06-18
**/
public interface StockOutOrderService {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(StockOutOrderQueryCriteria criteria, Pageable pageable);


    /**
     * 根据入库时间查询数据
     * @param criteria 条件参数
     * @return List<StockInOrderDto>
     */
    List<StockOutOrderDto> queryAllSortByStockOutTime(StockOutOrderQueryCriteria criteria);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<StockOutOrderDto>
    */
    List<StockOutOrderDto> queryAll(StockOutOrderQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return StockOutOrderDto
     */
    StockOutOrderDto findById(String id);

    /**
    * 创建
    * @param resources /
    * @return StockOutOrderDto
    */
    StockOutOrderDto create(StockOutOrder resources);

    /**
    * 编辑
    * @param resources /
    */
    void update(StockOutOrder resources);

    /**
    * 多选删除
    * @param ids /
    */
    void deleteAll(String[] ids);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<StockOutOrderDto> all, HttpServletResponse response) throws IOException;

    List<Map<String, Object>> groupingStatistics(LocalDate beginDate, LocalDate endDate);

    List<Map<String, Object>> groupingStatisticsMoney(LocalDate beginDate, LocalDate endDate);

    Map<String, Object> report(String reportType, StockOutOrderQueryCriteria criteria, Integer pageNumber, Integer pageSize);
}