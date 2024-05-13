package cn.com.qjun.cardboard.rest;


import cn.com.qjun.cardboard.service.BasicMaterialService;
import cn.com.qjun.cardboard.service.BasicWarehouseService;
import cn.com.qjun.cardboard.service.InventoryService;
import cn.com.qjun.cardboard.service.StockManageService;
import cn.com.qjun.cardboard.service.dto.BasicMaterialDto;
import cn.com.qjun.cardboard.service.dto.BasicWarehouseDto;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.exception.BadRequestException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Api(tags = "库存管理")
@RequestMapping("/api/inventory")
public class InventoryController {

    @Resource
    private InventoryService inventoryService;
    @Resource
    private BasicMaterialService materialService;
    @Resource
    private BasicWarehouseService basicWarehouseService;

    @ApiOperation(value = "库存查询")
    @GetMapping(value = "query")
    public ResponseEntity<Map<String, Object>> query(String stockId, String materialId, String dateTime) {
        if (StrUtil.isEmpty(stockId)) {
            throw new BadRequestException("仓库ID不能为空");
        }
        if (StrUtil.isEmpty(materialId)) {
            throw new BadRequestException("物料ID不能为空");
        }
        if (StrUtil.isEmpty(dateTime)) {
            throw new BadRequestException("统计日期不能为空");
        }
        Double result = this.inventoryService.query(stockId, materialId, dateTime);
        BasicMaterialDto materialEntity = materialService.findById(Integer.valueOf(materialId));
        BasicWarehouseDto wareHouseEntity = this.basicWarehouseService.findById(Integer.valueOf(stockId));
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("quantity", result);
        resultMap.put("stockId", stockId);
        resultMap.put("materialId", materialId);
        resultMap.put("dateTime", dateTime);
        resultMap.put("materialName", ObjectUtil.isNotEmpty(materialEntity) ? materialEntity.getName() : "");
        resultMap.put("stockName", ObjectUtil.isNotEmpty(wareHouseEntity) ? wareHouseEntity.getName() : "");
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }
}
