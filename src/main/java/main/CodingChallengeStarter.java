package main;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import codingChallenge.Event;
import utility.files.CodingChallengeUtility;
import utility.files.DatabaseUtility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.security.InvalidParameterException;
import java.util.ArrayList;

import org.apache.log4j.BasicConfigurator;

public class CodingChallengeStarter {

	public static void main(String[] args) {
		Logger logger = Logger.getLogger("CodingChallengeStarter.class");
		BasicConfigurator.configure();
		logger.info("CodingChallengeStarter");
		try {
			if (args.length != 1 || args[0].isEmpty()) {
				throw new InvalidParameterException("Please provide a single log file filePath argument");
			}
			ArrayList<JSONObject> json = new ArrayList<JSONObject>();
			String filePath = args[0];
			String line = null;
			JSONObject obj;

			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(filePath);
			DatabaseUtility db = new DatabaseUtility();
			CodingChallengeUtility utility = new CodingChallengeUtility();

			// wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while ((line = bufferedReader.readLine()) != null) {
				obj = (JSONObject) new JSONParser().parse(line);
				json.add(obj);
				String id = (String) obj.get("id");
				String state = (String) obj.get("state");
				long timestamp = (long) obj.get("timestamp");
				String host = (String) obj.get("host");

				Event event = new Event(id, timestamp, state, host, false);
				// db.saveEvent(event);

				logger.info("ID : " + (String) obj.get("id") + ", STATE :" + (String) obj.get("state") + " type : "
						+ (String) obj.get("type") + " host : " + (String) obj.get("host") + " timestamp : "
						+ (Long) obj.get("timestamp"));
			}
			if(json!=null)
			utility.groupByState(json);

			// close files.
			bufferedReader.close();
		} catch (Exception e) {
			logger.fatal("Exception occured in application starter : " + e);
		}

		logger.info("--------Finished------");
	}
}
