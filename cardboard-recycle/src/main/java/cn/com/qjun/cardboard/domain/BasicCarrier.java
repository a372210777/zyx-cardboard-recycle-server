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

import javax.persistence.*;
import javax.validation.constraints.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;
import java.io.Serializable;

/**
 * @author RenQiang
 * @website https://el-admin.vip
 * @description /
 * @date 2022-06-17
 **/
@Entity
@Getter
@Setter
@Table(name = "basic_carrier")
@EntityListeners(AuditingEntityListener.class)
public class BasicCarrier implements Serializable {
    private static final long serialVersionUID = -3212467080213087916L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @ApiModelProperty(value = "承运商编码，修改时必填")
    private Integer id;

    @Column(name = "`name_`", nullable = false)
    @NotBlank
    @ApiModelProperty(value = "承运商名称", required = true)
    private String name;

    @Column(name = "`institution_code`")
    @ApiModelProperty(value = "机构代码")
    private String institutionCode;

    @Column(name = "`address`")
    @ApiModelProperty(value = "承运商地址")
    private String address;

    @Column(name = "`contact`", nullable = false)
    @NotBlank
    @ApiModelProperty(value = "联系人", required = true)
    private String contact;

    @Column(name = "`phone`", nullable = false)
    @NotBlank
    @ApiModelProperty(value = "联系电话", required = true)
    private String phone;

    @Column(name = "`status_`", nullable = false)
    @NotBlank
    @ApiModelProperty(value = "承运商状态，数据字典：carrier_status", required = true)
    private String status;

    @Column(name = "`deleted`", nullable = false)
    @NotNull
    @ApiModelProperty(value = "是否已删除", hidden = true)
    private Integer deleted;

    @CreatedBy
    @Column(name = "`create_by`", nullable = false)
    @NotBlank
    @ApiModelProperty(value = "创建人", hidden = true)
    private String createBy;

    @LastModifiedBy
    @Column(name = "`update_by`")
    @ApiModelProperty(value = "更新人", hidden = true)
    private String updateBy;

    @Column(name = "`create_time`", nullable = false)
    @NotNull
    @CreationTimestamp
    @ApiModelProperty(value = "创建时间", hidden = true)
    private Timestamp createTime;

    @Column(name = "`update_time`")
    @UpdateTimestamp
    @ApiModelProperty(value = "更新时间", hidden = true)
    private Timestamp updateTime;

    public void copy(BasicCarrier source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
