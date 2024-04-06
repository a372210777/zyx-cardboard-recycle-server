package cn.com.qjun.cardboard.common.metric;

import lombok.Data;

import java.io.Serializable;

/**
 * 直方图项目
 *
 * @param <K> 项目类型
 * @param <V> 值类型
 * @author RenQiang
 * @date 2021/11/22
 */
@Data
public class HistogramItem<K extends Comparable<? super K>, V extends Number> implements Serializable {
    private static final long serialVersionUID = -7393115644145741912L;

    /**
     * 项目名称
     */
    private K item;
    /**
     * 项目值
     */
    private V value;
}
