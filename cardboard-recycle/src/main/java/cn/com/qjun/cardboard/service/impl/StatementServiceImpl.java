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
import cn.com.qjun.cardboard.domain.Statement;
import cn.com.qjun.cardboard.domain.StockInOrderItem;
import cn.com.qjun.cardboard.repository.StockInOrderItemRepository;
import cn.com.qjun.cardboard.service.dto.StatementQueryCriteria;
import cn.com.qjun.cardboard.utils.SerialNumberGenerator;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import cn.com.qjun.cardboard.repository.StatementRepository;
import cn.com.qjun.cardboard.service.StatementService;
import cn.com.qjun.cardboard.service.dto.StatementDto;
import cn.com.qjun.cardboard.service.mapstruct.StatementMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.io.IOException;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author RenQiang
* @date 2022-06-18
**/
@Service
@RequiredArgsConstructor
public class StatementServiceImpl implements StatementService {

    private final StatementRepository statementRepository;
    private final StatementMapper statementMapper;
    private final SerialNumberGenerator serialNumberGenerator;
    private final StockInOrderItemRepository stockInOrderItemRepository;

    @Override
    public Map<String,Object> queryAll(StatementQueryCriteria criteria, Pageable pageable){
        Page<Statement> page = statementRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(statementMapper::toDto));
    }

    @Override
    public List<StatementDto> queryAll(StatementQueryCriteria criteria){
        return statementMapper.toDto(statementRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public StatementDto findById(String id) {
        Statement statement = statementRepository.findById(id).orElseGet(Statement::new);
        ValidationUtil.isNull(statement.getId(),"Statement","id",id);
        return statementMapper.toDto(statement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StatementDto create(Statement resources) {
        Statement exist = statementRepository.findOneByYearAndMonth(resources.getYear(), resources.getMonth());
        StatementDto result;
        if (exist == null) {
            LocalDate date = resources.getStatementTime().toLocalDateTime().toLocalDate();
            resources.setId(serialNumberGenerator.generateStatementId(date));
            resources.setDeleted(0);
            resources.getStatementItems().forEach(item -> item.setStatement(resources));
            result = statementMapper.toDto(statementRepository.save(resources));
        } else {
            resources.setId(exist.getId());
            this.update(resources);
            result = statementMapper.toDto(resources);
        }
        Map<Integer, BigDecimal> materialIdUnitPriceMap = resources.getStatementItems().stream()
                .map(item -> Pair.of(item.getMaterial().getId(), item.getPurchasePrice()))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight, (p1, p2) -> p2));
        List<StockInOrderItem> stockInOrderItems = stockInOrderItemRepository.findByMonthAndMaterials(result.getYear(), result.getMonth(), materialIdUnitPriceMap.keySet());
        stockInOrderItems
                .forEach(item -> item.setUnitPrice(materialIdUnitPriceMap.get(item.getMaterial().getId())));
        stockInOrderItemRepository.saveAll(stockInOrderItems);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Statement resources) {
        Statement statement = statementRepository.findById(resources.getId()).orElseGet(Statement::new);
        ValidationUtil.isNull( statement.getId(),"Statement","id",resources.getId());
        statement.copy(resources);
        statementRepository.save(statement);
    }

    @Override
    public void deleteAll(String[] ids) {
        List<Statement> allById = statementRepository.findAllById(Arrays.asList(ids));
        for (Statement statement : allById) {
            statement.setDeleted(SystemConstant.DEL_FLAG_1);
        }
        statementRepository.saveAll(allById);
    }

    @Override
    public void download(List<StatementDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (StatementDto statement : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("对账年份", statement.getYear());
            map.put("对账月份", statement.getMonth());
            map.put("对账人", statement.getCreateBy());
            map.put("对账时间", statement.getStatementTime());
            map.put("创建时间", statement.getCreateTime());
            map.put("更新人", statement.getUpdateBy());
            map.put("更新时间", statement.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}