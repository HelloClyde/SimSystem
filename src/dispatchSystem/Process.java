package dispatchSystem;

public class Process {
	String ProcessName;
	//到达时间
	int ArrivalTime;
	//服务时间
	int BurstTime;
	//结束时间
	int FinishedTime;
	//已经运行时间
	int RunnedTime;
	
	public Process(String Name,int ArrTime,int BurTime){
		this.ProcessName = Name;
		this.ArrivalTime = ArrTime;
		this.BurstTime = BurTime;
	}

}
