/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cn.com.qjun.cardboard.service.impl;

import cn.com.qjun.cardboard.common.SystemConstant;
import cn.com.qjun.cardboard.domain.*;
import cn.com.qjun.cardboard.service.dto.*;
import cn.com.qjun.cardboard.utils.SerialNumberGenerator;
import com.google.common.collect.Lists;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import cn.com.qjun.cardboard.repository.StockOutOrderRepository;
import cn.com.qjun.cardboard.service.StockOutOrderService;
import cn.com.qjun.cardboard.service.mapstruct.StockOutOrderMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
 * @author RenQiang
 * @website https://el-admin.vip
 * @description 服务实现
 * @date 2022-06-18
 **/
@Service
@RequiredArgsConstructor
public class StockOutOrderServiceImpl implements StockOutOrderService {

    private final StockOutOrderRepository stockOutOrderRepository;
    private final StockOutOrderMapper stockOutOrderMapper;
    private final SerialNumberGenerator serialNumberGenerator;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Map<String, Object> queryAll(StockOutOrderQueryCriteria criteria, Pageable pageable) {
        Page<StockOutOrder> page = stockOutOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(stockOutOrderMapper::toDto));
    }
    //zmq新加方法   public List<StockInOrderDto>
    @Override
    public  List<StockOutOrderDto> queryAllSortByStockOutTime(StockOutOrderQueryCriteria criteria) {
        // 将 Timestamp 转换为 LocalDateTime
        System.out.println("queryAllSortByStockOutTime");
        LocalDateTime localStartDateTime = criteria.getStockOutTime().get(0).toLocalDateTime();
        LocalDate localStartDate = localStartDateTime.toLocalDate();
        LocalDateTime localEndDateTime = criteria.getStockOutTime().get(1).toLocalDateTime();
        LocalDate localEndDate = localEndDateTime.toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 使用DateTimeFormatter将LocalDate对象格式化为字符串
        String localStartDateStr = localStartDate.format(formatter);
        String localEndDateStr = localEndDate.format(formatter);
        System.out.println(localStartDateStr);
        System.out.println(localEndDateStr);

        Integer warehouseId = criteria.getWarehouseId();

        List<StockOutOrder> all = stockOutOrderRepository.findAllByOrderByStockOutTimeDesc(localStartDateStr,localEndDateStr,warehouseId);

        //有点问题，正常应该是数据排序，不需要再重新排
        List<StockOutOrder> sortResult = new ArrayList<StockOutOrder>(new HashSet<>(all));
        Collections.sort(sortResult, new Comparator<StockOutOrder>() {
            @Override
            public int compare(StockOutOrder o1, StockOutOrder o2) {
                return o2.getStockOutTime().compareTo(o1.getStockOutTime());
            }
        });
        System.out.println("test");
        System.out.println(sortResult);
//        return all;
        return stockOutOrderMapper.toDto(sortResult);

    }

    @Override
    public List<StockOutOrderDto> queryAll(StockOutOrderQueryCriteria criteria) {
        List<StockOutOrder> all = stockOutOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        return stockOutOrderMapper.toDto(new ArrayList<>(new HashSet<>(all)));
    }

    @Override
    @Transactional
    public StockOutOrderDto findById(String id) {
        StockOutOrder stockOutOrder = stockOutOrderRepository.findById(id).orElseGet(StockOutOrder::new);
        ValidationUtil.isNull(stockOutOrder.getId(), "StockOutOrder", "id", id);
        return stockOutOrderMapper.toDto(stockOutOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StockOutOrderDto create(StockOutOrder resources) {
        LocalDate date = resources.getStockOutTime().toLocalDateTime().toLocalDate();
        resources.setId(serialNumberGenerator.generateStockOutOrderId(date));
        resources.setDeleted(SystemConstant.DEL_FLAG_0);
        if (CollectionUtils.isNotEmpty(resources.getOrderItems())) {
            for (StockOutOrderItem orderItem : resources.getOrderItems()) {
                orderItem.setStockOutOrder(resources);
                if (CollectionUtils.isNotEmpty(orderItem.getQualityCheckCerts())) {
                    orderItem.getQualityCheckCerts().forEach(cert -> cert.setStockOutOrderItem(orderItem));
                }
            }
        }
        if (CollectionUtils.isNotEmpty(resources.getWaybills())) {
            for (Waybill waybill : resources.getWaybills()) {
                waybill.setStockOutOrder(resources);
            }
        }
        return stockOutOrderMapper.toDto(stockOutOrderRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(StockOutOrder resources) {
        StockOutOrder stockOutOrder = stockOutOrderRepository.findById(resources.getId()).orElseGet(StockOutOrder::new);
        ValidationUtil.isNull(stockOutOrder.getId(), "StockOutOrder", "id", resources.getId());
        stockOutOrder.copy(resources);
        stockOutOrderRepository.save(stockOutOrder);
    }

    @Override
    public void deleteAll(String[] ids) {
        List<StockOutOrder> allById = stockOutOrderRepository.findAllById(Arrays.asList(ids));
        for (StockOutOrder stockOutOrder : allById) {
            stockOutOrder.setDeleted(SystemConstant.DEL_FLAG_1);
        }
        stockOutOrderRepository.saveAll(allById);
    }

    @Override
    public void download(List<StockOutOrderDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        System.out.println("======");
        System.out.print(list);
//        导出的出库数据需要体现：采购商、出库日期、出库品种、出库重量、净重、单价、总价
        for (StockOutOrderDto stockOutOrder : all) {
            for (StockOutOrderItemDto item : stockOutOrder.getOrderItems()) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("出库单号", stockOutOrder.getId());
                map.put("出库时间", stockOutOrder.getStockOutTime());
                map.put("采购商", stockOutOrder.getBuyer().getName());
                map.put("物料名称", item.getMaterial().getName());
                map.put("物料类别", item.getMaterial().getCategory());
                map.put("出库数量", item.getQuantity());//净重（毛重-皮重）

                //计算所有车的扣减重量
                Double subNum = 0.0;
                for(QualityCheckCertDto cert :item.getQualityCheckCerts()){
                     subNum += cert.getActualWeight();
                }
                map.put("扣减后数量", subNum);//（毛重-皮重-扣减量）
                map.put("物料单价", item.getUnitPrice());

                BigDecimal total = item.getUnitPrice().multiply(BigDecimal.valueOf(subNum));
                map.put("总金额", total);
                list.add(map);
            }

        }

        //        List<Map<String, Object>> list = new ArrayList<>();
//        for (StockOutOrderDto stockOutOrder : all) {
//            Map<String, Object> map = new LinkedHashMap<>();
//            map.put("制单人", stockOutOrder.getCreateBy());
//            map.put("制单时间", stockOutOrder.getCreateTime());
//            map.put("出库时间", stockOutOrder.getStockOutTime());
//            map.put("更新人", stockOutOrder.getUpdateBy());
//            map.put("更新时间", stockOutOrder.getUpdateTime());
//            list.add(map);
//        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<Map<String, Object>> groupingStatistics(LocalDate beginDate, LocalDate endDate) {
        return stockOutOrderRepository.groupingStatistics(beginDate, endDate);
    }

    @Override
    public List<Map<String, Object>> groupingStatisticsMoney(LocalDate beginDate, LocalDate endDate) {
        return stockOutOrderRepository.groupingStatisticsMoney(beginDate, endDate);
    }

    @Override
    public Map<String, Object> report(String reportType, StockOutOrderQueryCriteria criteria, Integer pageNumber, Integer pageSize) {
        LocalDateTime startTime = criteria.getStockOutTime().get(0).toLocalDateTime();
        LocalDateTime endTime = criteria.getStockOutTime().get(1).toLocalDateTime();
        String resultSelect = String.join(" ", "select '出库' as `orderType`,",
                "daily".equals(reportType) ? "date(o.stock_out_time) as `date`," : String.format("'%s ~ %s' as `date`, ",
                        DateUtil.DFY_MD.format(startTime),
                        DateUtil.DFY_MD.format(endTime)),
                "w.name_ as `warehouseName`,", "m.name_ as `materialName`,", "dd.label as `materialCategory`,",
                "sum(if(m.category = 'paper', qc.actual_weight, oi.quantity)) as `quantity`,",
                "oi.unit_price * sum(if(m.category = 'paper', qc.actual_weight, oi.quantity)) as `money`");
        String from = String.join(" ", "from biz_stock_out_order o",
                "join basic_warehouse w on o.warehouse_id = w.id", "join biz_stock_out_order_item oi on o.id = oi.stock_out_order_id",
                "join basic_material m on oi.material_id = m.id", "join sys_dict_detail dd on m.category = dd.`value`",
                "left join biz_quality_check_cert qc on qc.stock_out_order_item_id = oi.id");
        StringBuilder where = new StringBuilder("where o.deleted = 0 and o.stock_out_time between ? and ?");
        List<Object> params = Lists.newArrayList(startTime, endTime);
        if (criteria.getWarehouseId() != null) {
            where.append(" and o.warehouse_id = ?");
            params.add(criteria.getWarehouseId());
        }
        if (StringUtils.isNotEmpty(criteria.getMaterialCategory())) {
            where.append(" and m.category = ?");
            params.add(criteria.getMaterialCategory());
        }
        if (criteria.getMaterialId() != null) {
            where.append(" and oi.material_id = ?");
            params.add(criteria.getMaterialId());
        }
        StringBuilder group = new StringBuilder("group by w.id, m.id");
        StringBuilder order = new StringBuilder("order by w.id, m.id");
        if ("daily".equals(reportType)) {
            group.insert(9, "date(o.stock_out_time), ");
            order.insert(9, "date(o.stock_out_time) desc, ");
        }
        String countSql = String.format("select count(*) from (%s) t", String.join(" ", resultSelect, from, where.toString(), group.toString()));
        Long total = jdbcTemplate.queryForObject(countSql, Long.class, params.toArray());
        String limit = "limit ?, ?";
        params.add((pageNumber - 1) * pageSize);
        params.add(pageSize);
        List<StockReportDto> content = jdbcTemplate.query(String.join(" ", resultSelect, from, where.toString(), group.toString(), order.toString(), limit),
                StockReportDto.ROW_MAPPER, params.toArray());
        return PageUtil.toPage(content, total);
    }
}