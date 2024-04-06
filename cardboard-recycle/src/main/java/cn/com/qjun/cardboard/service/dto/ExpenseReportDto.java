package cn.com.qjun.cardboard.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author RenQiang
 * @date 2022/6/24
 */
@Data
@ApiModel(value = "开销统计结果")
public class ExpenseReportDto implements Serializable {
    private static final long serialVersionUID = -4314473396577482464L;

    @ApiModelProperty(value = "开销分类")
    private String category;
    @ApiModelProperty(value = "总金额")
    private BigDecimal money;
    @ApiModelProperty(value = "日期")
    private String date;

    public static final RowMapper<ExpenseReportDto> ROW_MAPPER = (rs, rowNum) -> {
        ExpenseReportDto reportDto = new ExpenseReportDto();
        reportDto.setDate(rs.getString("date"));
        reportDto.setCategory(rs.getString("category"));
        reportDto.setMoney(rs.getBigDecimal("money"));
        return reportDto;
    };
}
