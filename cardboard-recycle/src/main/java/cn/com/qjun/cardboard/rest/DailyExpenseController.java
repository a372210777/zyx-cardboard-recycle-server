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
import cn.com.qjun.cardboard.domain.DailyExpense;
import cn.com.qjun.cardboard.service.DailyExpenseService;
import cn.com.qjun.cardboard.service.dto.DailyExpenseQueryCriteria;
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
* @date 2022-06-25
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "日常开销管理")
@RequestMapping("/api/dailyExpense")
public class DailyExpenseController {

    private final DailyExpenseService dailyExpenseService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('dailyExpense:list')")
    public void exportDailyExpense(HttpServletResponse response, DailyExpenseQueryCriteria criteria) throws IOException {
        dailyExpenseService.download(dailyExpenseService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询日常开销")
    @ApiOperation("查询日常开销")
    @PreAuthorize("@el.check('dailyExpense:list')")
    public ResponseEntity<Object> queryDailyExpense(DailyExpenseQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(dailyExpenseService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增日常开销")
    @ApiOperation("新增日常开销")
    @PreAuthorize("@el.check('dailyExpense:add')")
    public ResponseEntity<Object> createDailyExpense(@Validated @RequestBody DailyExpense resources){
        return new ResponseEntity<>(dailyExpenseService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改日常开销")
    @ApiOperation("修改日常开销")
    @PreAuthorize("@el.check('dailyExpense:edit')")
    public ResponseEntity<Object> updateDailyExpense(@Validated @RequestBody DailyExpense resources){
        dailyExpenseService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除日常开销")
    @ApiOperation("删除日常开销")
    @PreAuthorize("@el.check('dailyExpense:del')")
    public ResponseEntity<Object> deleteDailyExpense(@RequestBody Integer[] ids) {
        dailyExpenseService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}