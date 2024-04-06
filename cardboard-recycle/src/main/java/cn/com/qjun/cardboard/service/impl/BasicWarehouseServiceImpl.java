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
import cn.com.qjun.cardboard.domain.BasicWarehouse;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import cn.com.qjun.cardboard.repository.BasicWarehouseRepository;
import cn.com.qjun.cardboard.service.BasicWarehouseService;
import cn.com.qjun.cardboard.service.dto.BasicWarehouseDto;
import cn.com.qjun.cardboard.service.dto.BasicWarehouseQueryCriteria;
import cn.com.qjun.cardboard.service.mapstruct.BasicWarehouseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;

import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
 * @author RenQiang
 * @website https://el-admin.vip
 * @description 服务实现
 * @date 2022-06-17
 **/
@Service
@RequiredArgsConstructor
public class BasicWarehouseServiceImpl implements BasicWarehouseService {

    private final BasicWarehouseRepository basicWarehouseRepository;
    private final BasicWarehouseMapper basicWarehouseMapper;

    @Override
    public Map<String, Object> queryAll(BasicWarehouseQueryCriteria criteria, Pageable pageable) {
        Page<BasicWarehouse> page = basicWarehouseRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(basicWarehouseMapper::toDto));
    }

    @Override
    public List<BasicWarehouseDto> queryAll(BasicWarehouseQueryCriteria criteria) {
        return basicWarehouseMapper.toDto(basicWarehouseRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
    }

    @Override
    @Transactional
    public BasicWarehouseDto findById(Integer id) {
        BasicWarehouse basicWarehouse = basicWarehouseRepository.findById(id).orElseGet(BasicWarehouse::new);
        ValidationUtil.isNull(basicWarehouse.getId(), "BasicWarehouse", "id", id);
        return basicWarehouseMapper.toDto(basicWarehouse);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BasicWarehouseDto create(BasicWarehouse resources) {
        resources.setDeleted(SystemConstant.DEL_FLAG_0);
        return basicWarehouseMapper.toDto(basicWarehouseRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BasicWarehouse resources) {
        BasicWarehouse basicWarehouse = basicWarehouseRepository.findById(resources.getId()).orElseGet(BasicWarehouse::new);
        ValidationUtil.isNull(basicWarehouse.getId(), "BasicWarehouse", "id", resources.getId());
        basicWarehouse.copy(resources);
        basicWarehouseRepository.save(basicWarehouse);
    }

    @Override
    public void deleteAll(Integer[] ids) {
        List<BasicWarehouse> allById = basicWarehouseRepository.findAllById(Arrays.asList(ids));
        for (BasicWarehouse basicWarehouse : allById) {
            basicWarehouse.setDeleted(SystemConstant.DEL_FLAG_1);
        }
        basicWarehouseRepository.saveAll(allById);
    }

    @Override
    public void download(List<BasicWarehouseDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (BasicWarehouseDto basicWarehouse : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("仓库名称", basicWarehouse.getName());
            map.put("仓库地址", basicWarehouse.getAddress());
            map.put("仓库面积（平方米）", basicWarehouse.getArea());
            map.put("联系人", basicWarehouse.getContact());
            map.put("联系电话", basicWarehouse.getPhone());
            map.put("仓库状态", basicWarehouse.getStatus());
            map.put("创建人", basicWarehouse.getCreateBy());
            map.put("更新人", basicWarehouse.getUpdateBy());
            map.put("创建时间", basicWarehouse.getCreateTime());
            map.put("更新时间", basicWarehouse.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}