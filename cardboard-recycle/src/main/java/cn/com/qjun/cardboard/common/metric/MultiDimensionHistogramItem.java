package cn.com.qjun.cardboard.common.metric;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 多维度直方图项目
 *
 * @param <X>  横轴数据类型
 * @param <YK> 维度数据类型
 * @param <YV> 纵轴数据类型
 * @author RenQiang
 * @date 2021/11/22
 */
@Data
public class MultiDimensionHistogramItem<X extends Comparable<? super X>, YK extends Comparable<? super YK>, YV extends Number> implements Serializable {
    private static final long serialVersionUID = -8266146081496328006L;

    /**
     * X坐标值
     */
    private X xValue;
    /**
     * Y坐标值集
     */
    private List<HistogramItem<YK, YV>> yValues;
}
