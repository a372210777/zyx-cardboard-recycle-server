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
import cn.com.qjun.cardboard.domain.BasicBuyer;
import cn.com.qjun.cardboard.service.BasicBuyerService;
import cn.com.qjun.cardboard.service.dto.BasicBuyerQueryCriteria;
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
@Api(tags = "基础数据：采购商管理")
@RequestMapping("/api/basicBuyer")
public class BasicBuyerController {

    private final BasicBuyerService basicBuyerService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('basicBuyer:list')")
    public void exportBasicBuyer(HttpServletResponse response, BasicBuyerQueryCriteria criteria) throws IOException {
        basicBuyerService.download(basicBuyerService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询基础数据：采购商")
    @ApiOperation("查询基础数据：采购商")
    @PreAuthorize("@el.check('basicBuyer:list')")
    public ResponseEntity<Object> queryBasicBuyer(BasicBuyerQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(basicBuyerService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增基础数据：采购商")
    @ApiOperation("新增基础数据：采购商")
    @PreAuthorize("@el.check('basicBuyer:add')")
    public ResponseEntity<Object> createBasicBuyer(@Validated @RequestBody BasicBuyer resources){
        return new ResponseEntity<>(basicBuyerService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改基础数据：采购商")
    @ApiOperation("修改基础数据：采购商")
    @PreAuthorize("@el.check('basicBuyer:edit')")
    public ResponseEntity<Object> updateBasicBuyer(@Validated @RequestBody BasicBuyer resources){
        basicBuyerService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除基础数据：采购商")
    @ApiOperation("删除基础数据：采购商")
    @PreAuthorize("@el.check('basicBuyer:del')")
    public ResponseEntity<Object> deleteBasicBuyer(@RequestBody Integer[] ids) {
        basicBuyerService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}