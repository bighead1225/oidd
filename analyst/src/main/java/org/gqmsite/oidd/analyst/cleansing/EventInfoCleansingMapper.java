package org.gqmsite.oidd.analyst.cleansing;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class EventInfoCleansingMapper extends
		Mapper<LongWritable, Text, Text, EventInfo> {

	private EventInfo info = new EventInfo();

	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		StringTokenizer itr = new StringTokenizer(value.toString(), ",");
		String temp;
		int index = 0;

		while (itr.hasMoreElements()) {
			temp = itr.nextToken();

			if (index == 0) {
				info.getCell().set(temp);
			} else if (index == 1) {
				if (StringUtils.isNumeric(temp)) {
					info.getSector().set(Integer.parseInt(temp));
				} else {
					// maybe add a counter to count the invalid elements
					break;
				}
			} else if (index == 4) {
				if (temp.length() >= 4) {
					info.getMdn().set(temp);
				} else {
					// maybe add a counter to count the invalid elements
					break;
				}
			} else if (index == 6) {
				info.getTrackTime().set(temp);
			} else if (index == 7) {
				if (StringUtils.isNumeric(temp)) {
					info.getEvent().set(Integer.parseInt(temp));
				} else {
					// maybe add a counter to count the invalid elements
					break;
				}
				context.write(info.getMdn(), info);
			}

			index++;
		}
	}

}
