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

import cn.com.qjun.cardboard.domain.StockOutOrderItem;
import io.swagger.models.auth.In;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

/**
* @website https://el-admin.vip
* @author RenQiang
* @date 2022-06-18
**/
public interface StockOutOrderItemRepository extends JpaRepository<StockOutOrderItem, Integer>, JpaSpecificationExecutor<StockOutOrderItem> {

    @Query(value = "SELECT sum( t.quantity ) FROM biz_stock_out_order_item t LEFT JOIN biz_stock_out_order biz ON biz.id = t.stock_out_order_id WHERE biz.deleted!=1 and  biz.stock_out_time <= ?3 AND material_id = ?2 AND biz.warehouse_id = ?1" ,nativeQuery = true)
    String queryStockOutData(Integer stockId, Integer materialId, LocalDateTime dateTime );
}