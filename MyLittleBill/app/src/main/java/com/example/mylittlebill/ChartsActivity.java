package com.example.mylittlebill;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.Chart;
import lecho.lib.hellocharts.view.LineChartView;

public class ChartsActivity extends Activity {

    private LineChartView mChart;
    private Map<String,Integer> table=new TreeMap<>();
    private LineChartData mData;
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_view);
        mChart=(LineChartView) findViewById(R.id.chart);
        mData=new LineChartData();
        List<CostBean> allDate= (List<CostBean>) getIntent().getSerializableExtra("cost_list");
        getAxisXLables(allDate);//获取x轴的标注
        generateValues(allDate);
        generateData();
    }

    private void generateData() {
        List<Line> lines=new ArrayList<>();
        List<PointValue> values=new ArrayList<>();
        int indexX=0;
        for (Integer value:table.values()){
            values.add(new PointValue(indexX,value));
            indexX++;
        }
        Line line=new Line(values);
        line.setColor(ChartUtils.COLORS[0]);
        line.setShape(ValueShape.CIRCLE);
        line.setPointColor(ChartUtils.COLORS[1]);
        lines.add(line);
        mData =new LineChartData(lines);
        mData.setLines(lines);
        mChart.setLineChartData(mData);

        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(true);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.GRAY);  //设置字体颜色
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        mData.setAxisXBottom(axisX); //x 轴在底部
        Axis axisY = new Axis();  //Y轴
        axisY.setName("");//y轴标注
        axisY.setTextSize(10);//设置字体大小
        mData.setAxisYLeft(axisY);  //Y轴设置在左边
    }

    private void getAxisXLables(List<CostBean> allDate) {
        int flag = 0;
        for (int i = 0; i < allDate.size(); i++) {
            for (int j = 0 ; j < i - 1 ; j++){
                    if(allDate.get(i) == allDate.get(j)){
                        flag = 1;
                        break;
                    }else {
                        continue;
                    }
            }
            if (flag == 0){
                CostBean costBean=allDate.get(i);
                String costDate=costBean.costDate;
                mAxisXValues.add(new AxisValue(i).setLabel(costDate));
            }
        }
    }


    private void generateValues(List<CostBean> allDate) {
        if(allDate!=null){
            for(int i=0;i<allDate.size();i++){
                CostBean costBean=allDate.get(i);
                String costDate=costBean.costDate;
                int costMoney=Integer.parseInt(costBean.costMoney);
                if (!table.containsKey(costDate)){
                    table.put(costDate,costMoney);
                }else{
                    int originMoney=table.get(costDate);
                    table.put(costDate,originMoney+costMoney);
                }
            }
        }
    }
}
