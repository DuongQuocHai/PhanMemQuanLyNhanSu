package com.example.phanmemquanlynhansu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phanmemquanlynhansu.Function.Function;
import com.example.phanmemquanlynhansu.Function.NhanVienDAO;
import com.example.phanmemquanlynhansu.Model.ModelNhanVien;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.widget.Toast.LENGTH_SHORT;

public class SuaNhanVienActivity extends AppCompatActivity {
    ModelNhanVien modelNhanVien;
    CircleImageView imgAvatar, btnEditAvatar;
    EditText edtTen, edtSdt, edtDiaChi;
    Spinner spnChucVu, spnCuaHang;
    RadioButton rdNam, rdNu;
    Button btnDoiMk, btnLuu1;
    TextView btnXoa, btnLuu, edtUser, txtPass;
    ImageView btnBack;
    //dialog
    EditText edtOldPass, edtNewPass, edtRePass;
    Button btnHuy, btnLuudl;
    Function function;
    LinearLayout lyMain;

    NhanVienDAO nhanVienDAO;

    DatabaseReference mData;
    FirebaseStorage storage;
    StorageReference mountainsRef;
    StorageReference storageRef;
    FirebaseUser currentFirebaseUser;

    ProgressBar progressBar,progressBarMain;

    int REQUEST_CHOOSE_PHOTO = 321;

    String ten, user, pass, chucVu, cuaHang, gioiTinh, sdt, diaChi;

    Dialog dialog;
    Intent intent;
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sua_nhan_vien);
        addControls();
        addEvents();
        getData();
//        changePass();
//        delete();
    }

    public void addControls() {
        btnEditAvatar = findViewById(R.id.img_editimg_suanv);
        edtTen = findViewById(R.id.edt_ten_suanv);
        edtUser = findViewById(R.id.edt_tendn_suanv);
        edtSdt = findViewById(R.id.edt_sdt_suanv);
        edtDiaChi = findViewById(R.id.edt_diachi_suanv);

        btnXoa = findViewById(R.id.btn_xoa_suanv);

        imgAvatar = findViewById(R.id.img_suanv);
        spnChucVu = findViewById(R.id.spn_chucvu_suanv);
        spnCuaHang = findViewById(R.id.spn_chinhanh_suanv);
        rdNam = findViewById(R.id.rd_nam_suanv);
        rdNu = findViewById(R.id.rd_nu_suanv);
        btnDoiMk = findViewById(R.id.btn_doimk_suanv);
        progressBarMain = findViewById(R.id.pr_suanv);
        lyMain = findViewById(R.id.ly_main_suanv);
    }

    public void addEvents() {
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_sua_nhanvien);
        View view = getSupportActionBar().getCustomView();
        btnLuu = view.findViewById(R.id.action_bar_sua_nhanvien);
        btnBack = view.findViewById(R.id.action_bar_back_sua_nhanvien);

        rdNam.setOnCheckedChangeListener(listenerRadio);
        rdNu.setOnCheckedChangeListener(listenerRadio);
        btnEditAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImg();
            }
        });
        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (batLoi()) {
                    if (status != null) {
                        suaNhanVien(currentFirebaseUser.getUid());
                    } else
                        suaNhanVien(modelNhanVien.getIdNv());
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xacNhanXoa();
            }
        });
        btnDoiMk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDoiMatKhau();
            }
        });

    }

    CompoundButton.OnCheckedChangeListener listenerRadio = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                gioiTinh = (String) buttonView.getText();
            }
        }
    };

    public boolean batLoi() {
        if (edtTen.getText().length() == 0) {
            Toast.makeText(this, "Vui lòng nhập tên!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (edtUser.getText().length() == 0) {
            Toast.makeText(this, "Vui lòng nhập tên đăng nhập!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void getString() {
        ten = edtTen.getText().toString();
        user = edtUser.getText().toString();
        chucVu = spnChucVu.getSelectedItem().toString();
        cuaHang = spnCuaHang.getSelectedItem().toString();
        sdt = edtSdt.getText().toString();
        diaChi = edtDiaChi.getText().toString();
    }

    public void getData() {
        lyMain.setVisibility(View.GONE);
        progressBarMain.setVisibility(View.VISIBLE);
        intent = getIntent();
        nhanVienDAO = new NhanVienDAO();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        status = intent.getStringExtra("userfrag");
        if (status != null) {
            mData = FirebaseDatabase.getInstance().getReference("NhanVien");
            mData.orderByKey().equalTo(currentFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        modelNhanVien = data.getValue(ModelNhanVien.class);
                        Picasso.get().load(modelNhanVien.getUrlHinhNv()).into(imgAvatar);
                        nhanVienDAO.ganDsChucVuVaoSpiner(SuaNhanVienActivity.this, spnChucVu, modelNhanVien.getMaChucVu());
                        nhanVienDAO.ganDsCuaHangVaoSpiner(SuaNhanVienActivity.this, spnCuaHang, modelNhanVien.getMaCuaHang());
                        gioiTinh = modelNhanVien.getGioiTinhNv();
                        if (gioiTinh.equals("Nam")) {
                            rdNam.setChecked(true);
                        } else {
                            rdNu.setChecked(true);
                        }
                        edtTen.setText(modelNhanVien.getTenNv());
                        edtUser.setText(modelNhanVien.getUserNv());
                        edtSdt.setText(modelNhanVien.getSdtNv());
                        edtDiaChi.setText(modelNhanVien.getDiaChiNv());
                        lyMain.setVisibility(View.VISIBLE);
                        progressBarMain.setVisibility(View.GONE);
                        if (!modelNhanVien.getMaChucVu().equals("Quản lý") || modelNhanVien.getUserNv().equals(currentFirebaseUser.getEmail())) {
                            btnXoa.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            modelNhanVien = (ModelNhanVien) intent.getSerializableExtra("ModelNhanVien");
            Picasso.get().load(modelNhanVien.getUrlHinhNv()).into(imgAvatar);
            nhanVienDAO.ganDsChucVuVaoSpiner(SuaNhanVienActivity.this, spnChucVu, modelNhanVien.getMaChucVu());
            nhanVienDAO.ganDsCuaHangVaoSpiner(SuaNhanVienActivity.this, spnCuaHang, modelNhanVien.getMaCuaHang());
            gioiTinh = modelNhanVien.getGioiTinhNv();
            if (gioiTinh.equals("Nam")) {
                rdNam.setChecked(true);
            } else {
                rdNu.setChecked(true);
            }
            edtTen.setText(modelNhanVien.getTenNv());
            edtUser.setText(modelNhanVien.getUserNv());
            edtSdt.setText(modelNhanVien.getSdtNv());
            edtDiaChi.setText(modelNhanVien.getDiaChiNv());
            lyMain.setVisibility(View.VISIBLE);
            progressBarMain.setVisibility(View.GONE);
            if (currentFirebaseUser != null) {
                if (!modelNhanVien.getIdNv().equals(currentFirebaseUser.getUid())) {
                    btnDoiMk.setVisibility(View.GONE);
                }
                if (modelNhanVien.getUserNv().equals(currentFirebaseUser.getEmail())) {
                    btnXoa.setVisibility(View.GONE);
                }
                mData = FirebaseDatabase.getInstance().getReference("NhanVien");
                mData.orderByKey().equalTo(currentFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            ModelNhanVien modelNhanVien = data.getValue(ModelNhanVien.class);
                            if (!modelNhanVien.getMaChucVu().equals("Quản lý")) {
                                btnXoa.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    private void suaNhanVien(final String uid) {
        getString();
        function = new Function();
        function.pDialog(SuaNhanVienActivity.this);
        mData = FirebaseDatabase.getInstance().getReference("NhanVien");
//        final String uid = modelNhanVien.getIdNv();
        storage = FirebaseStorage.getInstance("gs://phanmemquanlynhansu-eda6c.appspot.com");
        storageRef = storage.getReference();
        Calendar calendar = Calendar.getInstance();
        mountainsRef = storageRef.child("image" + calendar.getTimeInMillis() + ".png");
        // Get the data from an ImageView as bytes
        imgAvatar.setDrawingCacheEnabled(true);
        imgAvatar.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imgAvatar.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(SuaNhanVienActivity.this, "Lỗi", LENGTH_SHORT).show();
                function.pDialog(SuaNhanVienActivity.this);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                Uri downloadUrl = urlTask.getResult();
                mData.child(uid).child("diaChiNv").setValue(diaChi);
                mData.child(uid).child("gioiTinhNv").setValue(gioiTinh);
                mData.child(uid).child("maChucVu").setValue(chucVu);
                mData.child(uid).child("maCuaHang").setValue(cuaHang);
                mData.child(uid).child("sdtNv").setValue(sdt);
                mData.child(uid).child("tenNv").setValue(ten);
                mData.child(uid).child("urlHinhNv").setValue(String.valueOf(downloadUrl));
                mData.child(uid).child("userNv").setValue(user);
                Toast.makeText(SuaNhanVienActivity.this, "Lưu thành công", LENGTH_SHORT).show();
                function.pDialog(SuaNhanVienActivity.this);
                finish();
            }
        });
    }

    private void xacNhanXoa() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có muốn xoá?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                xoaNhanVien();
            }
        }).setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private void xoaNhanVien() {
        DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
        mData.child("NhanVien").child(modelNhanVien.getIdNv()).removeValue();
        finish();
    }

    private void dialogDoiMatKhau() {
        dialog = new Dialog(SuaNhanVienActivity.this);
        dialog.setContentView(R.layout.dialog_doimatkhau);
        edtOldPass = dialog.findViewById(R.id.edt_mkcu_dldoimk);
        edtNewPass = dialog.findViewById(R.id.edt_mkmoi_dldoimk);
        edtRePass = dialog.findViewById(R.id.edt_nhaplaimk_dldoimk);
        btnHuy = dialog.findViewById(R.id.btn_huy_dldoimk);
        btnLuu1 = dialog.findViewById(R.id.btn_luu_dldoimk);
        progressBar = dialog.findViewById(R.id.pr_dldoimk);

        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnLuu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doiMatKhau();
            }
        });
        dialog.show();
    }

    private void doiMatKhau() {
        getString();

        String pass = edtOldPass.getText().toString();
        final String newpass = edtNewPass.getText().toString();
        String repass = edtRePass.getText().toString();
        final String uid = currentFirebaseUser.getUid();
        if (pass.trim().length() != 0 || newpass.trim().length() != 0 || repass.trim().length() != 0) {
            if (pass.equals(modelNhanVien.getPassNv())) {
                if (newpass.equals(repass)) {
                    progressBar.setVisibility(View.VISIBLE);
                    final FirebaseUser userF = FirebaseAuth.getInstance().getCurrentUser();
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(user, pass);
                    userF.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        userF.updatePassword(newpass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    mData = FirebaseDatabase.getInstance().getReference("NhanVien");
                                                    mData.child(uid).child("passNv").setValue(newpass, new DatabaseReference.CompletionListener() {
                                                        @Override
                                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                            if (databaseError == null) {
                                                                Toast.makeText(SuaNhanVienActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                                                progressBar.setVisibility(View.GONE);
                                                                dialog.dismiss();
                                                            } else
                                                                Toast.makeText(SuaNhanVienActivity.this, "Lỗi " + databaseError, Toast.LENGTH_SHORT).show();
                                                            progressBar.setVisibility(View.GONE);
                                                        }
                                                    });
                                                } else {
                                                    Toast.makeText(SuaNhanVienActivity.this, "Error password not updated", LENGTH_SHORT).show();
                                                    progressBar.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(SuaNhanVienActivity.this, "Error auth failed", LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                } else Toast.makeText(this, "Mật khẩu không trùng nhau", LENGTH_SHORT).show();
            } else Toast.makeText(this, "Sai mật khẩu", LENGTH_SHORT).show();
        } else Toast.makeText(this, "Hãy nhập đầy đủ các trường!", LENGTH_SHORT).show();
    }

    public void delete() {
        getString();
        final FirebaseUser userF = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(user, pass);

        userF.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            userF.delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SuaNhanVienActivity.this, "User account deleted.", LENGTH_SHORT).show();
                                                xoaNhanVien();
                                            } else {
                                                Toast.makeText(SuaNhanVienActivity.this, "Lỗi", LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(SuaNhanVienActivity.this, "Error auth failed", LENGTH_SHORT).show();
                        }

                    }
                });
    }


    private void chooseImg() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CHOOSE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHOOSE_PHOTO && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgAvatar.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
