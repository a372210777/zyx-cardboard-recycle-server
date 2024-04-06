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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author RenQiang
 * @website https://el-admin.vip
 * @description /
 * @date 2022-06-18
 **/
@Entity
@Data
@Table(name = "biz_stock_in_order_item")
public class StockInOrderItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @ApiModelProperty(value = "主键ID")
    private Integer id;

    @JoinColumn(name = "stock_in_order_id", nullable = false)
    @ManyToOne
    @ApiModelProperty(value = "所属入库单")
    private StockInOrder stockInOrder;

    @JoinColumn(name = "`material_id`", nullable = false)
    @NotNull
    @ApiModelProperty(value = "物料", required = true)
    @OneToOne
    private BasicMaterial material;

    @Column(name = "`quantity`", nullable = false)
    @NotNull
    @ApiModelProperty(value = "入库数量", required = true)
    private Integer quantity;

    @Column(name = "`unit`", nullable = false)
    @NotBlank
    @ApiModelProperty(value = "数量单位", required = true)
    private String unit;

    @Column(name = "`unit_price`", nullable = false)
    @NotNull
    @ApiModelProperty(value = "采购单价", required = true)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(name = "`remark`")
    @ApiModelProperty(value = "备注")
    private String remark;

    public void copy(StockInOrderItem source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
