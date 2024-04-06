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

import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author RenQiang
* @date 2022-06-18
**/
@Data
public class StatementItemDto implements Serializable {
    private static final long serialVersionUID = 7733817496345875229L;

    /** 自增主键ID */
    private Integer id;

    /** 对账单 */
    private StatementDto statement;

    /** 物料 */
    private BasicMaterialDto material;

    /** 数量 */
    private Integer quantity;

    /** 采购单价 */
    private BigDecimal purchasePrice;

    /** 合计金额 */
    private BigDecimal totalAmount;

    /** 对账结果 */
    private String statementResult;

    /**
     * 备注
     */
    private String remark;
}