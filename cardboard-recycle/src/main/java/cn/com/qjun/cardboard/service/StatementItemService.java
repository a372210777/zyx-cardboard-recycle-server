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
package cn.com.qjun.cardboard.service;

import cn.com.qjun.cardboard.domain.StatementItem;
import cn.com.qjun.cardboard.service.dto.StatementItemDto;
import cn.com.qjun.cardboard.service.dto.StatementItemQueryCriteria;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务接口
* @author RenQiang
* @date 2022-06-18
**/
public interface StatementItemService {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(StatementItemQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<StatementItemDto>
    */
    List<StatementItemDto> queryAll(StatementItemQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return StatementItemDto
     */
    StatementItemDto findById(Integer id);

    /**
    * 创建
    * @param resources /
    * @return StatementItemDto
    */
    StatementItemDto create(StatementItem resources);

    /**
    * 编辑
    * @param resources /
    */
    void update(StatementItem resources);

    /**
    * 多选删除
    * @param ids /
    */
    void deleteAll(Integer[] ids);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<StatementItemDto> all, HttpServletResponse response) throws IOException;
}