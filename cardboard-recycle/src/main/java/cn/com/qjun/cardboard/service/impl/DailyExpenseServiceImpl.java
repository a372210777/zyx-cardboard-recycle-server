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
import cn.com.qjun.cardboard.domain.DailyExpense;
import cn.com.qjun.cardboard.service.dto.ExpenseReportDto;
import com.google.common.collect.Lists;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import cn.com.qjun.cardboard.repository.DailyExpenseRepository;
import cn.com.qjun.cardboard.service.DailyExpenseService;
import cn.com.qjun.cardboard.service.dto.DailyExpenseDto;
import cn.com.qjun.cardboard.service.dto.DailyExpenseQueryCriteria;
import cn.com.qjun.cardboard.service.mapstruct.DailyExpenseMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Date;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

/**
 * @author RenQiang
 * @website https://el-admin.vip
 * @description 服务实现
 * @date 2022-06-25
 **/
@Service
@RequiredArgsConstructor
public class DailyExpenseServiceImpl implements DailyExpenseService {

    private final DailyExpenseRepository dailyExpenseRepository;
    private final DailyExpenseMapper dailyExpenseMapper;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Map<String, Object> queryAll(DailyExpenseQueryCriteria criteria, Pageable pageable) {
        Page<DailyExpense> page = dailyExpenseRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(dailyExpenseMapper::toDto));
    }

    @Override
    public List<DailyExpenseDto> queryAll(DailyExpenseQueryCriteria criteria) {
        return dailyExpenseMapper.toDto(dailyExpenseRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
    }

    @Override
    @Transactional
    public DailyExpenseDto findById(Integer id) {
        DailyExpense dailyExpense = dailyExpenseRepository.findById(id).orElseGet(DailyExpense::new);
        ValidationUtil.isNull(dailyExpense.getId(), "DailyExpense", "id", id);
        return dailyExpenseMapper.toDto(dailyExpense);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DailyExpenseDto create(DailyExpense resources) {
        resources.setDeleted(SystemConstant.DEL_FLAG_0);
        return dailyExpenseMapper.toDto(dailyExpenseRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DailyExpense resources) {
        DailyExpense dailyExpense = dailyExpenseRepository.findById(resources.getId()).orElseGet(DailyExpense::new);
        ValidationUtil.isNull(dailyExpense.getId(), "DailyExpense", "id", resources.getId());
        dailyExpense.copy(resources);
        dailyExpenseRepository.save(dailyExpense);
    }

    @Override
    public void deleteAll(Integer[] ids) {
        List<DailyExpense> allById = dailyExpenseRepository.findAllById(Arrays.asList(ids));
        for (DailyExpense dailyExpense : allById) {
            dailyExpense.setDeleted(SystemConstant.DEL_FLAG_1);

        }
        dailyExpenseRepository.saveAll(allById);
    }

    @Override
    public void download(List<DailyExpenseDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> dictDetails = jdbcTemplate.queryForList("select dd.`value`, dd.label from sys_dict_detail dd join sys_dict d on dd.dict_id = d.dict_id where d.`name` = 'expense_category'");
        Map<String, String> dictMap = dictDetails.stream()
                .collect(Collectors.toMap(map -> (String) map.get("value"), map -> (String) map.get("label")));
        List<Map<String, Object>> list = new ArrayList<>();
        for (DailyExpenseDto dailyExpense : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("开销分类", dictMap.get(dailyExpense.getCategory()));
            map.put("开销金额", dailyExpense.getMoney());
            map.put("开销日期", dailyExpense.getDate());
            map.put("备注", dailyExpense.getRemark());
            map.put("创建人", dailyExpense.getCreateBy());
            map.put("更新人", dailyExpense.getUpdateBy());
            map.put("创建时间", dailyExpense.getCreateTime());
            map.put("更新时间", dailyExpense.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public Map<String, Object> report(String reportType, DailyExpenseQueryCriteria criteria, Integer pageNumber, Integer pageSize) {
        Date startDate = criteria.getDates().get(0);
        Date endDate = criteria.getDates().get(1);
        String resultSelect = String.join(" ", "daily".equals(reportType) ? "select date_ as `date`,"
                        : String.format("select '%s ~ %s' as `date`, ",
                        DateUtil.DFY_MD.format(startDate.toLocalDate()),
                        DateUtil.DFY_MD.format(endDate.toLocalDate())),
                "category as `category`,",
                "sum(money) as `money`");
        String from = String.join(" ", "from biz_daily_expense");
        StringBuilder where = new StringBuilder("where deleted = 0 and date_ between ? and ?");
        List<Object> params = Lists.newArrayList(startDate, endDate);
        if (StringUtils.isNotEmpty(criteria.getCategory())) {
            where.append(" and category = ?");
            params.add(criteria.getCategory());
        }
        StringBuilder group = new StringBuilder("group by category");
        StringBuilder order = new StringBuilder("order by category");
        if ("daily".equals(reportType)) {
            group.insert(9, "date_ desc, ");
            order.insert(9, "date_ desc, ");
        }
        String countSql = String.format("select count(*) from (%s) t", String.join(" ", resultSelect, from, where.toString(), group.toString()));
        Long total = jdbcTemplate.queryForObject(countSql, Long.class, params.toArray());
        String limit = "limit ?, ?";
        params.add((pageNumber - 1) * pageSize);
        params.add(pageSize);
        List<ExpenseReportDto> content = jdbcTemplate.query(String.join(" ", resultSelect, from, where.toString(), group, order, limit),
                ExpenseReportDto.ROW_MAPPER, params.toArray());
        return PageUtil.toPage(content, total);
    }
}