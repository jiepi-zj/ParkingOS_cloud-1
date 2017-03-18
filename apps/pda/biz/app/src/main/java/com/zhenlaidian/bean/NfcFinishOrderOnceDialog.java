package com.zhenlaidian.bean;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.zhenlaidian.R;
import com.zhenlaidian.engine.ShowNfcFinishOrder;
import com.zhenlaidian.photo.InputCarNumberActivity;
import com.zhenlaidian.util.CheckUtils;
import com.zhenlaidian.util.MyLog;

public class NfcFinishOrderOnceDialog extends Dialog {
    ShowNfcFinishOrder finishOrder;
    private TextView tv_carnumber_warn;// 提示车主有几张车牌
    private TextView tv_carnumber_hint;// 提示切换车牌
    TextView tv_carnumber_on_carnumber;
    TextView tv_is_user;
    LinearLayout ll_write_carnumber;
    private Context context;
    private NfcOrder nfcOrder;
    private TextView tv_cash_type;// 结算的价格类型；
    private TextView tv_final_time_money;// 按时段计费不可修改；
    private Spinner sp_once_money;// 按次多价格列表；
    private EditText et_time_money;// 按时段价格可修改；
    private TextView tv_time;
    private LinearLayout ll_is_user;
    private TextView tv_duration;
    private TextView tv_carnumber;
    private Button bt_cancle;
    private Button bt_ok;
    private View view;
    private RelativeLayout rl_timecash;
    private RelativeLayout rl_oncecash;
    private CheckBox cb_once_cash;
    private CheckBox cb_time_cash;
    private String final_money;
    private SharedPreferences cashmode_sp;// 结算方式
    private ArrayAdapter<String> collectAdapter;
    private String[] collect1;// 按次多价格数据；
    private int cPosition = 0;// 记录选择多车牌位置

    public NfcFinishOrderOnceDialog(Context context) {
        super(context);
    }

    public NfcFinishOrderOnceDialog(Context context, int theme, ShowNfcFinishOrder finishOrder, NfcOrder nfcOrder) {
        super(context, theme);
        this.context = context;
        this.finishOrder = finishOrder;
        this.nfcOrder = nfcOrder;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.nfc_finish_order_once_dialog);
        cashmode_sp = context.getSharedPreferences("cashmode", Context.MODE_PRIVATE);
        initView();
        setView();
    }

    public void initView() {
        tv_duration = (TextView) this.findViewById(R.id.tv_nfc_finish_once_duration);
        tv_cash_type = (TextView) this.findViewById(R.id.tv_nfc_finish_once_cash_type);
        ll_is_user = (LinearLayout) this.findViewById(R.id.ll_nfc_finish_once_isuser);
        tv_is_user = (TextView) this.findViewById(R.id.tv_nfc_finish_once_isuser);
        ll_write_carnumber = (LinearLayout) findViewById(R.id.ll_nfc_finish_once_write_carnunber);
        tv_time = (TextView) this.findViewById(R.id.tv_nfc_finish_once_parktime);
        tv_carnumber_hint = (TextView) findViewById(R.id.tv_nfc_finish_once_carnunber_hint);
        tv_carnumber_warn = (TextView) findViewById(R.id.tv_nfc_once_carnunber_warn);
        tv_carnumber = (TextView) this.findViewById(R.id.tv_nfc_finish_once_carnunber);
        tv_final_time_money = (TextView) this.findViewById(R.id.tv_nfc_finish_once_time_collect);
        tv_carnumber_on_carnumber = (TextView) this.findViewById(R.id.tv_nfc_finish_once_carnunber_no_carnumber);
        bt_cancle = (Button) this.findViewById(R.id.bt_nfc_finish_once_cancle);
        bt_ok = (Button) this.findViewById(R.id.bt_nfc_finish_once_ok);
        view = findViewById(R.id.view_nfc_once_view);
        cb_once_cash = (CheckBox) findViewById(R.id.cb_nfc_finish_once_cash);
        cb_time_cash = (CheckBox) findViewById(R.id.cb_nfc_finish_once_timecash);
        rl_oncecash = (RelativeLayout) findViewById(R.id.rl_nfc_finish_once_cash);
        rl_timecash = (RelativeLayout) findViewById(R.id.rl_nfc_finish_once_time_cash);
        et_time_money = (EditText) findViewById(R.id.et_nfc_finish_once_time_collect);
        sp_once_money = (Spinner) findViewById(R.id.sp_nfc_finish_order_once_collect);

        if (cashmode_sp.getString("mcash", "time").equals("time")) {
            cb_time_cash.setChecked(true);
            cb_once_cash.setChecked(false);
            tv_cash_type.setText("按时段计费：");
            rl_timecash.setBackgroundColor(Color.parseColor("#D0ECDD"));
            rl_oncecash.setBackgroundColor(Color.parseColor("#ffffff"));
            if (nfcOrder.getIsedit() != null && nfcOrder.getIsedit().equals("0")) {// isedit
                // 按时价格是否可编辑
                // 0否，1是
                sp_once_money.setVisibility(View.GONE);
                et_time_money.setVisibility(View.GONE);
                tv_final_time_money.setVisibility(View.VISIBLE);
                tv_final_time_money.setText(nfcOrder.getCollect());
                final_money = nfcOrder.getCollect();
            } else {
                sp_once_money.setVisibility(View.GONE);
                tv_final_time_money.setVisibility(View.GONE);
                et_time_money.setVisibility(View.VISIBLE);
                et_time_money.setText(nfcOrder.getCollect());
            }
        } else {
            cb_once_cash.setChecked(true);
            cb_time_cash.setChecked(false);
            tv_cash_type.setText("按次计费：");
            rl_oncecash.setBackgroundColor(Color.parseColor("#D0ECDD"));
            rl_timecash.setBackgroundColor(Color.parseColor("#ffffff"));
            if (nfcOrder.getCollect1() != null && nfcOrder.getCollect1().length > 1) {
                sp_once_money.setVisibility(View.VISIBLE);
                et_time_money.setVisibility(View.GONE);
                tv_final_time_money.setVisibility(View.GONE);
                collect1 = new String[nfcOrder.getCollect1().length];
                for (int i = 0; i < nfcOrder.getCollect1().length; i++) {
                    collect1[i] = String.valueOf(nfcOrder.getCollect1()[i]);
                }
                collectAdapter = new ArrayAdapter<String>(context, R.layout.nfc_finish_dialog_spinner_item, collect1);
                sp_once_money.setAdapter(collectAdapter);
                sp_once_money.setOnItemSelectedListener(new OnceCashSpinnerSelectedListener());
            } else {
                sp_once_money.setVisibility(View.GONE);
                et_time_money.setVisibility(View.GONE);
                tv_final_time_money.setVisibility(View.VISIBLE);
                tv_final_time_money.setText(nfcOrder.getCollect0());
                final_money = nfcOrder.getCollect0();
            }
        }

        cb_time_cash.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (cb_time_cash.isChecked()) {
                    cb_time_cash.setChecked(true);
                    cb_once_cash.setChecked(false);
                    tv_cash_type.setText("按时段计费：");
                    cashmode_sp.edit().putString("mcash", "time").commit();
                    rl_timecash.setBackgroundColor(Color.parseColor("#D0ECDD"));
                    rl_oncecash.setBackgroundColor(Color.parseColor("#ffffff"));
                    if (nfcOrder.getIsedit() != null && nfcOrder.getIsedit().equals("0")) {// isedit
                        // 按时价格是否可编辑
                        // 0否，1是
                        sp_once_money.setVisibility(View.GONE);
                        et_time_money.setVisibility(View.GONE);
                        tv_final_time_money.setVisibility(View.VISIBLE);
                        tv_final_time_money.setText(nfcOrder.getCollect());
                        final_money = nfcOrder.getCollect();
                    } else {
                        sp_once_money.setVisibility(View.GONE);
                        tv_final_time_money.setVisibility(View.GONE);
                        et_time_money.setVisibility(View.VISIBLE);
                        et_time_money.setText(nfcOrder.getCollect());
                    }
                } else {
                    cb_time_cash.setChecked(true);
                }

            }
        });

        cb_once_cash.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (cb_once_cash.isChecked()) {
                    cb_once_cash.setChecked(true);
                    cb_time_cash.setChecked(false);
                    tv_cash_type.setText("按次计费：");
                    cashmode_sp.edit().putString("mcash", "once").commit();
                    rl_oncecash.setBackgroundColor(Color.parseColor("#D0ECDD"));
                    rl_timecash.setBackgroundColor(Color.parseColor("#ffffff"));
                    if (nfcOrder.getCollect1() != null && nfcOrder.getCollect1().length > 1) {
                        sp_once_money.setVisibility(View.VISIBLE);
                        et_time_money.setVisibility(View.GONE);
                        tv_final_time_money.setVisibility(View.GONE);
                        collect1 = new String[nfcOrder.getCollect1().length];
                        for (int i = 0; i < nfcOrder.getCollect1().length; i++) {
                            collect1[i] = String.valueOf(nfcOrder.getCollect1()[i]);
                        }
                        collectAdapter = new ArrayAdapter<String>(context, R.layout.nfc_finish_dialog_spinner_item, collect1);
                        sp_once_money.setAdapter(collectAdapter);
                        sp_once_money.setOnItemSelectedListener(new OnceCashSpinnerSelectedListener());
                    } else {
                        sp_once_money.setVisibility(View.GONE);
                        et_time_money.setVisibility(View.GONE);
                        tv_final_time_money.setVisibility(View.VISIBLE);
                        tv_final_time_money.setText(nfcOrder.getCollect0());
                        final_money = nfcOrder.getCollect0();
                    }
                } else {
                    cb_once_cash.setChecked(true);
                }

            }
        });
    }

    public void setView() {
        SharedPreferences spf = context.getSharedPreferences("autologin", Context.MODE_PRIVATE);
        String iscancle = spf.getString("iscancle", "1");

        if (nfcOrder.getBtime() != null && nfcOrder.getEtime() != null) {
            tv_time.setText(nfcOrder.getBtime() + "-" + nfcOrder.getEtime());
        }
        if (nfcOrder.getDuration() != null) {
            tv_duration.setText(nfcOrder.getDuration());
        }
        if (nfcOrder.getUin() != null) {
            if (nfcOrder.getUin().equals("-1")) {// 不是会员
                if (nfcOrder.getPrepay() == null || "0.0".equals(nfcOrder.getPrepay())) {// 没有预付费
                    ll_is_user.setVisibility(View.INVISIBLE);
                } else {
                    tv_is_user.setBackgroundResource(R.drawable.prepayment);
                    ll_is_user.setVisibility(View.VISIBLE);
                }
            } else {
                if (nfcOrder.getPrepay() == null || "0.0".equals(nfcOrder.getPrepay())) {
                    tv_is_user.setBackgroundResource(R.drawable.finish_order_vip);
                } else {
                    tv_is_user.setBackgroundResource(R.drawable.prepayment);
                }
                ll_is_user.setVisibility(View.VISIBLE);
            }
        }

        // NFC--->结算订单返回的数据解析：NfcOrder [total=0.75, etime=19:42, btime=19:39,
        // orderid=176623, collect=0.5, discount=0.25, duration=3分钟, uin=-1,
        // carnumber=车牌号未知]

        if (nfcOrder.getCarnumber() != null) {
            MyLog.i("--->>>", "" + "显示结算的车牌号是：" + nfcOrder.getCarnumber());
            boolean isMatched = CheckUtils.CarChecked(nfcOrder.getCarnumber());
            if (!isMatched) {
                tv_carnumber_hint.setVisibility(View.GONE);
                tv_carnumber.setVisibility(View.GONE);
                tv_carnumber_on_carnumber.setVisibility(View.VISIBLE);
                ll_write_carnumber.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO 点击添加车牌号；
                        if (nfcOrder.getIsedit() != null && nfcOrder.getIsedit().equals("1") && cb_time_cash.isChecked()) {
                            final_money = et_time_money.getText().toString().trim();
                        }
                        nfcOrder.setCollect(final_money);
                        Intent intent = new Intent(context, InputCarNumberActivity.class);
                        intent.putExtra("add", "cashOrder");
                        intent.putExtra("nfcorder", nfcOrder);
                        context.startActivity(intent);
//						LeaveActivity activity = (LeaveActivity) context;
//						activity.setNfcChenge("finish");
                        NfcFinishOrderOnceDialog.this.dismiss();
                    }
                });
            } else {
                tv_carnumber_hint.setVisibility(View.VISIBLE);
                tv_carnumber.setVisibility(View.VISIBLE);
                tv_carnumber_on_carnumber.setVisibility(View.GONE);
                if (nfcOrder.getCards() == null || nfcOrder.getCards().length == 0) {
                    tv_carnumber_warn.setText(Html.fromHtml("此卡未绑定车牌"));
                    tv_carnumber_hint.setText("普通卡");
                    tv_carnumber.setText("车牌未知");
                } else if (nfcOrder.getCards() != null && nfcOrder.getCards().length == 1) {
                    tv_carnumber_warn.setText(Html.fromHtml("该车主有 <font color='#36A56A'><big>" + nfcOrder.getCards().length
                            + "</big></font> 张车牌"));
                    tv_carnumber_hint.setText("速通卡会员");
                    tv_carnumber.setText(nfcOrder.getCards()[0]);
                } else {
                    tv_carnumber_warn.setText(Html.fromHtml("该车主有 <font color='#36A56A'><big>" + nfcOrder.getCards().length
                            + "</big></font> 张车牌"));
                    tv_carnumber_hint.setText("点击切换车牌");
                    tv_carnumber.setText(nfcOrder.getCards()[0]);
                    tv_carnumber_hint.setOnClickListener(new TextView.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int i = 0; i < nfcOrder.getCards().length; i++) {
                                if (tv_carnumber.getText().toString().equals(nfcOrder.getCards()[i])) {
                                    if (i == nfcOrder.getCards().length - 1) {
                                        tv_carnumber.setText(nfcOrder.getCards()[0]);
                                        cPosition = 0;
                                    } else {
                                        tv_carnumber.setText(nfcOrder.getCards()[i + 1]);
                                        cPosition = i + 1;
                                    }
                                    return;
                                }
                            }
                        }
                    });
                }

            }
        } else {
            tv_carnumber_hint.setVisibility(View.GONE);
            tv_carnumber.setVisibility(View.GONE);
            tv_carnumber_on_carnumber.setVisibility(View.VISIBLE);
            ll_write_carnumber.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO 点击添加车牌号；
                    if (nfcOrder.getIsedit() != null && nfcOrder.getIsedit().equals("1") && cb_time_cash.isChecked()) {
                        final_money = et_time_money.getText().toString().trim();
                    }
                    nfcOrder.setCollect(final_money);
                    Intent intent = new Intent(context, InputCarNumberActivity.class);
                    intent.putExtra("add", "cashOrder");
                    intent.putExtra("nfcorder", nfcOrder);
                    context.startActivity(intent);
//					LeaveActivity activity = (LeaveActivity) context;
//					activity.setNfcChenge("finish");
                    NfcFinishOrderOnceDialog.this.dismiss();
                }
            });
        }
        if (iscancle.equals("1")) {
            view.setVisibility(View.GONE);
            bt_cancle.setVisibility(View.GONE);
            bt_ok.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String carNumber;
                    if (nfcOrder.getCards() == null) {
                        carNumber = "";
                    } else {
                        if (nfcOrder.getCards().length >= 1) {
                            carNumber = nfcOrder.getCards()[cPosition];
                        } else {
                            carNumber = "";
                        }
                    }
                    if (nfcOrder.getIsedit() != null && nfcOrder.getIsedit().equals("1") && cb_time_cash.isChecked()) {
                        final_money = et_time_money.getText().toString().trim();
                    }
                    if (nfcOrder.getPrepay() == null || "0.0".equals(nfcOrder.getPrepay())) {
                        finishOrder.submitCash(final_money, NfcFinishOrderOnceDialog.this, "0", carNumber);
                    } else {
                        finishOrder.cashPrepayOrder(final_money, NfcFinishOrderOnceDialog.this);
                    }
                }
            });
        } else {
            view.setVisibility(View.VISIBLE);
            bt_cancle.setVisibility(View.VISIBLE);
            bt_ok.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String carNumber;
                    if (nfcOrder.getCards() == null) {
                        carNumber = "";
                    } else {
                        if (nfcOrder.getCards().length >= 1) {
                            carNumber = nfcOrder.getCards()[cPosition];
                        } else {
                            carNumber = "";
                        }
                    }
                    if (nfcOrder.getIsedit() != null && nfcOrder.getIsedit().equals("1") && cb_time_cash.isChecked()) {
                        final_money = et_time_money.getText().toString().trim();
                    }
                    if (nfcOrder.getPrepay() == null || "0.0".equals(nfcOrder.getPrepay())) {
                        finishOrder.submitCash(final_money, NfcFinishOrderOnceDialog.this, "0", carNumber);
                    } else {
                        finishOrder.cashPrepayOrder(final_money, NfcFinishOrderOnceDialog.this);
                    }
                }
            });
            bt_cancle.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    NfcFinishOrderOnceDialog.this.dismiss();

                }
            });
        }

    }

    class OnceCashSpinnerSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

            final_money = collect1[arg2];
            MyLog.i("CarNumberOutOnceDialog", "按次计费选择的价格是：" + collect1[arg2]);
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

}
