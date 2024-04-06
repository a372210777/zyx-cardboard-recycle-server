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
import cn.com.qjun.cardboard.domain.BasicVehicle;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import cn.com.qjun.cardboard.repository.BasicVehicleRepository;
import cn.com.qjun.cardboard.service.BasicVehicleService;
import cn.com.qjun.cardboard.service.dto.BasicVehicleDto;
import cn.com.qjun.cardboard.service.dto.BasicVehicleQueryCriteria;
import cn.com.qjun.cardboard.service.mapstruct.BasicVehicleMapper;
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
public class BasicVehicleServiceImpl implements BasicVehicleService {

    private final BasicVehicleRepository basicVehicleRepository;
    private final BasicVehicleMapper basicVehicleMapper;

    @Override
    public Map<String, Object> queryAll(BasicVehicleQueryCriteria criteria, Pageable pageable) {
        Page<BasicVehicle> page = basicVehicleRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(basicVehicleMapper::toDto));
    }

    @Override
    public List<BasicVehicleDto> queryAll(BasicVehicleQueryCriteria criteria) {
        return basicVehicleMapper.toDto(basicVehicleRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
    }

    @Override
    @Transactional
    public BasicVehicleDto findById(Integer id) {
        BasicVehicle basicVehicle = basicVehicleRepository.findById(id).orElseGet(BasicVehicle::new);
        ValidationUtil.isNull(basicVehicle.getId(), "BasicVehicle", "id", id);
        return basicVehicleMapper.toDto(basicVehicle);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BasicVehicleDto create(BasicVehicle resources) {
        resources.setDeleted(SystemConstant.DEL_FLAG_0);
        return basicVehicleMapper.toDto(basicVehicleRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BasicVehicle resources) {
        BasicVehicle basicVehicle = basicVehicleRepository.findById(resources.getId()).orElseGet(BasicVehicle::new);
        ValidationUtil.isNull(basicVehicle.getId(), "BasicVehicle", "id", resources.getId());
        basicVehicle.copy(resources);
        basicVehicleRepository.save(basicVehicle);
    }

    @Override
    public void deleteAll(Integer[] ids) {
        List<BasicVehicle> allById = basicVehicleRepository.findAllById(Arrays.asList(ids));
        for (BasicVehicle basicVehicle : allById) {
            basicVehicle.setDeleted(SystemConstant.DEL_FLAG_1);
        }
        basicVehicleRepository.saveAll(allById);
    }

    @Override
    public void download(List<BasicVehicleDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (BasicVehicleDto basicVehicle : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("车牌号", basicVehicle.getLicensePlate());
            map.put("车辆类型", basicVehicle.getType());
            map.put("司机姓名", basicVehicle.getDriverName());
            map.put("司机电话", basicVehicle.getDriverPhone());
            map.put("载重（吨）", basicVehicle.getLoad());
            map.put("体积", basicVehicle.getVolume());
            map.put("车辆状态", basicVehicle.getStatus());
            map.put("创建人", basicVehicle.getCreateBy());
            map.put("更新人", basicVehicle.getUpdateBy());
            map.put("创建时间", basicVehicle.getCreateTime());
            map.put("更新时间", basicVehicle.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}