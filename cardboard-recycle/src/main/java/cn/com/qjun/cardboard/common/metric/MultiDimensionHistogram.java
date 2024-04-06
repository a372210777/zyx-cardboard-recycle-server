package cn.com.qjun.cardboard.common.metric;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 多维度直方图
 *
 * @param <X>  横轴数据类型
 * @param <YK> 维度数据类型
 * @param <YV> 纵轴数据类型
 * @author RenQiang
 * @date 2021/11/22
 */
@Data
public class MultiDimensionHistogram<X extends Comparable<? super X>, YK extends Comparable<? super YK>, YV extends Number> implements Serializable {
    private static final long serialVersionUID = -394093982379489162L;

    private List<YK> labels;
    private List<MultiDimensionHistogramItem<X, YK, YV>> data;
}
