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
package cn.com.qjun.cardboard.repository;

import cn.com.qjun.cardboard.domain.StockInOrder;
import cn.com.qjun.cardboard.domain.StockOutOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
* @website https://el-admin.vip
* @author RenQiang
* @date 2022-06-18
**/
public interface StockOutOrderRepository extends JpaRepository<StockOutOrder, String>, JpaSpecificationExecutor<StockOutOrder> {
    @Query(value = "select date(o.stock_out_time) as date_, m.name_ as material, sum(oi.quantity) as quantity from biz_stock_out_order o join biz_stock_out_order_item oi on o.id = oi.stock_out_order_id join basic_material m on m.id = oi.material_id where o.stock_out_time between ?1 and ?2 and o.deleted = 0 group by date(o.stock_out_time), oi.material_id", nativeQuery = true)
    List<Map<String, Object>> groupingStatistics(LocalDate beginDate, LocalDate endDate);

    @Query(value = "select date(o.stock_out_time) as date_, m.name_ as material, if(m.category = 'paper', sum(qc.actual_weight * oi.unit_price), sum(oi.quantity * oi.unit_price)) as quantity from biz_stock_out_order o join biz_stock_out_order_item oi on o.id = oi.stock_out_order_id join basic_material m on m.id = oi.material_id left join biz_quality_check_cert qc on qc.stock_out_order_item_id = oi.id where o.stock_out_time between ?1 and ?2 and o.deleted = 0 group by date(o.stock_out_time), oi.material_id", nativeQuery = true)
    List<Map<String, Object>> groupingStatisticsMoney(LocalDate beginDate, LocalDate endDate);

    @Query(value = "select max(id) from biz_stock_out_order where date(stock_out_time) = ?", nativeQuery = true)
    String getMaxIdByStockInDate(LocalDate date);

    //zmq新加方法
    @Query(value = "select * from biz_stock_out_order o  left join biz_stock_out_order_item oi on o.id = oi.stock_out_order_id \n" +
            " left join basic_material m on oi.material_id = m.id   left join basic_buyer b on o.buyer_id = b.id  where o.stock_out_time between ?1 and ?2 and o.deleted = 0 and (O.warehouse_id=NULL or O.warehouse_id=?3 )order by o.stock_out_time asc", nativeQuery = true)
    List<StockOutOrder> findAllByOrderByStockOutTimeDesc(String beginDate, String endDate,Integer warehouseId);


}
