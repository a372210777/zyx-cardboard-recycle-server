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

import cn.com.qjun.cardboard.domain.StockInOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author RenQiang
 * @website https://el-admin.vip
 * @date 2022-06-18
 **/
public interface StockInOrderItemRepository extends JpaRepository<StockInOrderItem, Integer>, JpaSpecificationExecutor<StockInOrderItem> {
    @Query(value = "from StockInOrderItem oi where year(oi.stockInOrder.stockInTime) = ?1 and month(oi.stockInOrder.stockInTime) = ?2 and oi.material.id in ?3")
    List<StockInOrderItem> findByMonthAndMaterials(Integer year, Integer month, Set<Integer> materialIds);

    @Query(value = "SELECT sum(t.quantity) FROM biz_stock_in_order_item t LEFT JOIN biz_stock_in_order biz ON biz.id = t.stock_in_order_id WHERE biz.deleted!=1 and  biz.stock_in_time <= ?3 and material_id =?2  and biz.warehouse_id = ?1", nativeQuery = true)
    String queryStockInData(Integer stockId, Integer materialId, LocalDateTime dateTime);

    @Query(value = "SELECT * FROM biz_stock_in_order_item t WHERE t.stock_in_order_id = ?1", nativeQuery = true)
    List<StockInOrderItem> findListByStockInOrderId(String orderId);

    @Query(value = "SELECT * FROM biz_stock_in_order_item t WHERE t.id = ?1", nativeQuery = true)
    StockInOrderItem getStockInOrderItemById(Integer orderId);
}