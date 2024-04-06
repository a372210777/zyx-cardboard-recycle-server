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
import cn.com.qjun.cardboard.domain.BasicSupplier;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import cn.com.qjun.cardboard.repository.BasicSupplierRepository;
import cn.com.qjun.cardboard.service.BasicSupplierService;
import cn.com.qjun.cardboard.service.dto.BasicSupplierDto;
import cn.com.qjun.cardboard.service.dto.BasicSupplierQueryCriteria;
import cn.com.qjun.cardboard.service.mapstruct.BasicSupplierMapper;
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
public class BasicSupplierServiceImpl implements BasicSupplierService {

    private final BasicSupplierRepository basicSupplierRepository;
    private final BasicSupplierMapper basicSupplierMapper;

    @Override
    public Map<String, Object> queryAll(BasicSupplierQueryCriteria criteria, Pageable pageable) {
        Page<BasicSupplier> page = basicSupplierRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(basicSupplierMapper::toDto));
    }

    @Override
    public List<BasicSupplierDto> queryAll(BasicSupplierQueryCriteria criteria) {
        return basicSupplierMapper.toDto(basicSupplierRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
    }

    @Override
    @Transactional
    public BasicSupplierDto findById(Integer id) {
        BasicSupplier basicSupplier = basicSupplierRepository.findById(id).orElseGet(BasicSupplier::new);
        ValidationUtil.isNull(basicSupplier.getId(), "BasicSupplier", "id", id);
        return basicSupplierMapper.toDto(basicSupplier);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BasicSupplierDto create(BasicSupplier resources) {
        resources.setDeleted(SystemConstant.DEL_FLAG_0);
        return basicSupplierMapper.toDto(basicSupplierRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BasicSupplier resources) {
        BasicSupplier basicSupplier = basicSupplierRepository.findById(resources.getId()).orElseGet(BasicSupplier::new);
        ValidationUtil.isNull(basicSupplier.getId(), "BasicSupplier", "id", resources.getId());
        basicSupplier.copy(resources);
        basicSupplierRepository.save(basicSupplier);
    }

    @Override
    public void deleteAll(Integer[] ids) {
        List<BasicSupplier> allById = basicSupplierRepository.findAllById(Arrays.asList(ids));
        for (BasicSupplier basicSupplier : allById) {
            basicSupplier.setDeleted(SystemConstant.DEL_FLAG_1);
        }
        basicSupplierRepository.saveAll(allById);
    }

    @Override
    public void download(List<BasicSupplierDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (BasicSupplierDto basicSupplier : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("供应商名称", basicSupplier.getName());
            map.put("机构代码", basicSupplier.getInstitutionCode());
            map.put("供应商地址", basicSupplier.getAddress());
            map.put("联系人", basicSupplier.getContact());
            map.put("联系电话", basicSupplier.getPhone());
            map.put("供应商状态", basicSupplier.getStatus());
            map.put("创建人", basicSupplier.getCreateBy());
            map.put("更新人", basicSupplier.getUpdateBy());
            map.put("创建时间", basicSupplier.getCreateTime());
            map.put("更新时间", basicSupplier.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}