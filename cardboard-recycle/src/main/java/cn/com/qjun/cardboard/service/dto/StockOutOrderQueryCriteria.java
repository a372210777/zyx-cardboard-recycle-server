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
package cn.com.qjun.cardboard.service.dto;

import cn.com.qjun.cardboard.common.SystemConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import me.zhengjie.annotation.Query;

/**
* @website https://el-admin.vip
* @author RenQiang
* @date 2022-06-18
**/
@Data
public class StockOutOrderQueryCriteria{
    @Query(type = Query.Type.EQUAL, propName = "id", joinName = "orderItems>material")
    @ApiModelProperty(value = "物料ID，精确匹配")
    private Integer materialId;

    @Query(type = Query.Type.EQUAL, propName = "category", joinName = "orderItems>material")
    @ApiModelProperty(value = "物料类别，精确查询")
    private String materialCategory;

    @Query(type = Query.Type.BETWEEN)
    @ApiModelProperty(value = "出库时间区间，between [0] and [1]", dataType = "String")
    private List<Timestamp> stockOutTime;

    @Query(type = Query.Type.EQUAL)
    @ApiModelProperty(value = "出库单号，精确匹配")
    private String id;

    @Query(type = Query.Type.EQUAL, propName = "id", joinName = "warehouse")
    @ApiModelProperty(value = "仓库ID，精确匹配")
    private Integer warehouseId;

    @Query
    @ApiModelProperty(value = "是否已删除", hidden = true)
    private Integer deleted = SystemConstant.DEL_FLAG_0;
}