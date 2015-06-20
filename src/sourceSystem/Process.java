package sourceSystem;

public class Process {
	String Name;
	int[] ExistSource;
	int[] NeedSource;
	
	public Process(String SrcName,int[] SrcNeedSource,int[] SrcExistSource){
		this.Name = SrcName;
		this.ExistSource = SrcExistSource;
		this.NeedSource = SrcNeedSource;
	}
	
	public Process(Process SrcProcess){
		this.Name = String.valueOf(SrcProcess.Name);
		this.ExistSource = new int[SrcProcess.ExistSource.length];
		for (int i = 0;i < SrcProcess.ExistSource.length;i ++){
			this.ExistSource[i] = SrcProcess.ExistSource[i];
		}
		this.NeedSource  = new int[SrcProcess.NeedSource.length];
		for (int i = 0;i < SrcProcess.NeedSource.length;i ++){
			this.NeedSource[i] = SrcProcess.NeedSource[i];
		}
	}
}
