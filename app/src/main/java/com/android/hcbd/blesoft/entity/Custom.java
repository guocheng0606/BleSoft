package com.android.hcbd.blesoft.entity;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Custom implements Serializable {

    private static final long serialVersionUID = 4563098756272513013L;

    @Id(autoincrement = true)
    private Long id;
    private String name;
    @Convert(columnType = String.class, converter = ObjectConverter.class)
    private List<Instructions> orders;
    @Generated(hash = 1845522014)
    public Custom(Long id, String name, List<Instructions> orders) {
        this.id = id;
        this.name = name;
        this.orders = orders;
    }
    @Generated(hash = 62298964)
    public Custom() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<Instructions> getOrders() {
        return this.orders;
    }
    public void setOrders(List<Instructions> orders) {
        this.orders = orders;
    }


    

}
