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
import java.util.List;

/**
 * @author RenQiang
 * @website https://el-admin.vip
 * @description /
 * @date 2022-06-18
 **/
@Entity
@Data
@Table(name = "biz_stock_out_order_item")
public class StockOutOrderItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @ApiModelProperty(value = "自增主键ID", hidden = true)
    private Integer id;

    @JoinColumn(name = "`stock_out_order_id`", nullable = false)
    @NotBlank
    @ApiModelProperty(value = "所属出库单")
    @ManyToOne
    private StockOutOrder stockOutOrder;

    @JoinColumn(name = "`material_id`", nullable = false)
    @NotNull
    @ApiModelProperty(value = "物料")
    @OneToOne
    private BasicMaterial material;

    @Column(name = "`quantity`", nullable = false)
    @NotNull
    @ApiModelProperty(value = "出库数量")
    private Integer quantity;

    @Column(name = "`unit`", nullable = false)
    @NotBlank
    @ApiModelProperty(value = "数量单位")
    private String unit;

    @Column(name = "`unit_price`", nullable = false)
    @NotNull
    @ApiModelProperty(value = "单价")
    private BigDecimal unitPrice;

    @Column(name = "`remark`")
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "质检单")
    @OneToMany(mappedBy = "stockOutOrderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QualityCheckCert> qualityCheckCerts;

    public void copy(StockOutOrderItem source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
