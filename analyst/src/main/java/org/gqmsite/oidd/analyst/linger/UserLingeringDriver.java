package org.gqmsite.oidd.analyst.linger;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.compress.SnappyCodec;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MapFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.gqmsite.oidd.analyst.io.LingerInfo;

public class UserLingeringDriver extends Configured implements Tool {

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new UserLingeringDriver(), args);
		System.exit(exitCode);
	}

	@Override
	public int run(String[] args) throws Exception {
		if (args.length != 2) {
			System.out.printf("Usage %s [generic options] <in> <out>\n",
					getClass().getName());
			ToolRunner.printGenericCommandUsage(System.out);
			return -1;
		}

		Job job = Job.getInstance(getConf());
		job.setJobName("User Linger Time Estimate");
		job.setJarByClass(getClass());

		job.setMapperClass(UserLingeringMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(LingerInfo.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LingerInfo.class);

		job.setInputFormatClass(SequenceFileInputFormat.class);
		LazyOutputFormat.setOutputFormatClass(job, MapFileOutputFormat.class);

		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		// set compress option
		SequenceFileOutputFormat.setOutputCompressionType(job,
				CompressionType.BLOCK);
		FileOutputFormat.setCompressOutput(job, true);
		FileOutputFormat.setOutputCompressorClass(job, SnappyCodec.class);

		return job.waitForCompletion(true) ? 0 : 1;
	}

}