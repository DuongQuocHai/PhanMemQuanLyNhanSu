package com.example.phanmemquanlynhansu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phanmemquanlynhansu.Adapter.AdapterCaLam;
import com.example.phanmemquanlynhansu.Adapter.AdapterNhanVien;
import com.example.phanmemquanlynhansu.Fragment.FragmentThem;
import com.example.phanmemquanlynhansu.Model.ModelCuaHang;
import com.example.phanmemquanlynhansu.Model.ModelNhanVien;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NhanVienActivity extends AppCompatActivity {
    ListView lvNhanVien;
    ImageView btnBack, btnThem;
    ArrayList<ModelNhanVien> list;
    AdapterNhanVien adapterNhanVien;
    DatabaseReference mData;
    ImageView ivLoading, btnSearch, btnRefresh;
    AnimationDrawable animation;
    EditText edtSearch;
    Button btnTenNv, btnChucVuNv, btnCuaHangNV;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhan_vien);
        addControls();
        intent = getIntent();
        String cuahang12 = intent.getStringExtra("cuahang");
        if (cuahang12 != null) {
            thucHienSearch("maCuaHang", cuahang12);
            Log.e("llllll",cuahang12);
        } else {
            readData();
        }
        addEvents();


    }

    public void addControls() {
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_nhan_vien);
        View view = getSupportActionBar().getCustomView();
        btnBack = view.findViewById(R.id.btn_back_nhanvien);
        btnThem = view.findViewById(R.id.btn_them_nhanvien);
        btnSearch = findViewById(R.id.btn_search_nhanvien);
        btnRefresh = findViewById(R.id.btn_refresh_nhanvien);
        btnTenNv = findViewById(R.id.btn_ten_nhanvien);
        btnChucVuNv = findViewById(R.id.btn_chucvu_nhanvien);
        btnCuaHangNV = findViewById(R.id.btn_chinhanh_nhanvien);

        edtSearch = findViewById(R.id.search_nhan_vien);

        ivLoading = findViewById(R.id.iv_loading);
        ivLoading.setBackgroundResource(R.drawable.loading);
        animation = (AnimationDrawable) ivLoading.getBackground();
        animation.start();

        lvNhanVien = (ListView) findViewById(R.id.lv_nhanvien);
        list = new ArrayList<>();
        adapterNhanVien = new AdapterNhanVien(NhanVienActivity.this, list);
        lvNhanVien.setAdapter(adapterNhanVien);

    }

    public void addEvents() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NhanVienActivity.this, ThemNhanVienActivity.class);
                startActivity(intent);
            }
        });
        lvNhanVien.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = getIntent();
                String status = intent.getStringExtra("fragThem");
                if (status != null) {
                    intent = new Intent(NhanVienActivity.this, BangLuongActivity.class);
                    intent.putExtra("ModelNhanVien", list.get(position));
                    startActivity(intent);
                } else {
                    intent = new Intent(NhanVienActivity.this, SuaNhanVienActivity.class);
                    intent.putExtra("ModelNhanVien", list.get(position));
                    startActivity(intent);
                }
            }
        });
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readData();
                edtSearch.setText("");
                btnTenNv.setTextColor(Color.parseColor("#bdc3c7"));
                btnCuaHangNV.setTextColor(Color.parseColor("#bdc3c7"));
                btnChucVuNv.setTextColor(Color.parseColor("#bdc3c7"));
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogSearch();
            }
        });
    }

    public void readData() {

            mData = FirebaseDatabase.getInstance().getReference("NhanVien");
            mData.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    list.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        ModelNhanVien modelNhanVien = data.getValue(ModelNhanVien.class);
                        modelNhanVien.setIdNv(data.getKey());
                        list.add(modelNhanVien);
                    }
                    if (list.size() == 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(NhanVienActivity.this);
                        builder.setIcon(R.drawable.iconnotification);
                        builder.setCancelable(false);
                        builder.setTitle("Thông báo");
                        builder.setMessage("Hiện tại chưa có nhân viên, hãy thêm nhân viên");
                        builder.setPositiveButton("Thêm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(NhanVienActivity.this, ThemNhanVienActivity.class);
                                startActivity(intent);
                            }
                        });
                        builder.setNegativeButton("Bỏ qua", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
                        builder.show();
                    }
                    adapterNhanVien.notifyDataSetChanged();
                    ivLoading.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

    }

    public void showDialogSearch() {
        final Dialog dialog = new Dialog(NhanVienActivity.this);
        dialog.setContentView(R.layout.dialog_chonmucsearch);

        Button btnTen = dialog.findViewById(R.id.btn_ten_dlsearch);
        Button btnCuaHang = dialog.findViewById(R.id.btn_cuahang_dlsearch);
        Button btnChucvu = dialog.findViewById(R.id.btn_chucvu_dlsearch);
        btnTen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thucHienSearch("tenNv", edtSearch.getText().toString());
                btnTenNv.setTextColor(Color.parseColor("#6B6B6B"));
                btnCuaHangNV.setTextColor(Color.parseColor("#bdc3c7"));
                btnChucVuNv.setTextColor(Color.parseColor("#bdc3c7"));
                dialog.dismiss();
            }
        });
        btnCuaHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thucHienSearch("maCuaHang", edtSearch.getText().toString());
                btnCuaHangNV.setTextColor(Color.parseColor("#6B6B6B"));
                btnTenNv.setTextColor(Color.parseColor("#bdc3c7"));
                btnChucVuNv.setTextColor(Color.parseColor("#bdc3c7"));
                dialog.dismiss();
            }
        });
        btnChucvu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thucHienSearch("maChucVu", edtSearch.getText().toString());
                btnChucVuNv.setTextColor(Color.parseColor("#6B6B6B"));
                btnTenNv.setTextColor(Color.parseColor("#bdc3c7"));
                btnCuaHangNV.setTextColor(Color.parseColor("#bdc3c7"));
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void thucHienSearch(String child, String value) {
        mData = FirebaseDatabase.getInstance().getReference("NhanVien");
        mData.orderByChild(child).startAt(value).endAt(value + "\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    ModelNhanVien modelNhanVien = data.getValue(ModelNhanVien.class);
                    modelNhanVien.setIdNv(data.getKey());
                    list.add(modelNhanVien);
                }
                if (list.size() == 0) {

                }
                adapterNhanVien.notifyDataSetChanged();
                ivLoading.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
