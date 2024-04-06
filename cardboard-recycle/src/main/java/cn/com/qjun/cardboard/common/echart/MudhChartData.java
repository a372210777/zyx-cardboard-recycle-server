package cn.com.qjun.cardboard.common.echart;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 多维度直方图EChart数据
 * Multidimensional Histogram
 *
 * @param <XT> 横轴数据类型
 * @param <LT> 图例数据类型
 * @param <YT> 纵轴数据类型
 * @author RenQiang
 * @date 2021/11/30
 */
@Data
public class MudhChartData<XT extends Comparable<? super XT>, LT extends Comparable<? super LT>, YT extends Number> implements Serializable {
    private static final long serialVersionUID = 1052382475195546883L;

    /**
     * 横轴数据
     */
    private List<XT> xData;
    /**
     * 图例数据
     */
    private List<LT> legendData;
    /**
     * 各图例纵轴数据
     */
    private List<List<YT>> seriesData;
}
