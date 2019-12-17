package com.example.phanmemquanlynhansu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phanmemquanlynhansu.Adapter.AdapterThemPhanCaLam;
import com.example.phanmemquanlynhansu.Model.ModelCaLam;
import com.example.phanmemquanlynhansu.Model.ModelCaLamViec;
import com.example.phanmemquanlynhansu.Model.ModelNhanVien;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChiTietLichLamActivity extends AppCompatActivity {
    TextView txtCuaHang, txtNgay, txtCaLam, btnXoa;

    ListView lvChiTiet;

    String key;
    DatabaseReference mData;

    ArrayList<ModelNhanVien> listNhanVien;
    AdapterThemPhanCaLam adapter;
//    ArrayAdapter adapter;

    ModelCaLam modelCaLam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_lich_lam);
        getData();
        addControls();
        addEvents();
    }

    public void addControls() {
        txtCaLam = findViewById(R.id.txt_calam_chitietcl);
        txtCuaHang = findViewById(R.id.txt_cuahang_chitietcl);
        txtNgay = findViewById(R.id.txt_ngay_chitietcl);
        lvChiTiet = findViewById(R.id.lv_chitietcl);
        btnXoa = findViewById(R.id.btn_xoa_chitietcl);
    }
    public void addEvents(){
        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xoaLichLam(key);
            }
        });
    }

    public void getData() {
        Intent intent = getIntent();
        listNhanVien = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mData = FirebaseDatabase.getInstance().getReference("NhanVien");
            mData.orderByKey().equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        ModelNhanVien modelNhanVien = data.getValue(ModelNhanVien.class);
                        if (!modelNhanVien.getMaChucVu().equals("Quản lý")){
                            btnXoa.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        key = intent.getStringExtra("keylichlam");
        mData = FirebaseDatabase.getInstance().getReference("CaLamViec");
        mData.orderByKey().equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listNhanVien.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    ModelCaLamViec modelCaLamViec = data.getValue(ModelCaLamViec.class);
                    listNhanVien = modelCaLamViec.getListNhanVien();
                    modelCaLam = modelCaLamViec.getModelCaLam();
                    txtCaLam.setText(modelCaLam.getTenCaLam() + ": "+ modelCaLam.getTgBatDauCaLam()+" - "+modelCaLam.getTgKetThucCaLam());
                    txtCuaHang.setText(modelCaLamViec.getCuaHang());
                    txtNgay.setText(modelCaLamViec.getNgay());
                }
                adapter = new AdapterThemPhanCaLam(ChiTietLichLamActivity.this, listNhanVien);
                lvChiTiet.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                Log.e("sssss", listNhanVien + "");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void xoaLichLam(String key){
        mData = FirebaseDatabase.getInstance().getReference();
        mData.child("CaLamViec").child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ChiTietLichLamActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChiTietLichLamActivity.this, "Lỗi "+ e, Toast.LENGTH_SHORT).show();
            }
        });
    }
}