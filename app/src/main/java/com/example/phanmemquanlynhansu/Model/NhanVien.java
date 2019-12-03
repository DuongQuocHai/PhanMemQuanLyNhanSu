package com.example.phanmemquanlynhansu.Model;

public class NhanVien {
    private String name;
    private String  chucVu;
    private String  chiNhanh;

    public NhanVien(String name, String chucVu, String chiNhanh) {
        this.name = name;
        this.chucVu = chucVu;
        this.chiNhanh = chiNhanh;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChucVu() {
        return chucVu;
    }

    public void setChucVu(String chucVu) {
        this.chucVu = chucVu;
    }

    public String getChiNhanh() {
        return chiNhanh;
    }

    public void setChiNhanh(String chiNhanh) {
        this.chiNhanh = chiNhanh;
    }
    public String toString() {
        return "NhanVien{" +
                "name='" + name + '\'' +
                ", chucVu='" + chucVu + '\'' +
                ", chiNhanh='" + chiNhanh + '\'' +
                '}';
    }
}
