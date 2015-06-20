package ProcessSystem;

public class Process {
	String name;
	String id;
	
	public Process(String srcName,String srcId){
		name = String.valueOf(srcName);
		id = srcId;
	}
	
	public void Destory(){
		
	}
	
	public String GetName(){
		return name;
	}
	
	public String GetPid(){
		return id;
	}

}
