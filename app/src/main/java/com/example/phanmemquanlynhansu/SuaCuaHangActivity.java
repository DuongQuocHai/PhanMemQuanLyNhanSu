package com.example.phanmemquanlynhansu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.phanmemquanlynhansu.Model.ModelCuaHang;
import com.example.phanmemquanlynhansu.Model.ModelNhanVien;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;

public class SuaCuaHangActivity extends AppCompatActivity {
    Button btnEdit, btnHuy;
    EditText edtEditMaCH, edtEditTenCH, edtEditDiaChi;
    ModelCuaHang modelCuaHang;
    String maCH, tenCH, diaChi, keyId;
    ArrayList<ModelCuaHang> list;
    DatabaseReference mData;

    String tench;
    boolean check = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sua_cua_hang);
        addControls();
        addEvents();
        getData();
        checkCuaHang();


    }

    private void addEvents() {
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_sua_cuahang);
        View view = getSupportActionBar().getCustomView();
    }

    private void addControls() {
        edtEditMaCH = findViewById(R.id.edt_macuahang_edit);
        edtEditTenCH = findViewById(R.id.edt_tencuahang_edit);
        edtEditDiaChi = findViewById(R.id.edt_diachi_edit);

    }

    private void getData() {
        Intent intent = getIntent();
        modelCuaHang = (ModelCuaHang) intent.getSerializableExtra("ModelCuaHang");
        keyId = modelCuaHang.getId();
        edtEditMaCH.setText(modelCuaHang.getMaCuaHang());
        edtEditTenCH.setText(modelCuaHang.getTenCuaHang());
        edtEditDiaChi.setText(modelCuaHang.getDiaChi());
    }

    public void clickSuaCuaHang(View view) throws ParseException {
        switch (view.getId()) {
            case R.id.action_bar_sua_cuahang:
                if (batLoi()) {
                    editCuaHang(keyId);
                }
                break;
            case R.id.action_bar_back_sua_cuahang:
                finish();
                break;
            case R.id.txt_xoa_cuahang:
                xacNhanXoa();
        }
    }

    public void checkCuaHang() {
        mData = FirebaseDatabase.getInstance().getReference();
        mData.child("NhanVien").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        ModelNhanVien modelNhanVien = data.getValue(ModelNhanVien.class);
                        tench = modelNhanVien.getMaCuaHang();
                        if (tench.equals(modelCuaHang.getTenCuaHang())) {
                            check = false;
                            break;
                        }
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void xacNhanXoa() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có muốn xoá?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCuaHang(keyId);
            }
        }).setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private boolean batLoi() {
        if (edtEditMaCH.getText().length() == 0) {
            Toast.makeText(this, "Vui lòng nhập mã cửa hàng!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (edtEditTenCH.getText().length() == 0) {
            Toast.makeText(this, "Vui lòng nhập tên cửa hàng", Toast.LENGTH_SHORT).show();
            return false;
        } else if (edtEditDiaChi.getText().length() == 0) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void deleteCuaHang(String keyId) {
        if (check){
            mData = FirebaseDatabase.getInstance().getReference();
            mData.child("CuaHang").child(keyId).removeValue();
            finish();
        }else Toast.makeText(this, "Hãy xóa nhân viên trước khi xóa cửa hàng", Toast.LENGTH_LONG).show();
    }

    private void getString() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        maCH = edtEditMaCH.getText().toString();
        tenCH = edtEditTenCH.getText().toString();
        diaChi = edtEditDiaChi.getText().toString();
        keyId = (String) bundle.get("KeyID");
//        keyId = modelCuaHang.getId();
    }

    private void editCuaHang(String uid) {
        mData = FirebaseDatabase.getInstance().getReference();
        getString();
        modelCuaHang = new ModelCuaHang(maCH, tenCH, diaChi);
        mData.child("CuaHang").child(uid).setValue(modelCuaHang).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(SuaCuaHangActivity.this, "Lưu thành công", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SuaCuaHangActivity.this, "Lưu thất bại", Toast.LENGTH_SHORT).show();

            }
        });
    }


}
