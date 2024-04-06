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
import cn.com.qjun.cardboard.domain.InventoryList;
import cn.com.qjun.cardboard.service.InventoryListService;
import cn.com.qjun.cardboard.service.dto.InventoryListQueryCriteria;
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
* @date 2022-06-18
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "库存管理")
@RequestMapping("/api/inventoryList")
public class InventoryListController {

    private final InventoryListService inventoryListService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('inventoryList:list')")
    public void exportInventoryList(HttpServletResponse response, InventoryListQueryCriteria criteria) throws IOException {
        inventoryListService.download(inventoryListService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询库存")
    @ApiOperation("查询库存")
    @PreAuthorize("@el.check('inventoryList:list')")
    public ResponseEntity<Object> queryInventoryList(InventoryListQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(inventoryListService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增库存")
    @ApiOperation("新增库存")
    @PreAuthorize("@el.check('inventoryList:add')")
    public ResponseEntity<Object> createInventoryList(@Validated @RequestBody InventoryList resources){
        return new ResponseEntity<>(inventoryListService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改库存")
    @ApiOperation("修改库存")
    @PreAuthorize("@el.check('inventoryList:edit')")
    public ResponseEntity<Object> updateInventoryList(@Validated @RequestBody InventoryList resources){
        inventoryListService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除库存")
    @ApiOperation("删除库存")
    @PreAuthorize("@el.check('inventoryList:del')")
    public ResponseEntity<Object> deleteInventoryList(@RequestBody Integer[] ids) {
        inventoryListService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}