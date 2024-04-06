package cn.com.qjun.cardboard.rest;

import cn.com.qjun.cardboard.common.echart.MudhChartData;
import cn.com.qjun.cardboard.service.BasicMaterialService;
import cn.com.qjun.cardboard.service.DailyExpenseService;
import cn.com.qjun.cardboard.service.StockInOrderService;
import cn.com.qjun.cardboard.service.StockOutOrderService;
import cn.com.qjun.cardboard.service.dto.*;
import cn.com.qjun.cardboard.utils.ChartUtils;
import cn.com.qjun.cardboard.utils.DateTimeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.utils.FileUtil;
import org.apache.commons.collections4.MapUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author RenQiang
 * @date 2022/6/24
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "报表统计")
@RequestMapping("/api/report")
public class ReportController {
    private final StockInOrderService stockInOrderService;
    private final StockOutOrderService stockOutOrderService;
    private final DailyExpenseService dailyExpenseService;
    private final BasicMaterialService materialService;

    @GetMapping("/stock")
    @Log("出入库统计")
    @ApiOperation("出入库统计")
    @PreAuthorize("@el.check('report:stock')")
    public ResponseEntity<Map<String, Object>> stockReport(@RequestParam(required = false) @ApiParam("仓库ID") Integer warehouseId,
                                                            @RequestParam(required = false) @ApiParam("物料类别") String materialCategory,
                                                            @RequestParam @ApiParam(value = "统计开始日期", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginDate,
                                                            @RequestParam @ApiParam(value = "统计结束日期", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                            @RequestParam(required = false) Integer materialId,
                                                            @RequestParam @ApiParam("订单类型：stockIn-入库 stockOut-出库") String orderType,
                                                            @RequestParam @ApiParam("统计方式：daily-按天 summary-汇总") String reportType,
                                                            @RequestParam(defaultValue = "1") @ApiParam("页号") Integer pageNumber,
                                                            @RequestParam(defaultValue = "20") @ApiParam("分页大小") Integer pageSize) {
        List<Timestamp> duration = Stream.of(Timestamp.valueOf(beginDate.atStartOfDay()), Timestamp.valueOf(endDate.plusDays(1).atStartOfDay().minusSeconds(1)))
                .collect(Collectors.toList());
        if ("stockIn".equals(orderType)) {
            StockInOrderQueryCriteria criteria = new StockInOrderQueryCriteria();
            criteria.setWarehouseId(warehouseId);
            criteria.setMaterialCategory(materialCategory);
            criteria.setStockInTime(duration);
            criteria.setMaterialId(materialId);
            Map<String,Object> pageResult = stockInOrderService.report(reportType, criteria, pageNumber, pageSize);
            return new ResponseEntity<>(pageResult, HttpStatus.OK);
        }
        if ("stockOut".equals(orderType)) {
            StockOutOrderQueryCriteria criteria = new StockOutOrderQueryCriteria();
            criteria.setWarehouseId(warehouseId);
            criteria.setMaterialCategory(materialCategory);
            criteria.setStockOutTime(duration);
            criteria.setMaterialId(materialId);
            Map<String,Object> pageResult = stockOutOrderService.report(reportType, criteria, pageNumber, pageSize);
            return new ResponseEntity<>(pageResult, HttpStatus.OK);
        }
        throw new IllegalArgumentException("参数错误");
    }


    @Log("出入库统计导出")
    @ApiOperation("出入库统计导出")
    @GetMapping(value = "/stock/download")
    @PreAuthorize("@el.check('report:stock')")
    public void exportStockReport(HttpServletResponse response,
                                  @RequestParam(required = false) @ApiParam("仓库ID") Integer warehouseId,
                                  @RequestParam(required = false) @ApiParam("物料类别") String materialCategory,
                                  @RequestParam @ApiParam(value = "统计开始日期", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginDate,
                                  @RequestParam @ApiParam(value = "统计结束日期", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                  @RequestParam(required = false) Integer materialId,
                                  @RequestParam @ApiParam("订单类型：stockIn-入库 stockOut-出库") String orderType,
                                  @RequestParam @ApiParam("统计方式：daily-按天 summary-汇总") String reportType) throws IOException {
        int page = 1, pageSize = 100;
        List<Map<String, Object>> list = new ArrayList<>();
        List temp;
        while (!(temp = (List) stockReport(warehouseId, materialCategory, beginDate, endDate, materialId, orderType, reportType, page++, pageSize).getBody().get("content")).isEmpty()) {
            for (Object o : temp) {
                StockReportDto reportDto = (StockReportDto) o;
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("出入库", reportDto.getOrderType());
                map.put("物料名称", reportDto.getMaterialName());
                map.put("物料类别", reportDto.getMaterialCategory());
                map.put("所属仓库", reportDto.getWarehouseName());
                map.put("数量(Kg)", reportDto.getQuantity());
                map.put("统计时间", reportDto.getDate());
                map.put("金额", reportDto.getMoney());
                list.add(map);
            }
        }
        FileUtil.downloadExcel(list, response);
    }

    @GetMapping("/expense")
    @Log("开销统计")
    @ApiOperation("开销统计")
    @PreAuthorize("@el.check('report:expense')")
    public ResponseEntity<Map<String,Object>> expenseReport(@RequestParam(required = false) @ApiParam("开销种类") String category,
                                                                @RequestParam @ApiParam("统计方式：daily-按天 summary-汇总") String reportType,
                                                                @RequestParam @ApiParam(value = "统计开始日期", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginDate,
                                                                @RequestParam @ApiParam(value = "统计结束日期", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                                @RequestParam(defaultValue = "1") @ApiParam("页号") Integer pageNumber,
                                                                @RequestParam(defaultValue = "20") @ApiParam("分页大小") Integer pageSize) {
        DailyExpenseQueryCriteria criteria = new DailyExpenseQueryCriteria();
        criteria.setDates(Stream.of(Date.valueOf(beginDate), Date.valueOf(endDate)).collect(Collectors.toList()));
        criteria.setCategory(category);
        Map<String,Object> pageResult = dailyExpenseService.report(reportType, criteria, pageNumber, pageSize);
        return new ResponseEntity<>(pageResult, HttpStatus.OK);
    }

    @Log("开销统计导出")
    @ApiOperation("开销统计导出")
    @GetMapping(value = "/expense/download")
    @PreAuthorize("@el.check('report:expense')")
    public void exportExpenseReport(HttpServletResponse response,
                                    @RequestParam(required = false) @ApiParam("开销种类") String category,
                                    @RequestParam @ApiParam("统计方式：daily-按天 summary-汇总") String reportType,
                                    @RequestParam @ApiParam(value = "统计开始日期", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginDate,
                                    @RequestParam @ApiParam(value = "统计结束日期", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) throws IOException {
        int page = 1, pageSize = 100;
        List<Map<String, Object>> list = new ArrayList<>();
        List temp;
        while (!(temp = (List) expenseReport(category, reportType, beginDate, endDate, page++, pageSize).getBody().get("content")).isEmpty()) {
            for (Object o : temp) {
                ExpenseReportDto reportDto = (ExpenseReportDto) o;
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("开销分类", reportDto.getCategory());
                map.put("总金额", reportDto.getMoney());
                map.put("统计日期", reportDto.getDate());
                list.add(map);
            }
        }
        FileUtil.downloadExcel(list, response);
    }

    @Log("入库统计图")
    @ApiOperation("入库统计图")
    @GetMapping(value = "/chart/stockIn")
    @PreAuthorize("@el.check('report:chart:stockIn')")
    public ResponseEntity<MudhChartData> stockInChart(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "统计开始日期", required = true) LocalDate beginDate,
                                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "统计结束日期", required = true) LocalDate endDate) {
        List<Map<String, Object>> dataList = stockInOrderService.groupingStatistics(beginDate, endDate.plusDays(1));
        return new ResponseEntity<>(convertMapToChartData(dataList, beginDate, endDate), HttpStatus.OK);
    }

    @Log("出库统计图")
    @ApiOperation("出库统计图")
    @GetMapping(value = "/chart/stockOut")
    @PreAuthorize("@el.check('report:chart:stockOut')")
    public ResponseEntity<MudhChartData> stockOutChart(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "统计开始日期", required = true) LocalDate beginDate,
                                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "统计结束日期", required = true) LocalDate endDate) {
        List<Map<String, Object>> dataList = stockOutOrderService.groupingStatistics(beginDate, endDate.plusDays(1));
        return new ResponseEntity<>(convertMapToChartData(dataList, beginDate, endDate), HttpStatus.OK);
    }

    @Log("出库金额统计图")
    @ApiOperation("出库金额统计图")
    @GetMapping(value = "/chart/stockOutMoney")
    @PreAuthorize("@el.check('report:chart:stockOutMoney')")
    public ResponseEntity<MudhChartData> stockOutMoneyChart(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "统计开始日期", required = true) LocalDate beginDate,
                                                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "统计结束日期", required = true) LocalDate endDate) {
        List<Map<String, Object>> dataList = stockOutOrderService.groupingStatisticsMoney(beginDate, endDate.plusDays(1));
        return new ResponseEntity<>(convertMapToChartData(dataList, beginDate, endDate), HttpStatus.OK);
    }

    private MudhChartData<LocalDate, String, BigDecimal> convertMapToChartData(List<Map<String, Object>> dataList, LocalDate beginDate, LocalDate endDate) {
        List<BasicMaterialDto> allMaterial = materialService.queryAll(new BasicMaterialQueryCriteria());
        String[] materials = allMaterial.stream()
                .map(BasicMaterialDto::getName)
                .toArray(String[]::new);
        Map<LocalDate, Map<String, Object>> collect = dataList.stream()
                .collect(Collectors.groupingBy(map -> ((Date) map.get("date_")).toLocalDate(),
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            Map<String, Object> map = new HashMap<>();
                            for (Map<String, Object> item : list) {
                                map.put((String) item.get("material"),
                                        Optional.ofNullable(item.get("quantity"))
                                                .map(obj -> new BigDecimal(obj.toString()))
                                                .orElse(null));
                            }
                            return map;
                        })));
        List<LocalDate> dateList = DateTimeUtils.generateDateSequence(beginDate, endDate.plusDays(1), Period.ofDays(1));
        List<Map<String, Object>> chartDataList = dateList.stream()
                .map(date -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("date", date);
                    if (MapUtils.isNotEmpty(collect.get(date))) {
                        data.putAll(collect.get(date));
                    }
                    for (String material : materials) {
                        data.putIfAbsent(material, BigDecimal.ZERO);
                    }
                    return data;
                })
                .collect(Collectors.toList());
        Function<Map<String, Object>, BigDecimal>[] yConverters = Arrays.stream(materials)
                .map(material -> (Function<Map<String, Object>, BigDecimal>) map -> (BigDecimal) map.get(material))
                .toArray(Function[]::new);
        return ChartUtils.buildMudhFromDataList(chartDataList, map -> (LocalDate) map.get("date"), materials, yConverters);
    }
}
