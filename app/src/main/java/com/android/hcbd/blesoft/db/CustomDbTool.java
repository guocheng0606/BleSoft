package com.android.hcbd.blesoft.db;

import com.android.hcbd.blesoft.entity.Custom;
import com.android.hcbd.blesoft.entity.CustomDao;
import com.android.hcbd.blesoft.manager.GreenDaoManager;

import java.util.List;

/**
 * Created by guocheng on 2019/1/8.
 */

public class CustomDbTool {

    //添加一个
    public static void add(Custom bean){
        CustomDao dao = GreenDaoManager.getInstance().getSession().getCustomDao();
        dao.insert(bean);
    }

    //批量添加
    public static void addList(List<Custom> list){
        CustomDao dao = GreenDaoManager.getInstance().getSession().getCustomDao();
        dao.insertInTx(list);
    }

    //根据实体删除
    public static void delete(Custom bean){
        CustomDao dao = GreenDaoManager.getInstance().getSession().getCustomDao();
        dao.delete(bean);
    }

    //根据实体批量删除
    public static void deleteList(List<Custom> list){
        CustomDao dao = GreenDaoManager.getInstance().getSession().getCustomDao();
        dao.deleteInTx(list);
    }

    //根据id删除
    public static void deleteById(Long id){
        CustomDao dao = GreenDaoManager.getInstance().getSession().getCustomDao();
        dao.deleteByKey(id);
    }

    //根据id批量删除
    public static void deleteListById(List<Long> list){
        CustomDao dao = GreenDaoManager.getInstance().getSession().getCustomDao();
        dao.deleteByKeyInTx(list);
    }

    //删除全部
    public static void deleteAll(){
        CustomDao dao = GreenDaoManager.getInstance().getSession().getCustomDao();
        dao.deleteAll();
    }

    //修改一个
    public static void update(Custom bean){
        CustomDao dao = GreenDaoManager.getInstance().getSession().getCustomDao();
        dao.update(bean);
    }

    //批量修改
    public static void updateList(List<Custom> list){
        CustomDao dao = GreenDaoManager.getInstance().getSession().getCustomDao();
        dao.updateInTx(list);
    }

    //査询全部
    public static List<Custom> queryAll(){
        CustomDao dao = GreenDaoManager.getInstance().getSession().getCustomDao();
        return dao.loadAll();
    }

    //通过id査询
    public static Custom queryById(Long id){
        CustomDao dao = GreenDaoManager.getInstance().getSession().getCustomDao();
        return dao.load(id);
    }


}
