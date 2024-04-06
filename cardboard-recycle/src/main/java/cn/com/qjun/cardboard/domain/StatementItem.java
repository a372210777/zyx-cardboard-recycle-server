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
package cn.com.qjun.cardboard.domain;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.io.Serializable;

/**
 * @author RenQiang
 * @website https://el-admin.vip
 * @description /
 * @date 2022-06-18
 **/
@Entity
@Data
@Table(name = "biz_statement_item")
public class StatementItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @ApiModelProperty(value = "自增主键ID")
    private Integer id;

    @JoinColumn(name = "`statement_id`", nullable = false)
    @ManyToOne
    @ApiModelProperty(value = "所属对账单")
    @NotNull
    private Statement statement;

    @JoinColumn(name = "`material_id`", nullable = false)
    @NotNull
    @ApiModelProperty(value = "物料", required = true)
    @OneToOne
    private BasicMaterial material;

    @Column(name = "`quantity`", nullable = false)
    @NotNull
    @ApiModelProperty(value = "数量", required = true)
    private Integer quantity;

    @Column(name = "`purchase_price`", nullable = false)
    @NotNull
    @ApiModelProperty(value = "采购单价", required = true)
    private BigDecimal purchasePrice;

    @Column(name = "`total_amount`", nullable = false)
    @NotNull
    @ApiModelProperty(value = "合计金额", required = true)
    private BigDecimal totalAmount;

    @Column(name = "`statement_result`", nullable = false)
    @NotBlank
    @ApiModelProperty(value = "对账结果，数据字典：statement_result", required = true)
    private String statementResult;

    @Column(name = "`remark`")
    @ApiModelProperty(value = "备注")
    private String remark;

    public void copy(StatementItem source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
