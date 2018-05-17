package ro.tuc.pt.Assignment5;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.text.DateFormatter;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Controller {

	private List<MonitoredData> monitoredData;
	private List<Integer> days;

	private List<Integer> distinctDays;
	private List<String> activities;
	private List<String> distinctActivities;
	private Map<String, Integer> activitiesTable = new HashMap<String, Integer>();
	private Map<Integer, HashMap<String, Integer>> dailyMap = new HashMap<Integer, HashMap<String, Integer>>();
	private Map<String, DateTime> timeMap = new HashMap<String, DateTime>();

	private int noDays;

	public Controller() {
		this.monitoredData = readData();

		days = getDays();
		activities = this.getSomething(monitoredData, m -> m.getActivity());
		distinctActivities = this.getSomething(monitoredData, m -> m.getActivity()).stream().distinct().collect(Collectors.toList());

		firstFunction();
		secondFunction();
		thirdFunction();
		fourthFunction();
		fifthFunction();
	}

	public List<MonitoredData> readData() {
		String filePath = "Activities.txt";
		List<Object> list = new ArrayList<Object>();
		List<MonitoredData> data = new ArrayList<MonitoredData>();
		Stream<String> stream;

		try {
			stream = Files.lines(Paths.get(filePath));
			list = stream.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<String> data1 = new ArrayList<String>();
		for (Object o : list) {
			data1.add(o.toString());
		}

		List<String[]> parts = new ArrayList<String[]>();
		for (String s : data1) {
			parts.add(s.split("		"));
		}

		for (String[] s : parts) {
			data.add(new MonitoredData(s[0], s[1], s[2]));
		}

		return data;

	}

	public void firstFunction() {

		noDays = (int) days.stream().distinct().count();
		System.out.println(noDays);
	}

	public void secondFunction() {

		distinctActivities.stream()
				.forEach(act -> activitiesTable.put(act, (int) activities.stream().filter(a -> a.equals(act)).count()));

		File file = new File("FrecventaActivitati.txt");
		BufferedWriter output = null;
		try {
			output = new BufferedWriter(new FileWriter(file));
			output.flush();
			Set<String> set = activitiesTable.keySet();
			for (String s : set) {
				System.out.println(s + " " + activitiesTable.get(s));
				output.write(s + "	" + activitiesTable.get(s));
				output.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void thirdFunction() {
		days.stream().distinct().forEach(d -> dailyMap.put(d, this.getDistinctActivities(
				getSomething(Controller.filter(monitoredData, m -> m.getStartingDay() == d), m -> m.getActivity()))));

		File file = new File("FrecventaPeZile.txt");
		BufferedWriter output = null;
		try {
			output = new BufferedWriter(new FileWriter(file));
			output.flush();
			Set<Integer> set = dailyMap.keySet();
			for (Integer s : set) {

				output.write(s.toString());
				output.newLine();
				Set<String> set1 = dailyMap.get(s).keySet();
				for (String s1 : set1) {
					output.write("	" + s1 + "	" + dailyMap.get(s).get(s1));
					output.newLine();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void fourthFunction() {
		distinctActivities.stream().forEach(act -> timeMap.put(act, getActivityDuration(
				monitoredData.stream().filter(m -> m.getActivity().equals(act)).collect(Collectors.toList()))));

		File file = new File("DurataActivitatilor.txt");
		BufferedWriter output = null;
		try {
			output = new BufferedWriter(new FileWriter(file));
			output.flush();
			Set<String> set = timeMap.keySet();
			for (String s : set) {
				// System.out.println(s + " " + activitiesTable.get(s));
				DateTime time = timeMap.get(s);
				if (time != null) {
					Time t = new Time(time.getMillis());
					output.write(s + "	" + t);
					output.newLine();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void fifthFunction() {
		List<String> listaa = distinctActivities.stream().filter(act -> getAverageDuration(act) >= 0.9)
				.collect(Collectors.toList());

		File file = new File("ActivitatiCuTimp5.txt");
		BufferedWriter output = null;
		try {
			output = new BufferedWriter(new FileWriter(file));
			output.flush();

			for (String s : listaa) {
				output.write(s);
				output.newLine();

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public List<Integer> getDays() {
		List<Integer> days = new ArrayList();
		for (MonitoredData data : monitoredData) {
			days.add(data.getStartingDay());
		}
		return days;
	}

	public static <T> List<T> filter(List<T> list, Predicate<? super T> predicate) {
		List<T> filteredList = new ArrayList<>();
		for (T item : list) {
			if (predicate.test(item)) {
				filteredList.add(item);
			}
		}
		return filteredList;
	}

	public List<String> getSomething(List<MonitoredData> monData, Function<MonitoredData, String> mapper) {
		List<String> filteredList = new ArrayList<>();
		for (MonitoredData item : monData) {
			filteredList.add(mapper.apply(item));
		}
		return filteredList;
	}

	public HashMap<String, Integer> getDistinctActivities(List<String> activities) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		activities.stream().forEach(act -> map.put(act, (int) activities.stream().filter(a -> a.equals(act)).count()));
		return map;
	}

	public DateTime getActivityDuration(List<MonitoredData> monData) {
		long time = 0;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (MonitoredData mon : monData) {
			try {
				Date date1 = formatter.parse(mon.getStartTime());
				Date date2 = formatter.parse(mon.getEndTime());
				time += date2.getTime() - date1.getTime();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		Period period = new Period(new DateTime(0), new DateTime(time), PeriodType.time());

		if (period.getHours() <= 10) {
			DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm:ss");

			DateTime tt = dtf.parseDateTime(new Time(time).toString());
			return tt;
		} else {
			return null;
		}

	}

	@SuppressWarnings("deprecation")
	public float getAverageDuration(String activity) {
		List<MonitoredData> monData = monitoredData.stream().filter(m -> m.getActivity().equals(activity))
				.collect(Collectors.toList());
		long total = monData.stream().distinct().count();
		long appLessThen5 = 0;
		long time = 0;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (MonitoredData mon : monData) {
			try {
				Date date1 = formatter.parse(mon.getStartTime());
				Date date2 = formatter.parse(mon.getEndTime());
				time = date2.getTime() - date1.getTime();
				if ((new Time(time)).getMinutes() <= 5) {
					appLessThen5++;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		System.out.println((float) appLessThen5 / total + " " + activity);
		return (float) appLessThen5 / total;

	}

}
