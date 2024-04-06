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

import me.zhengjie.annotation.Query;

/**
 * @author RenQiang
 * @website https://el-admin.vip
 * @date 2022-06-18
 **/
@Data
public class StatementItemQueryCriteria {
    @Query(type = Query.Type.EQUAL, propName = "year", joinName = "statement")
    @ApiModelProperty(value = "对账年份")
    private Integer year;

    @Query(type = Query.Type.EQUAL, propName = "month", joinName = "statement")
    @ApiModelProperty(value = "对账月份")
    private Integer month;

    @Query(type = Query.Type.EQUAL)
    @ApiModelProperty(value = "对账结果")
    private String statementResult;

    @Query(type = Query.Type.EQUAL, propName = "id", joinName = "material")
    @ApiModelProperty(value = "物料ID")
    private Integer materialId;

    @Query(type = Query.Type.EQUAL, propName = "deleted", joinName = "statement")
    @ApiModelProperty(value = "是否已删除", hidden = true)
    private Integer deleted = SystemConstant.DEL_FLAG_0;
}