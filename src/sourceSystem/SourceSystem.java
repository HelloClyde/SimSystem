package sourceSystem;

import java.util.ArrayList;

public class SourceSystem {
	int[] SourceTotal;
	ArrayList<Process> ProcessList = new ArrayList<Process>();
	
	public int GetSourceNum(){
		return this.SourceTotal.length;
	}
	
	static public void main(String[] args){
		SourceSystem MainSourceSystem = new SourceSystem(new int[]{3,3,2});
		/*
		 * 7 5 3       0 1 0
		 * 3 2 2       2 0 0
		 * 9 0 2       3 0 2
		 * 2 2 2       2 1 1
		 * 4 3 3       0 0 2
		 */
		MainSourceSystem.AddProcess(new Process("P0",new int[]{7,5,3},new int[]{0,1,0}));
		MainSourceSystem.AddProcess(new Process("P1",new int[]{3,2,2},new int[]{2,0,0}));
		MainSourceSystem.AddProcess(new Process("P2",new int[]{9,0,2},new int[]{3,0,2}));
		MainSourceSystem.AddProcess(new Process("P3",new int[]{2,2,2},new int[]{2,1,1}));
		MainSourceSystem.AddProcess(new Process("P4",new int[]{4,3,3},new int[]{0,0,2}));
		ArrayList<Process> DesProcessList = MainSourceSystem.GetSafeSeries();
		for (Process TempProcess:DesProcessList){
			System.out.println(TempProcess.Name);
		}
		System.out.println(MainSourceSystem.IsQuest("P1", new int[]{1,0,2}));
	}
	
	public void ShowSafeSeries(){
		ArrayList<Process> DesProcessList = this.GetSafeSeries();
		if (DesProcessList == null){
			System.out.println("不安全。");
		}
		else{
			for (Process TempProcess:DesProcessList){
				System.out.println(TempProcess.Name);
			}
		}
	}
	
	public SourceSystem(int[] SrcSourceTotal){
		this.SourceTotal = SrcSourceTotal;
	}
	
	public void AddProcess(Process SrcProcess){
		this.ProcessList.add(SrcProcess);
	}
	
	public void AddProcess(String Name,int[] total,int[] exist){
		this.ProcessList.add(new Process(Name,total,exist));
	}
	
	public ArrayList<Process> GetSafeSeries(){
		ArrayList<Process> SafeSeries = new ArrayList<Process>();
		//复制
		int[] SourceNow = new int[this.SourceTotal.length];
		for (int i = 0;i < this.SourceTotal.length;i ++){
			SourceNow[i] = this.SourceTotal[i];
		}
		ArrayList<Process> ProcessNow = new ArrayList<Process>();
		for (Process TempProcess:this.ProcessList){
			ProcessNow.add(TempProcess);
		}
		while (!ProcessNow.isEmpty()){
			//搜索一个能满足的进程
			Process SrcProcess = null;
			for (Process TempProcess:ProcessNow){
				//每个资源还需要的都少于等于还有的
				boolean IsSatify = true;
				for (int i = 0;i < TempProcess.NeedSource.length;i ++){
					if (TempProcess.NeedSource[i] - TempProcess.ExistSource[i] > SourceNow[i]){
						IsSatify =false;
						break;
					}
				}
				if (IsSatify){
					SrcProcess = TempProcess;
					break;
				}
			}
			//未搜索到则表示不安全
			if (SrcProcess == null){
				return null;
			}
			else{
				SafeSeries.add(SrcProcess);
				ProcessNow.remove(SrcProcess);
				//资源释放
				for (int i = 0;i < SourceNow.length;i ++){
					SourceNow[i] += SrcProcess.ExistSource[i];
				}
			}
		}
		return SafeSeries;
	}
	
	public boolean IsQuest(String Name,int[] ask){
		for (int i = 0;i < ask.length;i ++){
			if (ask[i] > this.SourceTotal[i]){
				return false;
			}
		}
		boolean Result;
		Process DesProcess = null;
		for (Process TempProcess:this.ProcessList){
			if (TempProcess.Name.equals(Name)){
				DesProcess = TempProcess;
				break;
			}
		}
		if (DesProcess == null){
			System.out.println("未找到该进程。");
			return false;
		}
		Process BakProcess = new Process(DesProcess);
		for (int i = 0;i < ask.length;i ++){
			DesProcess.ExistSource[i] += ask[i];
		}
		if (this.GetSafeSeries() == null){
			return false;
		}
		else{
			return true;
		}
	}
}
