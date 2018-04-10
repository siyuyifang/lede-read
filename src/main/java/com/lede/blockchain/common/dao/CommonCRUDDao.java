package com.lede.blockchain.common.dao;

import com.lede.blockchain.common.pagination.PaginationInfo;
import com.lede.blockchain.common.pagination.PaginationList;

import java.util.List;
import java.util.Map;

public interface CommonCRUDDao<Entity> {
    /**
     * insert
     *
     * @param entity
     * @return 返回实体包含生成主键
     */
    Entity addEntity(Entity entity);

    /**
     * delete
     *
     * @param entity
     * @return 是否删除成功
     */
    boolean deleteEntity(Entity entity);

    /**
     * update
     *
     * @param entity
     * @return 是否更新成功
     */
    boolean updateEntity(Entity entity);

    /**
     * delete list
     *
     * @param entityList
     * @return 返回删除成功实体列表
     */
    List<Entity> deleteEntityList(List<Entity> entityList);

    /**
     * update list
     *
     * @param entityList
     * @return 返回更新成功实体列表
     */
    List<Entity> updateEntityList(List<Entity> entityList);

    /**
     * select by condition
     *
     * @param cond
     * @param paginationInfo
     * @return 分页实体列表
     */
    PaginationList<Entity> findEntityListByCond(Map<String, Object> cond,
                                                PaginationInfo paginationInfo);

    /**
     * select by id
     *
     * @param entity
     * @return 实体
     */
    Entity findEntityById(Entity entity);
}
