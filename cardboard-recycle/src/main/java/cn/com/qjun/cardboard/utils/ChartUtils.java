package cn.com.qjun.cardboard.utils;

import cn.com.qjun.cardboard.common.echart.HistogramChartData;
import cn.com.qjun.cardboard.common.echart.MudhChartData;
import cn.com.qjun.cardboard.common.metric.HistogramItem;
import cn.com.qjun.cardboard.common.metric.MultiDimensionHistogram;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * EChart图表工具
 *
 * @author RenQiang
 * @date 2021/11/30
 */
public class ChartUtils {

    /**
     * 通过列表数据构建直方图EChart图表
     *
     * @param dataList   数据列表
     * @param xConverter X坐标转换器
     * @param yConverter Y坐标转换器
     * @param <XT>       X坐标数据类型
     * @param <YT>       Y坐标数据类型
     * @param <T>        列表数据类型
     * @return
     */
    public static <XT extends Comparable<? super XT>, YT extends Number, T>
    HistogramChartData<XT, YT> buildHistogramFromDataList(List<T> dataList, Function<T, XT> xConverter, Function<T, YT> yConverter) {
        HistogramChartData<XT, YT> result = new HistogramChartData<>();
        result.setXData(dataList.stream().map(xConverter).collect(Collectors.toList()));
        result.setSeriesData(dataList.stream().map(yConverter).collect(Collectors.toList()));
        return result;
    }

    /**
     * 通过列表数据构建多维度直方图EChart图表
     *
     * @param dataList    数据列表
     * @param xConverter  X坐标转换器
     * @param legends     图例
     * @param yConverters Y坐标转换器，每个图例对应一个
     * @param <XT>        X坐标数据类型
     * @param <LT>        图例数据类型
     * @param <YT>        Y坐标数据类型
     * @param <T>         列表数据类型
     * @return
     */
    @SafeVarargs
    public static <XT extends Comparable<? super XT>, LT extends Comparable<? super LT>, YT extends Number, T>
    MudhChartData<XT, LT, YT> buildMudhFromDataList(List<T> dataList, Function<T, XT> xConverter, LT[] legends, Function<T, YT>... yConverters) {
        MudhChartData<XT, LT, YT> result = new MudhChartData<>();
        result.setLegendData(Arrays.asList(legends));
        result.setXData(dataList.stream()
                .map(xConverter)
                .collect(Collectors.toList()));
        result.setSeriesData(Arrays.stream(yConverters)
                .map(yConverter -> dataList.stream()
                        .map(yConverter)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList()));
        return result;
    }

    /**
     * 将多维度直方图统计数据转换为EChart图表数据
     *
     * @param mudh
     * @param xConverter
     * @param lConverter
     * @param yConverter
     * @param <SXT>
     * @param <SLT>
     * @param <SYT>
     * @param <TXT>
     * @param <TLT>
     * @param <TYT>
     * @return
     */
    public static <SXT extends Comparable<? super SXT>, SLT extends Comparable<? super SLT>, SYT extends Number,
            TXT extends Comparable<? super TXT>, TLT extends Comparable<? super TLT>, TYT extends Number>
    MudhChartData<TXT, TLT, TYT> mudhMetricToChart(MultiDimensionHistogram<SXT, SLT, SYT> mudh, Function<SXT, TXT> xConverter, Function<SLT, TLT> lConverter, Function<SYT, TYT> yConverter) {
        MudhChartData<TXT, TLT, TYT> result = new MudhChartData<>();
        result.setLegendData(mudh.getLabels().stream()
                .map(lConverter)
                .collect(Collectors.toList()));
        result.setXData(mudh.getData().stream()
                .map(item -> xConverter.apply(item.getXValue()))
                .collect(Collectors.toList()));
        Map<SLT, List<TYT>> legendValues = mudh.getData().stream()
                .flatMap(mudhItem -> mudhItem.getYValues().stream())
                .collect(Collectors.groupingBy(HistogramItem::getItem,
                        Collectors.mapping(item -> yConverter.apply(item.getValue()),
                                Collectors.toList())));
        result.setSeriesData(legendValues.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList()));
        return result;
    }

    /**
     * 将直方图统计数据转换成EChart图表
     *
     * @param histogramItems
     * @param xConverter
     * @param yConverter
     * @param <SXT>
     * @param <SYT>
     * @param <TXT>
     * @param <TYT>
     * @return
     */
    public static <SXT extends Comparable<? super SXT>, SYT extends Number, TXT extends Comparable<? super TXT>, TYT extends Number>
    HistogramChartData<TXT, TYT> histogramMetricToChart(List<HistogramItem<SXT, SYT>> histogramItems, Function<SXT, TXT> xConverter, Function<SYT, TYT> yConverter) {
        HistogramChartData<TXT, TYT> result = new HistogramChartData<>();
        result.setXData(histogramItems.stream()
                .map(item -> xConverter.apply(item.getItem()))
                .collect(Collectors.toList()));
        result.setSeriesData(histogramItems.stream()
                .map(item -> yConverter.apply(item.getValue()))
                .collect(Collectors.toList()));
        return result;
    }
}
