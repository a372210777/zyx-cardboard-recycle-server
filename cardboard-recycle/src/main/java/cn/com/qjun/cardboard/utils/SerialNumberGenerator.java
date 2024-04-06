package cn.com.qjun.cardboard.utils;

import cn.com.qjun.cardboard.repository.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.utils.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author RenQiang
 * @date 2022/6/21
 */
@Component
@RequiredArgsConstructor
public class SerialNumberGenerator {
    /**
     * 流水号前缀
     */
    private static final String SERIAL_NUMBER_PREFIX_STOCK_IN_ORDER = "IO",
            SERIAL_NUMBER_PREFIX_STOCK_OUT_ORDER = "OO",
            SERIAL_NUMBER_PREFIX_STATEMENT = "BO";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final StockInOrderRepository stockInOrderRepository;
    private final StockOutOrderRepository stockOutOrderRepository;
    private final StatementRepository statementRepository;

    public String generateStockInOrderId(LocalDate date) {
        String maxId = stockInOrderRepository.getMaxIdByStockInDate(date);
        return generateSerialNumber(SERIAL_NUMBER_PREFIX_STOCK_IN_ORDER, date, maxId);
    }

    public String generateStockOutOrderId(LocalDate date) {
        String maxId = stockOutOrderRepository.getMaxIdByStockInDate(date);
        return generateSerialNumber(SERIAL_NUMBER_PREFIX_STOCK_OUT_ORDER, date, maxId);
    }

    public String generateStatementId(LocalDate date) {
        String maxId = statementRepository.getMaxIdByStatementDate(date);
        return generateSerialNumber(SERIAL_NUMBER_PREFIX_STATEMENT, date, maxId);
    }

    private String generateSerialNumber(String prefix, LocalDate date, String currentMaxId) {
        int serialNumber;
        if (StringUtils.isEmpty(currentMaxId)) {
            serialNumber = 1;
        } else {
            serialNumber = Integer.parseInt(currentMaxId.substring(currentMaxId.lastIndexOf("-") + 1)) + 1;
        }
        return String.format("%s-%s-%06d", prefix, date.format(DATE_FORMATTER), serialNumber);
    }
}
