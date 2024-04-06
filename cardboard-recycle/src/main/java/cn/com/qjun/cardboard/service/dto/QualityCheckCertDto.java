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
import me.zhengjie.service.dto.LocalStorageDto;

import java.sql.Timestamp;
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author RenQiang
* @date 2022-06-18
**/
@Data
public class QualityCheckCertDto implements Serializable {

    /** 质检单号 */
    private Integer id;

    /** 所属出库单明细 */
    private Integer stockOutOrderItemId;

    /** 毛重 */
    private Double grossWeight;

    /** 皮重 */
    private Double tareWeight;

    /** 净重 */
    private Double netWeight;

    /** 扣重 */
    private Double deductWeight;

    /**
     * 合计重量
     */
    private Double actualWeight;

    /** 水分百分比 */
    private BigDecimal waterPercent;

    /** 杂物百分比 */
    private BigDecimal impurityPercent;

    /** 杂纸百分比 */
    private BigDecimal incidentalPaperPercent;

    /** 综合折率 */
    private BigDecimal totalDeductPercent;

    /** 称重单附件 */
    private LocalStorageDto weighingAttachment;

    /** 质检单附件 */
    private LocalStorageDto attachment;

    /** 备注 */
    private String remark;
}