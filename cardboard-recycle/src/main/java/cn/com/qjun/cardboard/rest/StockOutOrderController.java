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

import cn.com.qjun.cardboard.domain.StockOutOrderItem;
import cn.com.qjun.cardboard.utils.SerialNumberGenerator;
import me.zhengjie.annotation.Log;
import cn.com.qjun.cardboard.domain.StockOutOrder;
import cn.com.qjun.cardboard.service.StockOutOrderService;
import cn.com.qjun.cardboard.service.dto.StockOutOrderQueryCriteria;
import me.zhengjie.exception.BadRequestException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import java.io.IOException;
import java.time.LocalDate;
import javax.servlet.http.HttpServletResponse;

/**
 * @author RenQiang
 * @website https://el-admin.vip
 * @date 2022-06-18
 **/
@RestController
@RequiredArgsConstructor
@Api(tags = "出库单管理")
@RequestMapping("/api/stockOutOrder")
public class StockOutOrderController {

    private final StockOutOrderService stockOutOrderService;
    private final SerialNumberGenerator serialNumberGenerator;

    @GetMapping("/genId")
    @Log("生成出库单号")
    @ApiOperation("生成出库单号")
    @PreAuthorize("@el.check('stockOutOrder:add')")
    public ResponseEntity<String> generateOrderId(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return new ResponseEntity<>(serialNumberGenerator.generateStockOutOrderId(date), HttpStatus.OK);
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('stockOutOrder:list')")
    public void exportStockOutOrder(HttpServletResponse response, StockOutOrderQueryCriteria criteria) throws IOException {

        stockOutOrderService.download(stockOutOrderService.queryAllSortByStockOutTime(criteria), response);
    }

    @GetMapping
    @Log("查询出库单")
    @ApiOperation("查询出库单")
    @PreAuthorize("@el.check('stockOutOrder:list')")
    public ResponseEntity<Object> queryStockOutOrder(StockOutOrderQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(stockOutOrderService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @PostMapping
    @Log("新增出库单")
    @ApiOperation("新增出库单")
    @PreAuthorize("@el.check('stockOutOrder:add')")
    public ResponseEntity<Object> createStockOutOrder(@Validated @RequestBody StockOutOrder resources) {
        if (CollectionUtils.isEmpty(resources.getOrderItems())) {
            throw new BadRequestException("出库单明细不能为空");
        }
        boolean hasPaper = false;
        for (StockOutOrderItem orderItem : resources.getOrderItems()) {
            if ("paper".equals(orderItem.getMaterial().getCategory())) {
                hasPaper = true;
                if (CollectionUtils.isEmpty(orderItem.getQualityCheckCerts())) {
                    throw new BadRequestException("纸类物料出库单质检单必填");
                }
            }
        }
        if (hasPaper) {
            if (CollectionUtils.isEmpty(resources.getWaybills())) {
                throw new BadRequestException("纸类物料出库单托运单必填");
            }
        }
        return new ResponseEntity<>(stockOutOrderService.create(resources), HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改出库单")
    @ApiOperation("修改出库单")
    @PreAuthorize("@el.check('stockOutOrder:edit')")
    public ResponseEntity<Object> updateStockOutOrder(@Validated @RequestBody StockOutOrder resources) {
        stockOutOrderService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除出库单")
    @ApiOperation("删除出库单")
    @PreAuthorize("@el.check('stockOutOrder:del')")
    public ResponseEntity<Object> deleteStockOutOrder(@RequestBody String[] ids) {
        stockOutOrderService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}