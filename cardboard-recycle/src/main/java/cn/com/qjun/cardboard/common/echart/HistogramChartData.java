package cn.com.qjun.cardboard.common.echart;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 直方图EChart数据
 *
 * @param <XT> 横轴数据类型
 * @param <YT> 纵轴数据类型
 * @author RenQiang
 * @date 2021/11/30
 */
@Data
public class HistogramChartData<XT extends Comparable<? super XT>, YT extends Number> implements Serializable {
    private static final long serialVersionUID = 1052382475195546883L;

    /**
     * 横轴数据
     */
    private List<XT> xData;
    /**
     * 纵轴数据
     */
    private List<YT> seriesData;
}
