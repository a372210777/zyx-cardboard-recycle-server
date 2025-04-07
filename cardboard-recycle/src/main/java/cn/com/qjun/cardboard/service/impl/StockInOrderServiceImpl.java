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
import cn.com.qjun.cardboard.repository.*;
import cn.com.qjun.cardboard.service.BasicSupplierService;
import cn.com.qjun.cardboard.service.BasicWarehouseService;
import cn.com.qjun.cardboard.service.dto.*;
import cn.com.qjun.cardboard.service.mapstruct.StockInOrderItemMapper;
import cn.com.qjun.cardboard.utils.SerialNumberGenerator;
import cn.com.qjun.cardboard.vo.StockOrderItem;
import cn.com.qjun.cardboard.vo.StockOrderItemVo;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.Lists;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import cn.com.qjun.cardboard.service.StockInOrderService;
import cn.com.qjun.cardboard.service.mapstruct.StockInOrderMapper;
import org.springframework.data.domain.Example;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.io.IOException;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * @author RenQiang
 * @website https://el-admin.vip
 * @description 服务实现
 * @date 2022-06-18
 **/
@Service
@RequiredArgsConstructor
public class StockInOrderServiceImpl implements StockInOrderService {

    private final StockInOrderRepository stockInOrderRepository;
    private final StockInOrderMapper stockInOrderMapper;
    private final SerialNumberGenerator serialNumberGenerator;
    private final JdbcTemplate jdbcTemplate;

    @Resource
    private StockInOrderItemRepository stockInOrderItemRepository;
    @Resource
    private StockInOrderItemMapper stockInOrderItemMapper;

    @Resource
    private BasicWarehouseRepository basicWarehouseRepository;
    @Resource
    private BasicSupplierRepository supplierRepository;
    @Resource
    private BasicMaterialRepository materialRepository;

    @Override
    public Map<String, Object> queryAll(StockInOrderQueryCriteria criteria, Pageable pageable) {
        Page<StockInOrder> page = stockInOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(stockInOrderMapper::toDto));
    }

    @Override
    public List<StockInOrderDto> queryAll(StockInOrderQueryCriteria criteria) {
        List<StockInOrder> all = stockInOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        return stockInOrderMapper.toDto(new ArrayList<>(new HashSet<>(all)));
    }

    //zmq新加方法   public List<StockInOrderDto>
    @Override
    public List<StockInOrderDto> queryAllSortByStockInTime(StockInOrderQueryCriteria criteria) {
        // 将 Timestamp 转换为 LocalDateTime
        System.out.println("queryAllSortByStockInTime");
        if (null==criteria||(StringUtils.isBlank(criteria.getId())&& CollectionUtil.isEmpty(criteria.getStockInTime()))){
            throw new RuntimeException("请在条件栏中输入单号/入库时间");
        }
        List<StockInOrder> all =new ArrayList<>();
        if (CollectionUtil.isNotEmpty(criteria.getStockInTime())) {
            LocalDateTime localStartDateTime = criteria.getStockInTime().get(0).toLocalDateTime();
            LocalDate localStartDate = localStartDateTime.toLocalDate();
            LocalDateTime localEndDateTime = criteria.getStockInTime().get(1).toLocalDateTime();
            LocalDate localEndDate = localEndDateTime.toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // 使用DateTimeFormatter将LocalDate对象格式化为字符串
            String localStartDateStr = localStartDate.format(formatter);
            String localEndDateStr = localEndDate.format(formatter);
            System.out.println(localStartDateStr);
            System.out.println(localEndDateStr);
            all = stockInOrderRepository.findAllByOrderByStockInTimeDesc(localStartDateStr, localEndDateStr);
        }else {
            all = stockInOrderRepository.selectOneById(criteria.getId());
        }
        if (CollectionUtil.isEmpty(all)){
            throw new RuntimeException("无数据可导出");
        }
        //有点问题，正常应该是数据排序，不需要再重新排
        List<StockInOrder> sortResult = new ArrayList<StockInOrder>(new HashSet<>(all));
        Collections.sort(sortResult, new Comparator<StockInOrder>() {
            @Override
            public int compare(StockInOrder o1, StockInOrder o2) {
                return o1.getStockInTime().compareTo(o2.getStockInTime());
            }
        });

//        return all;
        return stockInOrderMapper.toDto(sortResult);

    }

    @Override
    @Transactional
    public StockInOrderDto findById(String id) {
        StockInOrder stockInOrder = stockInOrderRepository.findById(id).orElseGet(StockInOrder::new);
        ValidationUtil.isNull(stockInOrder.getId(), "StockInOrder", "id", id);
        return stockInOrderMapper.toDto(stockInOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StockInOrderDto create(StockInOrder resources) {
        LocalDate date = resources.getStockInTime().toLocalDateTime().toLocalDate();
        resources.setId(serialNumberGenerator.generateStockInOrderId(date));
        resources.setDeleted(SystemConstant.DEL_FLAG_0);
        resources.getOrderItems()
                .forEach(item -> item.setStockInOrder(resources));
        return stockInOrderMapper.toDto(stockInOrderRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(StockInOrder resources) {
        StockInOrder stockInOrder = stockInOrderRepository.findById(resources.getId()).orElseGet(StockInOrder::new);
        ValidationUtil.isNull(stockInOrder.getId(), "StockInOrder", "id", resources.getId());
        stockInOrder.copy(resources);
        stockInOrderRepository.save(stockInOrder);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(StockOrderItem resources) {

        /**
         * 修改入库单本身
         */
        StockInOrder stockInOrder = this.stockInOrderRepository.getById(resources.getOrderId());
        stockInOrder.setId(resources.getOrderId());
        BasicWarehouse basicWarehouse = basicWarehouseRepository.findById(resources.getStockId()).orElseGet(BasicWarehouse::new);
        stockInOrder.setWarehouse(basicWarehouse);
        stockInOrder.setSupplier(this.supplierRepository.findById(resources.getSupplier()).orElseGet(BasicSupplier::new));
        stockInOrder.setStockInTime(resources.getStockInTime());
        this.stockInOrderRepository.save(stockInOrder);

        List<StockInOrderItem> list = this.stockInOrderItemRepository.findListByStockInOrderId(stockInOrder.getId());
//        Map<BasicMaterial, List<StockInOrderItem>> collect = list.stream().collect(Collectors.groupingBy(StockInOrderItem::getMaterial));
        Map<Integer, List<StockInOrderItem>> collect = list.stream().collect(Collectors.groupingBy(StockInOrderItem::getId));
        /**
         * 保存明细数据
         */
        for (StockOrderItemVo orderItem : resources.getOrderItems()) {
            StockInOrderItem entity = null;
            if (orderItem.isNew()) {
                /**
                 * 新增明细
                 */
                entity = new StockInOrderItem();
                entity.setStockInOrder(stockInOrder);

            } else {
                /**
                 * 修改明细
                 */
                entity = this.stockInOrderItemRepository.getStockInOrderItemById(orderItem.getId());
                if (ObjectUtil.isEmpty(entity)) {
                    collect.remove(orderItem.getId());
                    continue;
                } else {
                    collect.remove(orderItem.getId());
                }
            }
            entity.setUnit(orderItem.getUnit());
            entity.setRemark(orderItem.getRemark());
            entity.setQuantity(orderItem.getQuantity().intValue());
            entity.setMaterial(this.materialRepository.findById(orderItem.getMaterial().getId()).orElseGet(BasicMaterial::new));
            entity.setUnitPrice(orderItem.getUnitPrice());
            this.stockInOrderItemRepository.save(entity);
        }
        /**
         * 其它的都删除
         */
        collect.keySet().forEach(key -> {
            List<StockInOrderItem> stockInOrderItems = collect.get(key);
            if (CollUtil.isNotEmpty(stockInOrderItems)) {
                this.stockInOrderItemRepository.delete(stockInOrderItems.get(0));
            }
        });

    }

    @Override
    public void deleteAll(String[] ids) {
        List<StockInOrder> allById = stockInOrderRepository.findAllById(Arrays.asList(ids));
        for (StockInOrder stockInOrder : allById) {
            stockInOrder.setDeleted(SystemConstant.DEL_FLAG_1);
        }
        stockInOrderRepository.saveAll(allById);
    }

    @Override
    public void download(List<StockInOrderDto> all, HttpServletResponse response) throws IOException {

        List<Map<String, Object>> list = new ArrayList<>();

        for (StockInOrderDto stockInOrder : all) {
            for (StockInOrderItemDto item : stockInOrder.getOrderItems()) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("入库单号", stockInOrder.getId());
                map.put("入库时间", stockInOrder.getStockInTime());
                map.put("物料名称", item.getMaterial().getName());
                map.put("物料类别", item.getMaterial().getCategory());
                map.put("物料数量", item.getQuantity());
                map.put("物料单价", item.getUnitPrice());
                list.add(map);
            }

        }
//        for (StockInOrderDto stockInOrder : all) {
//            Map<String, Object> map = new LinkedHashMap<>();
//            map.put("入库单号", stockInOrder.getId());
//            map.put("入库时间", stockInOrder.getStockInTime());
//
//            map.put("制单人", stockInOrder.getCreateBy());
//            map.put("制单时间", stockInOrder.getCreateTime());
//            list.add(map);
//        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<Map<String, Object>> groupingStatistics(LocalDate beginDate, LocalDate endDate) {
        return stockInOrderRepository.groupingStatistics(beginDate, endDate);
    }

    @Override
    public Map<String, Object> report(String reportType, StockInOrderQueryCriteria criteria, Integer pageNumber, Integer pageSize) {
        LocalDateTime startTime = criteria.getStockInTime().get(0).toLocalDateTime();
        LocalDateTime endTime = criteria.getStockInTime().get(1).toLocalDateTime();
        String resultSelect = String.join(" ", "select '入库' as `orderType`,",
                "daily".equals(reportType) ? "date(o.stock_in_time) as `date`," : String.format("'%s ~ %s' as `date`, ",
                        DateUtil.DFY_MD.format(startTime),
                        DateUtil.DFY_MD.format(endTime)),
                "w.name_ as `warehouseName`,", "m.name_ as `materialName`,", "dd.label as `materialCategory`,",
                "sum(oi.quantity) as `quantity`,",
                "sum(ifnull(oi.unit_price, 0) * oi.quantity) as `money`");
        String from = String.join(" ", "from biz_stock_in_order o",
                "join basic_warehouse w on o.warehouse_id = w.id", "join biz_stock_in_order_item oi on o.id = oi.stock_in_order_id",
                "join basic_material m on oi.material_id = m.id", "join sys_dict_detail dd on m.category = dd.`value`");
        StringBuilder where = new StringBuilder("where o.deleted = 0 and o.stock_in_time between ? and ?");
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
            group.insert(9, "date(o.stock_in_time), ");
            order.insert(9, "date(o.stock_in_time) desc, ");
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
