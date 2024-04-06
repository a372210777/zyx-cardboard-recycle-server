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
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.io.Serializable;
import java.util.List;

/**
 * @author RenQiang
 * @website https://el-admin.vip
 * @description /
 * @date 2022-06-18
 **/
@Entity
@Getter
@Setter
@Table(name = "biz_stock_in_order")
@EntityListeners(AuditingEntityListener.class)
public class StockInOrder implements Serializable {
    private static final long serialVersionUID = 4803133084739275119L;

    @Id
    @Column(name = "`id`")
    @NotEmpty
    @ApiModelProperty(value = "入库单号", required = true)
    private String id;

    @JoinColumn(name = "`warehouse_id`", nullable = false)
    @NotNull
    @ApiModelProperty(value = "仓库", required = true)
    @OneToOne
    private BasicWarehouse warehouse;

    @Column(name = "`stock_in_time`", nullable = false)
    @NotNull
    @ApiModelProperty(value = "入库时间", required = true)
    private Timestamp stockInTime;

    @JoinColumn(name = "`supplier_id`", nullable = false)
    @NotNull
    @ApiModelProperty(value = "供应商", required = true)
    @OneToOne
    private BasicSupplier supplier;

    @CreatedBy
    @Column(name = "`create_by`", nullable = false)
    @NotBlank
    @ApiModelProperty(value = "制单人", hidden = true)
    private String createBy;

    @CreationTimestamp
    @Column(name = "`create_time`", nullable = false)
    @NotNull
    @ApiModelProperty(value = "制单时间", hidden = true)
    private Timestamp createTime;

    @LastModifiedBy
    @Column(name = "`update_by`")
    @ApiModelProperty(value = "更新人", hidden = true)
    private String updateBy;

    @LastModifiedDate
    @Column(name = "`update_time`")
    @ApiModelProperty(value = "更新时间", hidden = true)
    private Timestamp updateTime;

    @Column(name = "`deleted`", nullable = false)
    @NotNull
    @ApiModelProperty(value = "是否已删除", hidden = true)
    private Integer deleted;

    @ApiModelProperty(value = "入库单明细")
    @NotEmpty
    @OneToMany(mappedBy = "stockInOrder", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<StockInOrderItem> orderItems;

    public void copy(StockInOrder source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true)
                .setIgnoreProperties("stockInOrderItems"));
        this.getOrderItems().clear();
        this.getOrderItems().addAll(source.getOrderItems());
        this.getOrderItems().forEach(item -> item.setStockInOrder(this));
    }
}
