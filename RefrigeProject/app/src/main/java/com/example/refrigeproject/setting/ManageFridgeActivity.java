package com.example.refrigeproject.setting;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.refrigeproject.DBHelper;
import com.example.refrigeproject.R;
import com.example.refrigeproject.show_foods.RefrigeratorData;
import com.example.refrigeproject.show_foods.ShowFoodsFragment;
import com.r0adkll.slidr.Slidr;

import org.aviran.cookiebar2.CookieBar;
import org.aviran.cookiebar2.OnActionClickListener;

public class ManageFridgeActivity extends AppCompatActivity {
    private static final String TAG = "ManageFridgeActivity";
    private ListView lvFridgeList;
    private ListViewAdapter adapter;

    private DBHelper fridgeDBHelper;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_fridge);
        Slidr.attach(this);

        fridgeDBHelper = new DBHelper(this);

        lvFridgeList = findViewById(R.id.lvFridgeList);
        adapter = new ListViewAdapter();
        lvFridgeList.setAdapter(adapter);

    }

    private class ListViewAdapter extends BaseAdapter{
        LinearLayout llFridgeItem;
        ImageView ivFridge;
        TextView tvName, tvCode;

        @Override
        public int getCount() {
            return ShowFoodsFragment.refrigeratorList.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if(position == ShowFoodsFragment.refrigeratorList.size() + 1) return "냉장고 추가하기";

            return ShowFoodsFragment.refrigeratorList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {

            if (convertView == null){
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fridge_list_item, null);
            }

            llFridgeItem = convertView.findViewById(R.id.llFridgeItem);
            ivFridge = convertView.findViewById(R.id.ivFridge);
            tvName = convertView.findViewById(R.id.tvName);
            tvCode = convertView.findViewById(R.id.tvCode);

            if(ShowFoodsFragment.refrigeratorList.size() == position){
                // 리스트뷰의 마지막 아이템일 경우
                // 냉장고 추가 기능
                Log.d("log", position+" / "+ ShowFoodsFragment.refrigeratorList.size());
                ivFridge.setImageResource(R.drawable.add_box);
                tvName.setText("냉장고 추가하기");
                tvCode.setVisibility(View.GONE);

                llFridgeItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final CookieBar.Builder build = CookieBar.build(ManageFridgeActivity.this);
                        build.setCustomView(R.layout.cookiebar_add_fridge);
                        build.setCustomViewInitializer(new CookieBar.CustomViewInitializer() {
                            @Override
                            public void initView(View view) {
                                TextView tvNew = view.findViewById(R.id.tvNew);
                                TextView tvExisting = view.findViewById(R.id.tvExisting);

                                // 새로운 냉장고 추가하기
                                tvNew.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(ManageFridgeActivity.this, AddFridgeActivity.class);
                                        startActivity(intent);
                                        CookieBar.dismiss(ManageFridgeActivity.this);

                                    }
                                });

                                // 기존 냉장고 불러오기
                                tvExisting.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        View dialogView = View.inflate(v.getContext(), R.layout.add_existing_fridge, null);
                                        final EditText edtCode = dialogView.findViewById(R.id.edtCode);

                                        AlertDialog.Builder dialog = new AlertDialog.Builder(ManageFridgeActivity.this);
                                        dialog.setView(dialogView)
                                                .setNeutralButton("등록", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
//                                                        // 코드로 냉장고 등록
//                                                        sqLiteDatabase = fridgeDBHelper.getWritableDatabase();
//                                                        String sql = "SELECT * FROM refrigeratorTBL WHERE code = '" +
//                                                                edtCode.getText().toString().trim() + "';";
//                                                        sqLiteDatabase.execSQL(sql);
//                                                        sqLiteDatabase.close();

                                                        // 잘못된 코드일 경우

                                                        // 수정 확인
                                                        simpleCookieBar("냉장고를 성공적으로 불러왔습니다.");

                                                        // 데이터 변경 알림
//                                                        ShowFoodsFragment.refrigeratorList.add(?);
//                                                        adapter.notifyDataSetChanged();
                                                    }
                                                });

                                        dialog.show();
                                        CookieBar.dismiss(ManageFridgeActivity.this);
                                    }
                                });
                            }
                        });

                        build.setTitle("냉장고 추가하기");
                        build.setSwipeToDismiss(true);
                        build.setEnableAutoDismiss(true);
                        build.setDuration(5000);
                        build.setCookiePosition(CookieBar.BOTTOM);
                        build.show();

                    }
                });
                Log.d("log", tvName.getText().toString());
            } else {
                // 해당 냉장고 아이템
                final RefrigeratorData ref = ShowFoodsFragment.refrigeratorList.get(position);

                ivFridge.setImageResource(ref.getImgResource());
                tvName.setText(ref.getName());
                tvCode.setVisibility(View.VISIBLE);
                tvCode.setText(ref.getCode()); // ArrayList 타입 다시 정의하기!

                llFridgeItem.setOnClickListener(null);
                llFridgeItem.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        CookieBar.dismiss(ManageFridgeActivity.this);
                        Log.d(TAG, ref.getName());

                        View dialogView = View.inflate(v.getContext(), R.layout.edit_fridge_info, null);
                        final EditText edtName = dialogView.findViewById(R.id.edtName);
                        TextView dTextCode = dialogView.findViewById(R.id.tvCode);

                        edtName.setText(ref.getName());
                        dTextCode.setText(ref.getCode());

                        AlertDialog.Builder dialog = new AlertDialog.Builder(ManageFridgeActivity.this);
                        dialog.setView(dialogView)
                                .setTitle("냉장고 정보")
                                .setNeutralButton("수정", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // DB 수정
                                        sqLiteDatabase = fridgeDBHelper.getWritableDatabase();
                                        String sql = "UPDATE refrigeratorTBL SET name = '" +
                                                edtName.getText().toString().trim() +
                                                "' WHERE code = '" +
                                                ref.getCode() + "';";
                                        sqLiteDatabase.execSQL(sql);
                                        sqLiteDatabase.close();

                                        // 수정 확인
                                        simpleCookieBar(edtName.getText().toString().trim() + "(으)로 변경되었습니다.");
                                    }
                                })
                                .setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 확인 받기
                                        AlertDialog.Builder delete = new AlertDialog.Builder(ManageFridgeActivity.this);
                                        delete.setCancelable(false)
                                                .setTitle(ref.getName() + "(을)를 정말로 삭제하시겠어요?")
                                                .setMessage("냉장고에 포함된 음식도 삭제됩니다")
                                                .setNeutralButton("삭제하기", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // DB 삭제
                                                        sqLiteDatabase = fridgeDBHelper.getWritableDatabase();
                                                        String sql = "DELETE FROM refrigeratorTBL WHERE code = '" +
                                                                ref.getCode() + "';";
                                                        sqLiteDatabase.execSQL(sql);
                                                        sqLiteDatabase.close();

                                                        // 삭제 확인
                                                        simpleCookieBar(ref.getName() + "(이)가 삭제되었습니다.");

                                                        // 데이터 변경 알림
                                                        ShowFoodsFragment.refrigeratorList.remove(ref);
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                })
                                                .setNegativeButton("취소", null)
                                                .show();
                                    }
                                })
                                .show();
                        return false;
                    }
                });

            }

            return convertView;
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "sdf");
        // 추가가 되었으면
        adapter.notifyDataSetChanged();
        simpleCookieBar("냉장고가 추가되었습니다");
    }

    public void simpleCookieBar(String message){
        CookieBar.build(ManageFridgeActivity.this)
                .setTitle(message)
                .setSwipeToDismiss(true)
                .setEnableAutoDismiss(true)
                .setDuration(2000)
                .setCookiePosition(CookieBar.BOTTOM)
                .show();
    }
}
