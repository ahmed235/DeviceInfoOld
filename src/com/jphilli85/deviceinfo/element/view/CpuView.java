package com.jphilli85.deviceinfo.element.view;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.jphilli85.deviceinfo.app.DeviceInfo;
import com.jphilli85.deviceinfo.element.Cpu;
import com.jphilli85.deviceinfo.element.Cpu.CpuStat;
import com.jphilli85.deviceinfo.element.Cpu.LogicalCpu;
import com.jphilli85.deviceinfo.element.Element;


public class CpuView extends ListeningElementView implements Cpu.Callback {
	private Cpu mCpu;
	private final Section mCpuInfoSection;
	private int mCores;
	
	private TextView[] 
			// LogicalCpu
			mFrequency, mGovernor,
			mTransitions, mTransitionTime,
			mFrequencyTime,
			
			// CpuStat
			mUserPercent, mNicePercent, mSystemPercent,
			mIdlePercent, mIoWaitPercent, mIntrPercent,
			mSoftIrqPercent,
			mUserTotal, mSystemTotal,
			mIdleTotal, mTotal;
	

	public CpuView(Context context) {
		super(context);
		
		
		mCpuInfoSection = new Section(getContext(), "CPU Info");
	}
	
	private TableSection getCpuStatTable(int index) {
		TableSection table = new TableSection(getContext()) ;
		
		mUserPercent[index] = table.getValueTextView();
		mNicePercent[index] = table.getValueTextView();
		mSystemPercent[index] = table.getValueTextView();
		mIdlePercent[index] = table.getValueTextView();
		mIoWaitPercent[index] = table.getValueTextView();
		mIntrPercent[index] = table.getValueTextView();
		mSoftIrqPercent[index] = table.getValueTextView();
		mUserTotal[index] = table.getValueTextView();
		mSystemTotal[index] = table.getValueTextView();
		mIdleTotal[index] = table.getValueTextView();
		mTotal[index] = table.getValueTextView();
				
		table.add("Total (%)", mTotal[index]);
		table.add("User Total (%)", mUserTotal[index]);
		table.add("System Total (%)", mSystemTotal[index]);
		table.add("Idle Total (%)", mIdleTotal[index]);
		table.add("User (%)", mUserPercent[index]);
		table.add("Nice (%)", mNicePercent[index]);
		table.add("System (%)", mSystemPercent[index]);
		table.add("Idle (%)", mIdlePercent[index]);
		table.add("I/O Wait (%)", mIoWaitPercent[index]);
		table.add("Intr (%)", mIntrPercent[index]);
		table.add("Soft IRQ (%)", mSoftIrqPercent[index]);
		
		return table;
	}
	
	private TableSection getCpuInfoTable() {
		TableSection table = new TableSection(getContext()) ;
		List<String> list = mCpu.getCpuinfo();
		if (list == null || list.isEmpty()) {
			table.add("", "CPU Info not available");
			return table;
		}
		String[] parts;
		for (String s : list) {
			parts = s.split(":", 2);
			if (parts == null || parts.length != 2) continue;
			table.add(parts[0].trim(), parts[1].trim());
		}
		return table;
	}
	
	private String getFrequencyDistribution(LogicalCpu cpu) {
		Map<Integer, Float> percents = cpu.getPercentInFrequency();
		if (percents == null || percents.isEmpty()) return null;
		String result = "";
		for (Entry<Integer, Float> e : percents.entrySet()) {
			result += e.getKey() + ", " + e.getValue() + "\n";
		}
		return result;
	}
	
	private String getTimeInTransitions(LogicalCpu cpu) {
		return DeviceInfo.getDuration(cpu.getTimeInTransitions());
	}

	@Override
	public Element getElement() {
		return mCpu;
	}

	@Override
	public void onUpdated() {
		mCpuInfoSection.getContent().removeAllViews();
		mCpuInfoSection.add(getCpuInfoTable());
		LogicalCpu cpu;
		CpuStat stat;
		
		setCpuStatText(mCpu.getCpuStat(), mCores);
		
		for (int i = 0; i < mCores; ++i) {						
			cpu = mCpu.getLogicalCpus().get(i);			
			stat = cpu.getCpuStat();
			
			mFrequency[i].setText(String.valueOf(cpu.getFrequency()));
			mFrequencyTime[i].setText(getFrequencyDistribution(cpu));
			mGovernor[i].setText(cpu.getGovernor());
			mTransitionTime[i].setText(getTimeInTransitions(cpu));
			mTransitions[i].setText(String.valueOf(cpu.getTotalTransitions()));
			
			setCpuStatText(stat, i);
		}		
	}
	
	private void setCpuStatText(CpuStat stat, int index) {
		mUserPercent[index].setText(String.valueOf((stat.getUserPercent())));
		mNicePercent[index].setText(String.valueOf((stat.getNicePercent())));
		mSystemPercent[index].setText(String.valueOf((stat.getSystemPercent())));	
		mIdlePercent[index].setText(String.valueOf((stat.getIdlePercent())));
		mIoWaitPercent[index].setText(String.valueOf((stat.getIoWaitPercent())));
		mIntrPercent[index].setText(String.valueOf((stat.getIntrPercent())));
		mSoftIrqPercent[index].setText(String.valueOf((stat.getSoftIrqPercent())));
		mUserTotal[index].setText(String.valueOf((stat.getUserTotalPercent())));
		mSystemTotal[index].setText(String.valueOf((stat.getSystemTotalPercent())));
		mIdleTotal[index].setText(String.valueOf((stat.getIdleTotalPercent())));
		mTotal[index].setText(String.valueOf((stat.getTotalPercent())));
	}
	@Override
	protected void initialize(Context context) {
		mCpu = new Cpu(context);
		mCpu.setCallback(this);
		
		mCores = mCpu.getLogicalCpus().size();	
		
		mFrequency = new TextView[mCores];
		mTransitions = new TextView[mCores];
		mGovernor = new TextView[mCores];
		mTransitionTime = new TextView[mCores];
		mFrequencyTime = new TextView[mCores];
		
		int stats = mCores + 1;
		mUserPercent = new TextView[stats]; 
		mNicePercent = new TextView[stats]; 
		mSystemPercent = new TextView[stats];
		mIdlePercent = new TextView[stats];
		mIoWaitPercent = new TextView[stats]; 
		mIntrPercent = new TextView[stats];
		mSoftIrqPercent = new TextView[stats];
		mUserTotal = new TextView[stats];
		mSystemTotal = new TextView[stats];
		mIdleTotal = new TextView[stats]; 
		mTotal = new TextView[stats];
		
		TableSection table = new TableSection(getContext()) ;
		Section overallStatSection;
		Section logicalCpuSection;
		Subsection statSubsection;	
		
		table.add("Number of Logical CPUs (cores)", String.valueOf(mCores));
		add(table);
		
		overallStatSection = new Section(getContext(), "Overall CPU Stat");
		overallStatSection.add(getCpuStatTable(mCores));
		add(overallStatSection);
		
		LogicalCpu cpu;		
		for (int i = 0; i < mCores; ++i) {						
				cpu = mCpu.getLogicalCpus().get(i);
				
				mFrequency[i] = table.getValueTextView();
				mTransitions[i] = table.getValueTextView();
				mGovernor[i] = table.getValueTextView();
				mTransitionTime[i] = table.getValueTextView();
				mFrequencyTime[i] = table.getValueTextView();
				
				logicalCpuSection = new Section(getContext(), "Logical CPU " + (i + 1));
				table = new TableSection(getContext()) ;
				table.add("Frequency (MHz)", mFrequency[i]);
				table.add("Frequency Distribution (MHz, %)", mFrequencyTime[i]);
				table.add("Governor", mGovernor[i]);
				table.add("Transitions", mTransitions[i]);
				table.add("Time in Transitions", mTransitionTime[i]);
				
				table.add("Transition Latency (ns)", String.valueOf(cpu.getTransitionLatency()));
				String[] govs = cpu.getAvailableGovernors();
				if (govs != null) table.add("Available Governors", TextUtils.join(", ", govs));
				table.add("Driver", cpu.getDriver());
				logicalCpuSection.add(table);
				
				statSubsection = new Subsection(getContext(), "CPU Stat");			
				statSubsection.add(getCpuStatTable(i));
				
				logicalCpuSection.add(statSubsection);
				
				add(logicalCpuSection);			
		}

		add(mCpuInfoSection);
		
		onUpdated();
	}
}
