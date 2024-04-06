package cn.com.qjun.cardboard.service.impl;

import cn.com.qjun.cardboard.domain.StockInOrder;
import cn.com.qjun.cardboard.domain.StockOutOrder;
import cn.com.qjun.cardboard.repository.StockManageRepository;
import cn.com.qjun.cardboard.service.StockManageService;
import cn.com.qjun.cardboard.service.dto.StockInOrderDto;
import cn.com.qjun.cardboard.service.dto.StockInOrderQueryCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @author RenQiang
 * @website https://el-admin.vip
 * @description 查询仓库某个物料的在库数量
 * @date 2024-03-3
 **/
@Service
@RequiredArgsConstructor
public class StockManageServiceImpl implements StockManageService {

    private  final StockManageRepository stockManageRepository;
    @Override
    public List<StockInOrderDto> queryAll(Integer warehouseId,Integer materialId,String date) {

//        String sql = "select * ";
//        String from = String.join(" ", "from biz_stock_in_order o",
//                "join basic_warehouse w on o.warehouse_id = w.id",
//                "join biz_stock_in_order_item oi on o.id = oi.stock_in_order_id",
//                "join basic_material m on oi.material_id = m.id",
//                "join sys_dict_detail dd on m.category = dd.`value`");
//        String where = String.join(" ","o.deleted=0 and stock_in_time< date");
           List<StockOutOrder> inOrderList =  stockManageRepository.findAllByOrderByStockOutTimeDesc(date);
           List<StockInOrder> outOrderList = stockManageRepository.findAllByOrderByStockInTimeDesc(date);
//           System.out.println(inOrderList);
           System.out.println("test====");
//           System.out.println(outOrderList);

        return null;
    }
}
