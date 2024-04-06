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

import cn.com.qjun.cardboard.domain.StatementItem;
import cn.com.qjun.cardboard.service.StatementItemService;
import cn.com.qjun.cardboard.service.StockInOrderService;
import cn.com.qjun.cardboard.service.dto.*;
import me.zhengjie.annotation.Log;
import cn.com.qjun.cardboard.domain.Statement;
import cn.com.qjun.cardboard.service.StatementService;
import me.zhengjie.exception.BadRequestException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author RenQiang
* @date 2022-06-18
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "对账单管理")
@RequestMapping("/api/statement")
public class StatementController {

    private final StatementService statementService;
    private final StockInOrderService stockInOrderService;
    private final StatementItemService statementItemService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('statement:list')")
    public void exportStatement(HttpServletResponse response, StatementItemQueryCriteria criteria) throws IOException {
        statementItemService.download(statementItemService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询对账单")
    @ApiOperation("查询对账单")
    @PreAuthorize("@el.check('statement:list')")
    public ResponseEntity<Object> queryStatement(StatementItemQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(statementItemService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增对账单")
    @ApiOperation(value = "新增对账单", notes = "如果当月已存在对账单，则更新")
    @PreAuthorize("@el.check('statement:add')")
    public ResponseEntity<Object> createStatement(@Validated @RequestBody Statement resources){
        if (CollectionUtils.isEmpty(resources.getStatementItems())) {
            throw new BadRequestException("对账单明细不能为空");
        }
        return new ResponseEntity<>(statementService.create(resources),HttpStatus.CREATED);
    }

    @GetMapping("/add")
    @Log("新增对账单查询统计数据")
    @ApiOperation("新增对账单查询统计数据")
    @PreAuthorize("@el.check('statement:add')")
    public ResponseEntity<Object> queryForAdd(@RequestParam @ApiParam(value = "年份", required = true) Integer year,
                                              @RequestParam @ApiParam(value = "月份", required = true) Integer month){
        StockInOrderQueryCriteria criteria = new StockInOrderQueryCriteria();
        LocalDateTime beginTime = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime endTime = beginTime.plusMonths(1).minusSeconds(1);
        criteria.setStockInTime(Stream.of(Timestamp.valueOf(beginTime), Timestamp.valueOf(endTime))
                .collect(Collectors.toList()));
        List<StockInOrderDto> stockInOrderDtos = stockInOrderService.queryAll(criteria);
        Map<BasicMaterialDto, Integer> materialQuantityMap = stockInOrderDtos.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .collect(Collectors.groupingBy(StockInOrderItemDto::getMaterial,
                        Collectors.mapping(StockInOrderItemDto::getQuantity,
                                Collectors.summingInt(quantity -> quantity))));
        List<StatementItemDto> statementItemDtos = materialQuantityMap.entrySet().stream()
                .map(entry -> {
                    StatementItemDto statementItemDto = new StatementItemDto();
                    statementItemDto.setMaterial(entry.getKey());
                    statementItemDto.setQuantity(entry.getValue());
                    return statementItemDto;
                })
                .collect(Collectors.toList());
        return new ResponseEntity<>(statementItemDtos,HttpStatus.OK);
    }

    @PutMapping
    @Log("修改对账单")
    @ApiOperation("修改对账单")
    @PreAuthorize("@el.check('statement:edit')")
    public ResponseEntity<Object> updateStatement(@Validated @RequestBody Statement resources){
        statementService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/item")
    @Log("修改对账单行项")
    @ApiOperation("修改对账单行项")
    @PreAuthorize("@el.check('statement:edit')")
    public ResponseEntity<Object> updateStatementItem(@Validated @RequestBody StatementItem resources){
        statementItemService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/item")
    @Log("删除对账单行项")
    @ApiOperation("删除对账单行项")
    @PreAuthorize("@el.check('statement:del')")
    public ResponseEntity<Object> deleteStatementItem(@RequestBody Integer[] ids){
        statementItemService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除对账单")
    @ApiOperation("删除对账单")
    @PreAuthorize("@el.check('statement:del')")
    public ResponseEntity<Object> deleteStatement(@RequestBody String[] ids) {
        statementService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}