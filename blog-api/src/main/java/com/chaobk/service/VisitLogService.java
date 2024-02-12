package com.chaobk.service;

import com.chaobk.model.dto.VisitLogUuidTime;
import org.springframework.scheduling.annotation.Async;
import com.chaobk.entity.VisitLog;

import java.util.List;

public interface VisitLogService {
	List<VisitLog> getVisitLogListByUUIDAndDate(String uuid, String startDate, String endDate);

	List<VisitLogUuidTime> getUUIDAndCreateTimeByYesterday();

	@Async
	void saveVisitLog(VisitLog log);

	void deleteVisitLogById(Long id);
}
