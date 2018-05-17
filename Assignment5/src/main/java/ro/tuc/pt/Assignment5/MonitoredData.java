package ro.tuc.pt.Assignment5;

public class MonitoredData {

	private String startTime;
	private String endTime;
	private String activity;

	public MonitoredData(String st, String end, String act) {
		this.activity = act;
		this.endTime = end;
		this.startTime = st;
	}
	
	public int getStartingDay() {
		return Integer.parseInt(startTime.substring(8, 10));
	}

	public int getEndingDay() {
		return Integer.parseInt(endTime.substring(8, 10));
	}

	public String getActivity() {
		return this.activity;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

}
