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
import me.zhengjie.domain.LocalStorage;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
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
@Table(name = "biz_quality_check_cert")
public class QualityCheckCert implements Serializable {
    private static final long serialVersionUID = 2659205448502508947L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @ApiModelProperty(value = "自增主键ID")
    private Integer id;

    @JoinColumn(name = "`stock_out_order_item_id`", nullable = false)
    @NotNull
    @ApiModelProperty(value = "所属出库单明细", required = true)
    @ManyToOne
    private StockOutOrderItem stockOutOrderItem;

    @Column(name = "`gross_weight`", nullable = false)
    @NotNull
    @ApiModelProperty(value = "毛重", required = true)
    private Double grossWeight;

    @Column(name = "`tare_weight`", nullable = false)
    @NotNull
    @ApiModelProperty(value = "皮重", required = true)
    private Double tareWeight;

    @Column(name = "`net_weight`", nullable = false)
    @NotNull
    @ApiModelProperty(value = "净重", required = true)
    private Double netWeight;

    @Column(name = "`deduct_weight`", nullable = false)
    @NotNull
    @ApiModelProperty(value = "扣重", required = true)
    private Double deductWeight;

    @Column(name = "`actual_weight`", nullable = false)
    @NotNull
    @ApiModelProperty(value = "合计重量", required = true)
    private Double actualWeight;

    @Column(name = "`water_percent`", nullable = false)
    @NotNull
    @ApiModelProperty(value = "水分百分比", required = true)
    private BigDecimal waterPercent;

    @Column(name = "`impurity_percent`", nullable = false)
    @NotNull
    @ApiModelProperty(value = "杂物百分比", required = true)
    private BigDecimal impurityPercent;

    @Column(name = "`incidental_paper_percent`", nullable = false)
    @NotNull
    @ApiModelProperty(value = "杂纸百分比", required = true)
    private BigDecimal incidentalPaperPercent;

    @Column(name = "`total_deduct_percent`", nullable = false)
    @NotNull
    @ApiModelProperty(value = "综合折率", required = true)
    private BigDecimal totalDeductPercent;

    @JoinColumn(name = "`weighing_attachment`")
    @ApiModelProperty(value = "称重单附件")
    @OneToOne
    private LocalStorage weighingAttachment;

    @JoinColumn(name = "`attachment`")
    @ApiModelProperty(value = "质检单附件")
    @OneToOne
    private LocalStorage attachment;

    @Column(name = "`remark`")
    @ApiModelProperty(value = "备注")
    private String remark;

    public void copy(QualityCheckCert source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
