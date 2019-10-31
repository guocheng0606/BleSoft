package com.android.hcbd.bledebug.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Custom implements Parcelable {

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeList(this.orders);
    }

    protected Custom(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.name = in.readString();
        this.orders = new ArrayList<Instructions>();
        in.readList(this.orders, Instructions.class.getClassLoader());
    }

    public static final Parcelable.Creator<Custom> CREATOR = new Parcelable.Creator<Custom>() {
        @Override
        public Custom createFromParcel(Parcel source) {
            return new Custom(source);
        }

        @Override
        public Custom[] newArray(int size) {
            return new Custom[size];
        }
    };
}
