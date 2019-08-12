package dto;

import java.util.Objects;

public class EventDto {
	private String id;
	private String state;
	private String type;
	private String host;
	private Long timestamp;

	public String getId() {
		return id;
	}

	public String getState() {
		return state;
	}

	public String getType() {
		return type;
	}

	public String getHost() {
		return host;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public enum State {
		STARTED, FINISHED
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		EventDto eventDto = (EventDto) o;
		return Objects.equals(id, eventDto.id) && state == eventDto.state && Objects.equals(type, eventDto.type)
				&& Objects.equals(host, eventDto.host) && Objects.equals(timestamp, eventDto.timestamp);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, state, type, host, timestamp);
	}

	@Override
	public String toString() {
		return "EventDto{" + "id='" + id + '\'' + ", state=" + state + ", type='" + type + '\'' + ", host='" + host
				+ '\'' + ", timestamp=" + timestamp + '}';
	}
}