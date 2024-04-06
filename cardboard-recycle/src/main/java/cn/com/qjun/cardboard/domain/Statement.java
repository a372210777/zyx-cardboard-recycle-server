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
* @website https://el-admin.vip
* @description /
* @author RenQiang
* @date 2022-06-18
**/
@Entity
@Getter
@Setter
@Table(name="biz_statement")
@EntityListeners(AuditingEntityListener.class)
public class Statement implements Serializable {
    private static final long serialVersionUID = -7797740222712444925L;

    @Id
    @Column(name = "`id`")
    @ApiModelProperty(value = "对账单号")
    private String id;

    @Column(name = "`year_`",nullable = false)
    @NotNull
    @ApiModelProperty(value = "对账年份", required = true)
    private Integer year;

    @Column(name = "`month_`",nullable = false)
    @NotNull
    @ApiModelProperty(value = "对账月份", required = true)
    private Integer month;

    @CreatedBy
    @Column(name = "`create_by`",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "对账人", hidden = true)
    private String createBy;

    @Column(name = "`statement_time`",nullable = false)
    @NotNull
    @ApiModelProperty(value = "对账时间", required = true)
    private Timestamp statementTime;

    @CreationTimestamp
    @Column(name = "`create_time`",nullable = false)
    @NotNull
    @ApiModelProperty(value = "创建时间", hidden = true)
    private Timestamp createTime;

    @LastModifiedBy
    @Column(name = "`update_by`")
    @ApiModelProperty(value = "更新人", hidden = true)
    private String updateBy;

    @LastModifiedDate
    @Column(name = "`update_time`")
    @ApiModelProperty(value = "更新时间", hidden = true)
    private Timestamp updateTime;

    @Column(name = "`deleted`",nullable = false)
    @NotNull
    @ApiModelProperty(value = "是否已删除", hidden = true)
    private Integer deleted;

    @ApiModelProperty(value = "对账单明细", required = true)
    @OneToMany(mappedBy = "statement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StatementItem> statementItems;

    public void copy(Statement source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true).setIgnoreProperties("statementItems"));
        this.getStatementItems().clear();
        this.getStatementItems().addAll(source.getStatementItems());
        this.getStatementItems().forEach(item -> item.setStatement(this));
    }
}
