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
import cn.com.qjun.cardboard.domain.BasicMaterial;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import cn.com.qjun.cardboard.repository.BasicMaterialRepository;
import cn.com.qjun.cardboard.service.BasicMaterialService;
import cn.com.qjun.cardboard.service.dto.BasicMaterialDto;
import cn.com.qjun.cardboard.service.dto.BasicMaterialQueryCriteria;
import cn.com.qjun.cardboard.service.mapstruct.BasicMaterialMapper;
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
public class BasicMaterialServiceImpl implements BasicMaterialService {

    private final BasicMaterialRepository basicMaterialRepository;
    private final BasicMaterialMapper basicMaterialMapper;

    @Override
    public Map<String,Object> queryAll(BasicMaterialQueryCriteria criteria, Pageable pageable){
        Page<BasicMaterial> page = basicMaterialRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(basicMaterialMapper::toDto));
    }

    @Override
    public List<BasicMaterialDto> queryAll(BasicMaterialQueryCriteria criteria){
        return basicMaterialMapper.toDto(basicMaterialRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public BasicMaterialDto findById(Integer id) {
        BasicMaterial basicMaterial = basicMaterialRepository.findById(id).orElseGet(BasicMaterial::new);
        ValidationUtil.isNull(basicMaterial.getId(),"BasicMaterial","id",id);
        return basicMaterialMapper.toDto(basicMaterial);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BasicMaterialDto create(BasicMaterial resources) {
        resources.setDeleted(SystemConstant.DEL_FLAG_0);
        return basicMaterialMapper.toDto(basicMaterialRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BasicMaterial resources) {
        BasicMaterial basicMaterial = basicMaterialRepository.findById(resources.getId()).orElseGet(BasicMaterial::new);
        ValidationUtil.isNull( basicMaterial.getId(),"BasicMaterial","id",resources.getId());
        basicMaterial.copy(resources);
        basicMaterialRepository.save(basicMaterial);
    }

    @Override
    public void deleteAll(Integer[] ids) {
        List<BasicMaterial> allById = basicMaterialRepository.findAllById(Arrays.asList(ids));
        for (BasicMaterial basicMaterial : allById) {
            basicMaterial.setDeleted(SystemConstant.DEL_FLAG_1);
        }
        basicMaterialRepository.saveAll(allById);
    }

    @Override
    public void download(List<BasicMaterialDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (BasicMaterialDto basicMaterial : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("物料类别", basicMaterial.getCategory());
            map.put("创建人", basicMaterial.getCreateBy());
            map.put("更新人", basicMaterial.getUpdateBy());
            map.put("创建时间", basicMaterial.getCreateTime());
            map.put("更新时间", basicMaterial.getUpdateTime());
            map.put("物料名称", basicMaterial.getName());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}