package cn.com.qjun.cardboard.vo;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Data
public class StockOrderItem implements Serializable {

    private String orderId;
    private Integer supplier;
    private Integer stockId;
    private Timestamp stockInTime;

    private List<StockOrderItemVo> orderItems;
}
