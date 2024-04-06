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
import cn.com.qjun.cardboard.domain.BasicMaterial;
import cn.com.qjun.cardboard.service.BasicMaterialService;
import cn.com.qjun.cardboard.service.dto.BasicMaterialQueryCriteria;
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
@Api(tags = "基础数据：物料管理")
@RequestMapping("/api/basicMaterial")
public class BasicMaterialController {

    private final BasicMaterialService basicMaterialService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('basicMaterial:list')")
    public void exportBasicMaterial(HttpServletResponse response, BasicMaterialQueryCriteria criteria) throws IOException {
        basicMaterialService.download(basicMaterialService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询基础数据：物料")
    @ApiOperation("查询基础数据：物料")
    @PreAuthorize("@el.check('basicMaterial:list')")
    public ResponseEntity<Object> queryBasicMaterial(BasicMaterialQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(basicMaterialService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增基础数据：物料")
    @ApiOperation("新增基础数据：物料")
    @PreAuthorize("@el.check('basicMaterial:add')")
    public ResponseEntity<Object> createBasicMaterial(@Validated @RequestBody BasicMaterial resources){
        return new ResponseEntity<>(basicMaterialService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改基础数据：物料")
    @ApiOperation("修改基础数据：物料")
    @PreAuthorize("@el.check('basicMaterial:edit')")
    public ResponseEntity<Object> updateBasicMaterial(@Validated @RequestBody BasicMaterial resources){
        basicMaterialService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除基础数据：物料")
    @ApiOperation("删除基础数据：物料")
    @PreAuthorize("@el.check('basicMaterial:del')")
    public ResponseEntity<Object> deleteBasicMaterial(@RequestBody Integer[] ids) {
        basicMaterialService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}