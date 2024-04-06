package cn.com.qjun.cardboard.repository;

import cn.com.qjun.cardboard.domain.StockInOrder;
import cn.com.qjun.cardboard.domain.StockOutOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;


import cn.com.qjun.cardboard.domain.StockInOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**

 **/
public interface StockManageRepository extends JpaRepository<StockInOrder, String>, JpaSpecificationExecutor<StockInOrder> {
    @Query(value = "select date(o.stock_in_time) as date_, m.name_ as material, sum(oi.quantity) as quantity from biz_stock_in_order o join biz_stock_in_order_item oi on o.id = oi.stock_in_order_id join basic_material m on oi.material_id = m.id where o.stock_in_time between ?1 and ?2 and o.deleted = 0 group by date(o.stock_in_time), oi.material_id", nativeQuery = true)
    List<Map<String, Object>> getAllStockInOrder(LocalDate beginDate, LocalDate endDate);


    //zmq新加方法
    @Query(value = "select * from biz_stock_in_order o  join biz_stock_in_order_item oi on o.id = oi.stock_in_order_id \n" +
            " join basic_material m on oi.material_id = m.id  where o.stock_in_time < ?1 and o.deleted = 0 order by o.stock_in_time desc", nativeQuery = true)
    List<StockInOrder> findAllByOrderByStockInTimeDesc(String endDate);

    //zmq新加方法
    @Query(value = "select * from biz_stock_out_order o  join biz_stock_out_order_item oi on o.id = oi.stock_out_order_id \n" +
            " join basic_material m on oi.material_id = m.id  where o.stock_out_time <?1 and o.deleted = 0 order by o.stock_out_time desc", nativeQuery = true)
    List<StockOutOrder> findAllByOrderByStockOutTimeDesc(String endDate);
}