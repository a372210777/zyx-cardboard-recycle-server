package cn.com.qjun.cardboard.service.impl;

import cn.com.qjun.cardboard.repository.StockInOrderItemRepository;
import cn.com.qjun.cardboard.repository.StockInOrderRepository;
import cn.com.qjun.cardboard.repository.StockOutOrderItemRepository;
import cn.com.qjun.cardboard.repository.StockOutOrderRepository;
import cn.com.qjun.cardboard.service.InventoryService;
import cn.com.qjun.cardboard.service.StockInOrderService;
import cn.com.qjun.cardboard.service.StockOutOrderService;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Resource
    private StockInOrderItemRepository stockInOrderItemRepository;
    @Resource
    private StockOutOrderItemRepository stockOutOrderItemRepository;

    @Override
    public Double query(String stockId, String materialId, String dateTime) {
        String inData = this.stockInOrderItemRepository.queryStockInData(Integer.valueOf(stockId), Integer.valueOf(materialId), LocalDateTimeUtil.parse(dateTime,"yyyy-MM-dd HH:mm:ss"));
        String outData = this.stockOutOrderItemRepository.queryStockOutData(Integer.valueOf(stockId), Integer.valueOf(materialId), LocalDateTimeUtil.parse(dateTime,"yyyy-MM-dd HH:mm:ss"));
        Double stockInData = Double.valueOf(StrUtil.isEmpty(inData)?"0":inData);
        Double stockOutData = Double.valueOf(StrUtil.isEmpty(outData)?"0":outData);
        return stockInData.doubleValue() - stockOutData.doubleValue();
    }
}
