package com.lede.blockchain.common.service.impl;

import com.lede.blockchain.common.dao.CommonCRUDDao;
import com.lede.blockchain.common.pagination.PaginationInfo;
import com.lede.blockchain.common.pagination.PaginationList;
import com.lede.blockchain.common.service.CommonCRUDService;

import java.util.List;
import java.util.Map;


public abstract class BaseCRUDServiceImpl<Entity> implements CommonCRUDService<Entity> {

    @Override
    public boolean deleteEntity(Entity entity) {
        return this.getDao().deleteEntity(entity);
    }

    @Override
    public List<Entity> deleteEntityList(List<Entity> entityList) {
        return this.getDao().deleteEntityList(entityList);
    }

    @Override
    public Entity addEntity(Entity entity) {
        return this.getDao().addEntity(entity);
    }

    @Override
    public Entity findEntityById(Entity entity) {
        return this.getDao().findEntityById(entity);
    }

    @Override
    public PaginationList<Entity> findEntityListByCond(Map<String, Object> cond, PaginationInfo paginationInfo) {
        return this.getDao().findEntityListByCond(cond, paginationInfo);
    }

    @Override
    public boolean updateEntity(Entity entity) {
        return this.getDao().updateEntity(entity);
    }

    @Override
    public List<Entity> updateEntityList(List<Entity> entityList) {
        return this.getDao().updateEntityList(entityList);
    }

    protected abstract CommonCRUDDao<Entity> getDao();

}
