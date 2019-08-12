package utility.files;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import codingChallenge.Event;
import dto.EventDto;

public class CodingChallengeUtility {
	Logger logger = Logger.getLogger("CodingChallengeUtility.class");
	private ObjectMapper objectMapper;
	private Connection connection;
	private Map<String, EventDto> startedMap = new ConcurrentHashMap<>();
	private Map<String, EventDto> finishedMap = new ConcurrentHashMap<>();

	public Event alertEvent(EventDto startTimeStamp, EventDto finishTimeStamp) {
		Long duration = finishTimeStamp.getTimestamp() - startTimeStamp.getTimestamp();
		boolean isAlert = duration > 4;
		return new Event(startTimeStamp.getId(), duration, startTimeStamp.getType(), startTimeStamp.getHost(), isAlert);
	}

	public void groupByState(ArrayList<JSONObject> json) {
		try {
			logger.info("Convert JSON to EventDto");

			for (JSONObject jsonObject : json) {
				String jsonStr = jsonObject.toString();
				EventDto eventDTO = Optional.ofNullable(objectMapper.readValue(jsonStr, EventDto.class))
						.orElseThrow(() -> new NullPointerException("Failed to convert json to EventDTO"));
				logger.info("Create STARTED and FINISHED events map");
				if (eventDTO.getState().equals("STARTED")) {
					startedMap.put(eventDTO.getId(), eventDTO);
				} else {
					finishedMap.put(eventDTO.getId(), eventDTO);
				}
			}

			processData(startedMap.keySet());
		} catch (IOException e) {
			logger.fatal("Error processing json :" + json);
		}
	}

	public void processData(Set<String> ids) {
		try {
			DatabaseUtility db = new DatabaseUtility();
			for (String id : ids) {
				EventDto startEvent = startedMap.get(id);
				EventDto finishEvent = finishedMap.get(id);
				if (startEvent != null && finishEvent != null) {

					Event event = alertEvent(startEvent, finishEvent);

					logger.info("Saving -" + event.toString());
					db.saveEvent(event);
				} else {
					logger.fatal("not complete event" + id);
				}
			}
		} catch (Exception e) {
			logger.fatal("processData exception" + e);
		}
	}
}