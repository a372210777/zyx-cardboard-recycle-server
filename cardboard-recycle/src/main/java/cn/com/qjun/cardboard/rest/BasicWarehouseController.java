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
package cn.com.qjun.cardboard.rest;

import me.zhengjie.annotation.Log;
import cn.com.qjun.cardboard.domain.BasicWarehouse;
import cn.com.qjun.cardboard.service.BasicWarehouseService;
import cn.com.qjun.cardboard.service.dto.BasicWarehouseQueryCriteria;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author RenQiang
* @date 2022-06-17
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "基础数据：仓库管理")
@RequestMapping("/api/basicWarehouse")
public class BasicWarehouseController {

    private final BasicWarehouseService basicWarehouseService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('basicWarehouse:list')")
    public void exportBasicWarehouse(HttpServletResponse response, BasicWarehouseQueryCriteria criteria) throws IOException {
        basicWarehouseService.download(basicWarehouseService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询基础数据：仓库")
    @ApiOperation("查询基础数据：仓库")
    @PreAuthorize("@el.check('basicWarehouse:list')")
    public ResponseEntity<Object> queryBasicWarehouse(BasicWarehouseQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(basicWarehouseService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增基础数据：仓库")
    @ApiOperation("新增基础数据：仓库")
    @PreAuthorize("@el.check('basicWarehouse:add')")
    public ResponseEntity<Object> createBasicWarehouse(@Validated @RequestBody BasicWarehouse resources){
        return new ResponseEntity<>(basicWarehouseService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改基础数据：仓库")
    @ApiOperation("修改基础数据：仓库")
    @PreAuthorize("@el.check('basicWarehouse:edit')")
    public ResponseEntity<Object> updateBasicWarehouse(@Validated @RequestBody BasicWarehouse resources){
        basicWarehouseService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除基础数据：仓库")
    @ApiOperation("删除基础数据：仓库")
    @PreAuthorize("@el.check('basicWarehouse:del')")
    public ResponseEntity<Object> deleteBasicWarehouse(@RequestBody Integer[] ids) {
        basicWarehouseService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}