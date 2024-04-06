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

import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.Getter;
import lombok.Setter;

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
@Getter
@Setter
@Table(name = "biz_waybill")
public class Waybill implements Serializable {
    private static final long serialVersionUID = -1537607144100906524L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @ApiModelProperty(value = "自增主键ID")
    private Integer id;

    @JoinColumn(name = "`stock_out_order_id`", nullable = false)
    @NotBlank
    @ApiModelProperty(value = "出库单")
    @ManyToOne
    private StockOutOrder stockOutOrder;

    @Column(name = "`consignment_vehicles`", nullable = false)
    @NotNull
    @ApiModelProperty(value = "托运车数")
    private Integer consignmentVehicles;

    @Column(name = "`price_per_vehicle`", nullable = false)
    @NotNull
    @ApiModelProperty(value = "每车价格")
    private BigDecimal pricePerVehicle;

    @Column(name = "`remark`")
    @ApiModelProperty(value = "备注")
    private String remark;

    public void copy(Waybill source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
