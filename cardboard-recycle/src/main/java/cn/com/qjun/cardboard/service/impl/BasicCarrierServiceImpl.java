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
import cn.com.qjun.cardboard.domain.BasicBuyer;
import cn.com.qjun.cardboard.domain.BasicCarrier;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import cn.com.qjun.cardboard.repository.BasicCarrierRepository;
import cn.com.qjun.cardboard.service.BasicCarrierService;
import cn.com.qjun.cardboard.service.dto.BasicCarrierDto;
import cn.com.qjun.cardboard.service.dto.BasicCarrierQueryCriteria;
import cn.com.qjun.cardboard.service.mapstruct.BasicCarrierMapper;
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
public class BasicCarrierServiceImpl implements BasicCarrierService {

    private final BasicCarrierRepository basicCarrierRepository;
    private final BasicCarrierMapper basicCarrierMapper;

    @Override
    public Map<String, Object> queryAll(BasicCarrierQueryCriteria criteria, Pageable pageable) {
        Page<BasicCarrier> page = basicCarrierRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(basicCarrierMapper::toDto));
    }

    @Override
    public List<BasicCarrierDto> queryAll(BasicCarrierQueryCriteria criteria) {
        return basicCarrierMapper.toDto(basicCarrierRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
    }

    @Override
    @Transactional
    public BasicCarrierDto findById(Integer id) {
        BasicCarrier basicCarrier = basicCarrierRepository.findById(id).orElseGet(BasicCarrier::new);
        ValidationUtil.isNull(basicCarrier.getId(), "BasicCarrier", "id", id);
        return basicCarrierMapper.toDto(basicCarrier);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BasicCarrierDto create(BasicCarrier resources) {
        resources.setDeleted(SystemConstant.DEL_FLAG_0);
        return basicCarrierMapper.toDto(basicCarrierRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BasicCarrier resources) {
        BasicCarrier basicCarrier = basicCarrierRepository.findById(resources.getId()).orElseGet(BasicCarrier::new);
        ValidationUtil.isNull(basicCarrier.getId(), "BasicCarrier", "id", resources.getId());
        basicCarrier.copy(resources);
        basicCarrierRepository.save(basicCarrier);
    }

    @Override
    public void deleteAll(Integer[] ids) {
        List<BasicCarrier> allById = basicCarrierRepository.findAllById(Arrays.asList(ids));
        for (BasicCarrier basicCarrier : allById) {
            basicCarrier.setDeleted(SystemConstant.DEL_FLAG_1);
        }
        basicCarrierRepository.saveAll(allById);
    }

    @Override
    public void download(List<BasicCarrierDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (BasicCarrierDto basicCarrier : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("承运商名称", basicCarrier.getName());
            map.put("机构代码", basicCarrier.getInstitutionCode());
            map.put("承运商地址", basicCarrier.getAddress());
            map.put("联系人", basicCarrier.getContact());
            map.put("联系电话", basicCarrier.getPhone());
            map.put("承运商状态", basicCarrier.getStatus());
            map.put("创建人", basicCarrier.getCreateBy());
            map.put("更新人", basicCarrier.getUpdateBy());
            map.put("创建时间", basicCarrier.getCreateTime());
            map.put("更新时间", basicCarrier.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}