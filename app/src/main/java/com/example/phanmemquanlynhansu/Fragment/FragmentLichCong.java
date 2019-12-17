package com.example.phanmemquanlynhansu.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.phanmemquanlynhansu.CaLamActivity;
import com.example.phanmemquanlynhansu.ChiTietLichLamActivity;
import com.example.phanmemquanlynhansu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentLichCong extends Fragment {
    View view;
    ArrayList<String> list;
    ArrayAdapter adapter;

    ListView lvLichLam;

    DatabaseReference mData;
    Button btnRefresh;
    String uidNv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_lichcong, container, false);
        addControls();
        readData();
        addEvents();

        return view;
    }

    public void addControls() {
        lvLichLam = view.findViewById(R.id.lv_fraglichlam);
        btnRefresh = view.findViewById(R.id.btn_refresh_fraglichlam);

    }
    public void addEvents() {
            lvLichLam.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getContext(), ChiTietLichLamActivity.class);
                    intent.putExtra("keylichlam",list.get(position));
                    startActivity(intent);
                }
            });
            btnRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    readData();
                }
            });
    }
    public void readData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            list = new ArrayList<>();
            getListLichLam(user.getUid());
        }
    }

    public void getListLichLam(final String idUser) {
        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, list);
        lvLichLam.setAdapter(adapter);
        mData = FirebaseDatabase.getInstance().getReference("CaLamViec");
        mData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (final DataSnapshot data : dataSnapshot.getChildren()) {
                    mData.child(data.getKey()).child("listNhanVien")
                            .orderByChild("idNv")
                            .equalTo(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                list.add(data.getKey());
                                Log.e("dataaaaa",dataSnapshot1+"");
                                Log.e("dataaaaa---",list.size()+"");
                            }
                            adapter.notifyDataSetChanged();
                            Log.e("dataaaaa-----",list.size()+"");

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
