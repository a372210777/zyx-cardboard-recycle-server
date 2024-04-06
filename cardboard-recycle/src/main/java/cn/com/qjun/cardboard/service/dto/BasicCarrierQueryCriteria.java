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

import java.util.List;

import me.zhengjie.annotation.Query;

/**
 * @author RenQiang
 * @website https://el-admin.vip
 * @date 2022-06-17
 **/
@Data
public class BasicCarrierQueryCriteria {

    /**
     * 模糊
     */
    @Query(type = Query.Type.INNER_LIKE)
    @ApiModelProperty(value = "承运商名称，模糊查询")
    private String name;

    /**
     * 精确
     */
    @Query
    @ApiModelProperty(value = "机构代码，精确查询")
    private String institutionCode;

    @Query
    @ApiModelProperty(value = "是否已删除", hidden = true)
    private Integer deleted = SystemConstant.DEL_FLAG_0;
}