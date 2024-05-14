package cn.com.qjun.cardboard.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class StockOrderItemVo implements Serializable {

    private Integer id;

    private BigDecimal quantity;
    private String remark;

    private String unit;
    private BigDecimal unitPrice;

    private String materialName;

    private boolean isEdit;

    private boolean isNew;

    private Integer tempMaterialId;

    private MaterialVo material;
}
