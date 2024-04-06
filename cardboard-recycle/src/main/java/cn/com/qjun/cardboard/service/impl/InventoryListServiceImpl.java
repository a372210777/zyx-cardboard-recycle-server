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

import cn.com.qjun.cardboard.domain.InventoryList;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import cn.com.qjun.cardboard.repository.InventoryListRepository;
import cn.com.qjun.cardboard.service.InventoryListService;
import cn.com.qjun.cardboard.service.dto.InventoryListDto;
import cn.com.qjun.cardboard.service.dto.InventoryListQueryCriteria;
import cn.com.qjun.cardboard.service.mapstruct.InventoryListMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author RenQiang
* @date 2022-06-18
**/
@Service
@RequiredArgsConstructor
public class InventoryListServiceImpl implements InventoryListService {

    private final InventoryListRepository inventoryListRepository;
    private final InventoryListMapper inventoryListMapper;

    @Override
    public Map<String,Object> queryAll(InventoryListQueryCriteria criteria, Pageable pageable){
        Page<InventoryList> page = inventoryListRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(inventoryListMapper::toDto));
    }

    @Override
    public List<InventoryListDto> queryAll(InventoryListQueryCriteria criteria){
        return inventoryListMapper.toDto(inventoryListRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public InventoryListDto findById(Integer id) {
        InventoryList inventoryList = inventoryListRepository.findById(id).orElseGet(InventoryList::new);
        ValidationUtil.isNull(inventoryList.getId(),"InventoryList","id",id);
        return inventoryListMapper.toDto(inventoryList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InventoryListDto create(InventoryList resources) {
        return inventoryListMapper.toDto(inventoryListRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(InventoryList resources) {
        InventoryList inventoryList = inventoryListRepository.findById(resources.getId()).orElseGet(InventoryList::new);
        ValidationUtil.isNull( inventoryList.getId(),"InventoryList","id",resources.getId());
        inventoryList.copy(resources);
        inventoryListRepository.save(inventoryList);
    }

    @Override
    public void deleteAll(Integer[] ids) {
        for (Integer id : ids) {
            inventoryListRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<InventoryListDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (InventoryListDto inventoryList : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("物料ID", inventoryList.getMaterialId());
            map.put("仓库ID", inventoryList.getWarehouseId());
            map.put("库存日期", inventoryList.getDate());
            map.put("创建时间", inventoryList.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}