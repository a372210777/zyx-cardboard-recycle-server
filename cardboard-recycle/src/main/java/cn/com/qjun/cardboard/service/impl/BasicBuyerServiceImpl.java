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
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import cn.com.qjun.cardboard.repository.BasicBuyerRepository;
import cn.com.qjun.cardboard.service.BasicBuyerService;
import cn.com.qjun.cardboard.service.dto.BasicBuyerDto;
import cn.com.qjun.cardboard.service.dto.BasicBuyerQueryCriteria;
import cn.com.qjun.cardboard.service.mapstruct.BasicBuyerMapper;
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
* @website https://el-admin.vip
* @description 服务实现
* @author RenQiang
* @date 2022-06-17
**/
@Service
@RequiredArgsConstructor
public class BasicBuyerServiceImpl implements BasicBuyerService {

    private final BasicBuyerRepository basicBuyerRepository;
    private final BasicBuyerMapper basicBuyerMapper;

    @Override
    public Map<String,Object> queryAll(BasicBuyerQueryCriteria criteria, Pageable pageable){
        Page<BasicBuyer> page = basicBuyerRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(basicBuyerMapper::toDto));
    }

    @Override
    public List<BasicBuyerDto> queryAll(BasicBuyerQueryCriteria criteria){
        return basicBuyerMapper.toDto(basicBuyerRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public BasicBuyerDto findById(Integer id) {
        BasicBuyer basicBuyer = basicBuyerRepository.findById(id).orElseGet(BasicBuyer::new);
        ValidationUtil.isNull(basicBuyer.getId(),"BasicBuyer","id",id);
        return basicBuyerMapper.toDto(basicBuyer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BasicBuyerDto create(BasicBuyer resources) {
        resources.setDeleted(SystemConstant.DEL_FLAG_0);
        return basicBuyerMapper.toDto(basicBuyerRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BasicBuyer resources) {
        BasicBuyer basicBuyer = basicBuyerRepository.findById(resources.getId()).orElseGet(BasicBuyer::new);
        ValidationUtil.isNull( basicBuyer.getId(),"BasicBuyer","id",resources.getId());
        basicBuyer.copy(resources);
        basicBuyerRepository.save(basicBuyer);
    }

    @Override
    public void deleteAll(Integer[] ids) {
        List<BasicBuyer> allById = basicBuyerRepository.findAllById(Arrays.asList(ids));
        for (BasicBuyer basicBuyer : allById) {
            basicBuyer.setDeleted(SystemConstant.DEL_FLAG_1);
        }
        basicBuyerRepository.saveAll(allById);
    }

    @Override
    public void download(List<BasicBuyerDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (BasicBuyerDto basicBuyer : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("采购商名称", basicBuyer.getName());
            map.put("机构代码", basicBuyer.getInstitutionCode());
            map.put("采购商地址", basicBuyer.getAddress());
            map.put("联系人", basicBuyer.getContact());
            map.put("联系电话", basicBuyer.getPhone());
            map.put("采购商状态", basicBuyer.getStatus());
            map.put("创建人", basicBuyer.getCreateBy());
            map.put("更新人", basicBuyer.getUpdateBy());
            map.put("创建时间", basicBuyer.getCreateTime());
            map.put("更新时间", basicBuyer.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}