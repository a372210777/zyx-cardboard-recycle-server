package cn.com.qjun.cardboard.rest;

import cn.com.qjun.cardboard.service.StockInOrderService;
import cn.com.qjun.cardboard.service.StockManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequiredArgsConstructor
@Api(tags="库存查询")
@RequestMapping("api/stock")
public class stockController {

    private final StockManageService stockManageService;
    @GetMapping("/queryStock")
    @Log("库存查询")
    @ApiOperation("查询当前仓库某一类物料的库存量")
    public ResponseEntity<String> queryStock(@RequestParam(required = true) @ApiParam("仓库ID") Integer warehouseId,
                                             @RequestParam(required = false) @ApiParam("物料ID") Integer materialId,
                                             @RequestParam(required = true) @ApiParam("统计日期") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        System.out.println("接收参数：");
        System.out.println(warehouseId);
        System.out.println(materialId);
        System.out.println(date);



        if (warehouseId == null) {
            System.out.print("warehouseId is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("请选择仓库");
        }
        if (date == null) {
            System.out.print("date is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("请选择日期");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 使用format方法将LocalDate对象转换为字符串
        String formattedDate = date.format(formatter);
        System.out.println(formattedDate);
        System.out.println(date.toString());
//        stockManageService.queryAll(warehouseId,materialId,formattedDate);
        return null;
    }
}